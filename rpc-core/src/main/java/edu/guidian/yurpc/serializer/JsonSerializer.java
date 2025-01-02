package edu.guidian.yurpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;

import java.io.IOException;
import java.util.concurrent.Callable;

public class JsonSerializer implements Serializer {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    /**
     * 序列化
     * @param object
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serializer(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T object = OBJECT_MAPPER.readValue(bytes, type);
        if (object instanceof RpcRequest) {
            return handleRequest((RpcRequest) object, type);
        }
        if (object instanceof RpcResponse) {
            return handleResponse((RpcResponse) object, type);
        }
        return object;
    }

    /**
     * 处理响应
     *
     * @param rpcResponse
     * @param type
     * @param <T>
     * @return
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        //处理响应数据
        //序列化
        byte[] data = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        //反序列化
        rpcResponse.setData(OBJECT_MAPPER.readValue(data, rpcResponse.getDataType()));
        return type.cast(rpcResponse);

    }

    /**
     * 处理请求
     * @param rpcRequest
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        //获取参数类型
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        //获取参数
        Object[] args = rpcRequest.getArgs();
        //循环处理每个参数类型
        for (int i = 0; i < parameterTypes.length; i++) {
            //获取参数类型
            Class<?> parameterType = parameterTypes[i];
            //判断参数类型是否相同
            if (!parameterType.isAssignableFrom(args[i].getClass())) {
                //需要转换
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcRequest);
                args[i] = OBJECT_MAPPER.readValue(bytes, parameterType);
            }
        }
        return type.cast(rpcRequest);
    }

}
