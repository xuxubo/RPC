package edu.guidian.yurpc.loadbalancer;

import edu.guidian.yurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHanshLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_COUNT = 100;

    /**
     * 每次调用负载均衡器时，都会重新构造 Hash 环，这是为了能够即时处理节点的变化。
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {

        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        //构建虚拟节点环
        for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_COUNT; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }
        //获取调用的哈希值
        int hash = getHash(requestParams);
        //选择最接近且大于等于调用请求hash值的虚拟节点,返回与大于或等于给定键的最小键相关联的键值映射，如果没有此键，则 null
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            //返回与该地图中的最小键相关联的键值映射，如果地图为空，则返回 null 。
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 哈希算法
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }

}
