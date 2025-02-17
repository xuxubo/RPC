package edu.guidian.yurpc.loadbalancer;

import edu.guidian.yurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();
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

        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
