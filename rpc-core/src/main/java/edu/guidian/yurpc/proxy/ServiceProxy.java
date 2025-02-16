package edu.guidian.yurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.protobuf.ProtocolStringList;
import edu.guidian.yurpc.RpcApplication;
import edu.guidian.yurpc.config.RpcConfig;
import edu.guidian.yurpc.constant.RpcConstant;
import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;
import edu.guidian.yurpc.model.ServiceMetaInfo;
import edu.guidian.yurpc.protocol.*;
import edu.guidian.yurpc.registry.Registry;
import edu.guidian.yurpc.registry.RegistryFactory;
import edu.guidian.yurpc.serializer.Serializer;
import edu.guidian.yurpc.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

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
            Vertx vertx = Vertx.vertx();
            NetClient netClient = Vertx.vertx().createNetClient();
            CompletableFuture<RpcResponse> responseFuture  = new CompletableFuture<>();
            netClient.connect(serviceMetaInfo.getServicePort(),serviceMetaInfo.getServiceHost(),result -> {
                if (result.succeeded()) {
                    System.out.println("Connected to TCP server");
                    io.vertx.core.net.NetSocket socket = result.result();
                    //发送数据
                    //构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    //请求头
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    protocolMessage.setHeader(header);
                    //请求体
                    protocolMessage.setBody(rpcRequest);
                    //编码
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                            throw new RuntimeException(e);
                    }
                    //响应
                    socket.handler(buffer -> {
                       try {
                           ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                           responseFuture.complete(responseProtocolMessage.getBody());
                       } catch (Exception e) {
                           throw new RuntimeException("消息协议解码错误");
                       }
                    });
                }else {
                    System.err.println("Failed to connect to TCP server ");
                }
            });
            RpcResponse rpcResponse = responseFuture.get();
            netClient.close();
            return rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
