package edu.guidian.yurpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo序列化器
 */
public class KryoSerializer implements Serializer {

    /**
     * kryo 线程不安全，使用ThreadLocal保证线程安全
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        //设置动态动态序列化和反序列化类，不提前注册所有类（可能有安全问题）
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    /**
     * 序列化
     * @param object
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serializer(T object) throws IOException {
        //byte[]的序列化对象
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //输出流，用于存储序列化后的字节数据
        Output output = new Output(byteArrayOutputStream);
        //从 ThreadLocal 获取 Kryo 实例，并对对象进行序列化
        KRYO_THREAD_LOCAL.get().writeObject(output, object);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T res = KRYO_THREAD_LOCAL.get().readObject(input, type);
        input.close();
        return res;
    }
}
