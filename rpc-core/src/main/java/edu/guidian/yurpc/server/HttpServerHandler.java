package edu.guidian.yurpc.server;


import edu.guidian.yurpc.RpcApplication;
import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;
import edu.guidian.yurpc.registry.LocalRegistry;
import edu.guidian.yurpc.serializer.JdkSerializer;
import edu.guidian.yurpc.serializer.Serializer;
import edu.guidian.yurpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ServiceLoader;


public class HttpServerHandler implements Handler<HttpServerRequest> {
    /**
     * 不同的 web 服务器对应的请求处理器实现方式也不同，
     * 比如 Vert.x 中是通过实现 Handler<HttpServerRequest>
     *     接口来自定义请求处理器的。并且可以通过 request.bodyHandler 异步处理请求。
     */

    /**
     * 处理请求
     *
     * @param request
     */
    @Override
    public void handle(HttpServerRequest request) {
        //使用自带序列化
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        request.bodyHandler(body -> {
            //传入序列化后的数据
            byte[] bytes = body.getBytes();
            //准备进行反序列化
            RpcRequest rpcRequest = null;
            try {
                //使用序列化器反序列化
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //构造返回（响应）的对象
            RpcResponse rpcResponse = new RpcResponse();
            if (request == null) {
                rpcResponse.setMessage("rpcRequest is null");
                //返回数据
                doResponse(request, rpcResponse, serializer);
                return;
            }
            try {

                //本地注册的服务实现类
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                System.out.println("需要的服务：" + rpcRequest.getServiceName());
                System.out.println("本地注册的服务实现类：" + implClass);
                if (implClass == null) {
                    rpcResponse.setMessage("implClass is null");
                    return;
                }
                //调用指定方法
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                //返回的结果
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);


        });

    }

    /**
     * 返回响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        /**
         *响应头
         */
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            /**
             * 响应体
             */
            byte[] serialized = serializer.serializer(rpcResponse);
            /**
             * 结束响应并发送缓冲区中的内容作为响应体
             */
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
