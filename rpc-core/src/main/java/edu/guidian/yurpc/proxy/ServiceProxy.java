package edu.guidian.yurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import edu.guidian.yurpc.RpcApplication;
import edu.guidian.yurpc.config.RpcConfig;
import edu.guidian.yurpc.constant.RpcConstant;
import edu.guidian.yurpc.fault.retry.RetryStrategy;
import edu.guidian.yurpc.fault.retry.RetryStrategyFactory;
import edu.guidian.yurpc.fault.tolerant.TolerantStrategy;
import edu.guidian.yurpc.fault.tolerant.TolerantStrategyFactory;
import edu.guidian.yurpc.loadbalancer.LoadBalancer;
import edu.guidian.yurpc.loadbalancer.LoadBalancerFactory;
import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;
import edu.guidian.yurpc.model.ServiceMetaInfo;
import edu.guidian.yurpc.registry.Registry;
import edu.guidian.yurpc.registry.RegistryFactory;
import edu.guidian.yurpc.serializer.Serializer;
import edu.guidian.yurpc.serializer.SerializerFactory;
import edu.guidian.yurpc.server.tcp.VertxTcpClient;



import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 服务代理（JDK 动态代理）
 *
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serializer(rpcRequest);

            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            //无负载均衡默认使用第一个
            //ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            //负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            /**
             * 我们给负载均衡器传入了一个 requestParams HashMap，
             * 并且将请求方法名作为参数放到了 Map 中。
             * 如果使用的是一致性 Hash 算法，那么会根据 requestParams 计算 Hash 值，
             * 调用相同方法的请求 Hash 值肯定相同，所以总会请求到同一个服务器节点上。
             */
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams,serviceMetaInfoList);
            // 发送HTTP请求
//            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }
            //发送tcp请求

            //RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest,selectedServiceMetaInfo);
            RpcResponse rpcResponse = null;

            try {
                //rpc请求
                //使用重试机制
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStategy());
                rpcResponse = retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest,selectedServiceMetaInfo));
            } catch (Exception e) {
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }
            return rpcResponse.getData();
//            Vertx vertx = Vertx.vertx();
//            NetClient netClient = Vertx.vertx().createNetClient();
//            CompletableFuture<RpcResponse> responseFuture  = new CompletableFuture<>();
//            netClient.connect(serviceMetaInfo.getServicePort(),serviceMetaInfo.getServiceHost(),result -> {
//                if (result.succeeded()) {
//                    System.out.println("Connected to TCP server");
//                    io.vertx.core.net.NetSocket socket = result.result();
//                    //发送数据
//                    //构造消息
//                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
//                    //请求头
//                    ProtocolMessage.Header header = new ProtocolMessage.Header();
//                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
//                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
//                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
//                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
//                    protocolMessage.setHeader(header);
//                    //请求体
//                    protocolMessage.setBody(rpcRequest);
//                    //编码
//                    try {
//                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
//                        socket.write(encodeBuffer);
//                    } catch (IOException e) {
//                            throw new RuntimeException(e);
//                    }
//                    //响应
//                    socket.handler(buffer -> {
//                       try {
//                           ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
//                           responseFuture.complete(responseProtocolMessage.getBody());
//                       } catch (Exception e) {
//                           throw new RuntimeException("消息协议解码错误");
//                       }
//                    });
//                }else {
//                    System.err.println("Failed to connect to TCP server ");
//                }
//            });
//            RpcResponse rpcResponse = responseFuture.get();
//            netClient.close();
//            return rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException("调用失败");
        }
    }
}
