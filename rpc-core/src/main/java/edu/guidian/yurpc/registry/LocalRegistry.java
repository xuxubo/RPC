package edu.guidian.yurpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 本地服务注册器和注册中心的作用是有区别的。注册中心的作用侧重于管理注册的服务、提供服务信息给消费者；
 * 而本地服务注册器的作用是根据服务名获取到对应的实现类，是完成调用必不可少的模块。
 */
public class LocalRegistry {

    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName 服务名称
     * @param implClass   服务对象
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 跟我服务名称获取服务
     *
     * @param serviceName 服务名
     * @return
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName 服务名称
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }


}
