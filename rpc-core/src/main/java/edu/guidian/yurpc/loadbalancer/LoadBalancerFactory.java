package edu.guidian.yurpc.loadbalancer;

import edu.guidian.yurpc.spi.SpiLoader;

/**
 * 使用工厂创建对象、使用 SPI 动态加载自定义的注册中心。
 *
 */
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    private  static final LoadBalancer DEFAULT_LOAD_BALANCER = new RandomLoadBalancer();

    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
