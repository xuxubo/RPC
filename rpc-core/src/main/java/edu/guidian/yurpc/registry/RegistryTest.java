package edu.guidian.yurpc.registry;

import cn.hutool.json.JSONUtil;
import edu.guidian.yurpc.config.RegistryConfig;
import edu.guidian.yurpc.model.ServiceMetaInfo;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import lombok.var;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RegistryTest {

    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
    }

    @Test
    public void unRegister() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        String serviceKey = serviceMetaInfo.getServiceKey();
        System.out.println("serviceKey = " + serviceKey);
        RegistryConfig registryConfig = new RegistryConfig();
        String searchPrefix = "/rpc/" + serviceKey + "/";
        System.out.println("Search Prefix: " + searchPrefix);
        Client client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        KV kvClient = client.getKVClient();
        List<ServiceMetaInfo> serviceMetaInfoList;
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();

            if (keyValues.isEmpty()) {
                System.out.println("No matching keys found for prefix: " + searchPrefix);
            }

            keyValues.forEach(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                System.out.println("Key: " + keyValue.getKey().toString(StandardCharsets.UTF_8));
                System.out.println("Value: " + value);
            });

            serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取服务列表失败", e);
        }

        if (serviceMetaInfoList.size() == 0) {
            System.out.println("获取服务失败");
            return;
        }
        for (ServiceMetaInfo metaInfo : serviceMetaInfoList)
            System.out.println(metaInfo);
    }

    @Test
    public void test() {

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        // 要查询的前缀
        String serviceKey = "/rpc/edu.guidian.example.common.service.UserService:1.0";
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        // 创建etcd客户端
        try (Client client = Client.builder().endpoints(registryConfig.getAddress()).build()) {
            // 获取KV客户端
            var kvClient = client.getKVClient();
            // 设置GetOption，启用前缀匹配
            GetOption getOption = GetOption.newBuilder()
                    .withPrefix(ByteSequence.from(serviceKey.getBytes(StandardCharsets.UTF_8)))
                    .build();
            // 异步获取数据
            CompletableFuture<GetResponse> getFuture = kvClient.get(ByteSequence.from(serviceKey.getBytes(StandardCharsets.UTF_8)), getOption);
            // 处理返回值
            GetResponse response = getFuture.get(); // 这里会阻塞，直到获取到结果
            // 遍历结果
            if(response == null){
                System.out.println("获取服务失败");
            }
            for (KeyValue kv : response.getKvs()) {
                String key = toString(kv.getKey());
                String value = toString(kv.getValue());
                System.out.printf("Key: %s, Value: %s%n", key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toString(ByteSequence key) {
        return new String(key.getBytes(), StandardCharsets.UTF_8);
    }


}
