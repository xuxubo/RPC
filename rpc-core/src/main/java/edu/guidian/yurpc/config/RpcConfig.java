package edu.guidian.yurpc.config;

import edu.guidian.yurpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {

    private String name = "yurpc";

    private String version = "1.0.0";

    private String serverHost = "127.0.0.1";

    private Integer serverPort = 8081;

    private boolean mock = false;

    private String serializer = SerializerKeys.JDK;

    private RegistryConfig registryConfig = new RegistryConfig();
}
