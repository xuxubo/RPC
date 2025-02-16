package edu.guidian.yurpc.protocol;

import edu.guidian.yurpc.model.RpcRequest;
import edu.guidian.yurpc.model.RpcResponse;
import edu.guidian.yurpc.serializer.Serializer;
import edu.guidian.yurpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;


public class ProtocolMessageDecoder {


    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        //校验魔术
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息magic非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        //解决粘包问题，只读指定距离的长度
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("消息类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case OTHERS:
            case HEART_BEAT:
            default:
                throw new RuntimeException("消息类型暂不支持");
        }
    }
}
