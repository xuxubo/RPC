package edu.guidian.yurpc.registry;

import edu.guidian.yurpc.config.RegistryConfig;
import edu.guidian.yurpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {

    void init(RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    void destroy();

    void heartBeat();

    void watch(String serviceNodeKey);


}
