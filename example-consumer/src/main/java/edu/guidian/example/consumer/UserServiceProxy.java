package edu.guidian.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import edu.guidian.example.common.model.User;
import edu.guidian.example.common.service.UserService;
import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;
import edu.guidian.yurpc.serializer.JdkSerializer;
import edu.guidian.yurpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        Serializer serializer = new JdkSerializer();

        /**
         * 构造request请求参数
         */
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            //序列化
            byte[] bodyBytes = serializer.serializer(rpcRequest);
            byte[] result;
            //发送请求
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                //接收响应
                result = httpResponse.bodyBytes();
            }
           //反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            //返回响应数据
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
