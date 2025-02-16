package edu.guidian.yurpc.server.tcp;

import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;
import edu.guidian.yurpc.protocol.ProtocolMessage;
import edu.guidian.yurpc.protocol.ProtocolMessageDecoder;
import edu.guidian.yurpc.protocol.ProtocolMessageEncoder;
import edu.guidian.yurpc.protocol.ProtocolMessageTypeEnum;
import edu.guidian.yurpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 实现 Vert.x 提供的 Handler<NetSocket> 接口，可以定义 TCP 请求处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket socket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer->{
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议解析码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();
            //处理请求
            //构造响应结果
            RpcResponse rpcResponse = new RpcResponse();
            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encoder = ProtocolMessageEncoder.encode(responseProtocolMessage);
                socket.write(encoder);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }

        });
        socket.handler(bufferHandlerWrapper);
    }
}
