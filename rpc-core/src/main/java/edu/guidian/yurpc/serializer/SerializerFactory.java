package edu.guidian.yurpc.serializer;

import edu.guidian.yurpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {


    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认加载器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取加载器实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }};
//
//    /**
//     * 默认实例
//     */
//    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);
//
//    /**
//     * 获取实例
//     * @param key
//     * getOrDefault 如何不存在则返回默认值
//     * @return
//     */
//    public static Serializer getSerializer(String key) {
//        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
//    }

}
