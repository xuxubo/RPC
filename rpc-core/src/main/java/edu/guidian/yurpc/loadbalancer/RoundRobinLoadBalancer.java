package edu.guidian.yurpc.loadbalancer;

import edu.guidian.yurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger currentIndex = new AtomicInteger(0);
    /**
     * @param requestParams       请求参数
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty())
            return null;
        int size = serviceMetaInfoList.size();
        if (size == 1)
            return serviceMetaInfoList.get(0);

        //取模算法轮询
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
