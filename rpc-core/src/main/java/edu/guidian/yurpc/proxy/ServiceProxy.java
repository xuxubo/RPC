package edu.guidian.yurpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import edu.guidian.yurpc.RpcApplication;
import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;
import edu.guidian.yurpc.serializer.JdkSerializer;
import edu.guidian.yurpc.serializer.Serializer;
import edu.guidian.yurpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * InvocationHandler 拦截代理对象的方法调用，并执行自定义的处理逻辑。
     * proxy: 代理实例，表示当前正在调用的代理对象。
     * method: 被调用的方法对象，表示代理对象调用的方法。
     * args: 方法参数数组，如果没有参数则为 null。
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serializer(rpcRequest);

            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                //反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
