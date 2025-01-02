package edu.guidian.yurpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import edu.guidian.yurpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;




@Slf4j
public class SpiLoader {
    //ConcurrentHashMap 是 Java 中一个线程安全的哈希表实现，位于 java.util.concurrent 包中。它是 高效且线程安全 的，专为多线程环境下的高并发访问设计。
    // 存储已经加载的实例类
    private static Map<String, Map<String, Class<?>>> LoaderMap = new ConcurrentHashMap<>(); // <spiName, spi>>

    // 存储已经加载的实例
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    //系统SPI目录
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";
    //用户定义SPI目录
    private static final String RPC_USER_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_USER_SPI_DIR};
    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);


    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("开始加载所有SPI");
        for (Class<?> clazz : LOAD_CLASS_LIST) {
            load(clazz);
        }
    }

    /**
     * 获取某个接口的实例
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = LoaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(String.format("实例化 %s 失败", implClassName), e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        //扫描路径，用户自定义的SPI优先级高于系统SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            System.out.println(scanDir+loadClass.getName());
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream(), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("加载 SPI 失败", e);
                }
            }
        }
        LoaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

}



