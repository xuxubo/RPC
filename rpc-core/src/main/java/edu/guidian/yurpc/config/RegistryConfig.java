package edu.guidian.yurpc.config;

import edu.guidian.yurpc.registry.LocalRegistry;
import lombok.Data;

@Data
public class RegistryConfig {
    private String registry = "etcd";

    private String address = "http://127.0.0.1:2379";

    private String username;

    private String password;

    private Long timeout = 10000L;

}
