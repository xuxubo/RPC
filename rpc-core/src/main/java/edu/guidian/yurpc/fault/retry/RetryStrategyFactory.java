package edu.guidian.yurpc.fault.retry;

import edu.guidian.yurpc.spi.SpiLoader;

public class RetryStrategyFactory {
    static{
        SpiLoader.load(RetryStrategy.class);
    }

    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    /**
     * 获取加载器实例
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key) {
        RetryStrategy retryStrategy = SpiLoader.getInstance(RetryStrategy.class, key);
        return retryStrategy == null ? DEFAULT_RETRY_STRATEGY : retryStrategy;
    }
}
