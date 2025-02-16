package edu.guidian.yurpc.protocol;


import lombok.*;
import org.checkerframework.checker.units.qual.N;

/**
 * 自定义消息结构
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProtocolMessage<T> {

    /**
     * 请求头
     */
    private Header header;

    /**
     * 消息体
     */
    private T body;

    @Getter
    @Setter
    public static class Header {
        /**
         * 魔数，保证安全性
         */
        private byte magic;

        /**
         * 版本号
         */
        private byte version;

        /**
         * 序列化器
         */
        private byte serializer;

        /**
         * 消息类型（请求 / 响应）
         */
        private byte type;

        /**
         * 状态
         */
        private byte status;

        /**
         * 请求 id
         */
        private long requestId;

        /**
         * 消息体长度
         */
        private int bodyLength;
    }

}
