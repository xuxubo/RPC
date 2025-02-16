package edu.guidian.yurpc.protocol;

import edu.guidian.yurpc.serializer.Serializer;
import edu.guidian.yurpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

public class ProtocolMessageEncoder {

    /**
     * 编码
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        //空数据集
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }

        //序列化
        ProtocolMessage.Header header = protocolMessage.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        //序列化序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serializer(protocolMessage.getBody());
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
