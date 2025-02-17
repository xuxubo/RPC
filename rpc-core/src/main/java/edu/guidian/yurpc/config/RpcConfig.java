package edu.guidian.yurpc.config;

import edu.guidian.yurpc.fault.retry.RetryStrategy;
import edu.guidian.yurpc.fault.retry.RetryStrategyKeys;
import edu.guidian.yurpc.fault.tolerant.TolerantStrategyKeys;
import edu.guidian.yurpc.loadbalancer.LoadBalancerKeys;
import edu.guidian.yurpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {

    private String name = "yurpc";

    private String version = "1.0.0";

    private String serverHost = "127.0.0.1";

    private Integer serverPort = 8080;

    private boolean mock = false;
    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;

    private String serializer = SerializerKeys.JDK;

    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    private String retryStategy = RetryStrategyKeys.No;

    private RegistryConfig registryConfig = new RegistryConfig();
}
