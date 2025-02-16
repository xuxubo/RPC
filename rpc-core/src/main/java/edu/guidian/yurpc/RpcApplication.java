package edu.guidian.yurpc;

import edu.guidian.yurpc.config.RegistryConfig;
import edu.guidian.yurpc.config.RpcConfig;
import edu.guidian.yurpc.constant.RpcConstant;
import edu.guidian.yurpc.registry.Registry;
import edu.guidian.yurpc.registry.RegistryFactory;
import edu.guidian.yurpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * RPC 框架应用
 * 相当于 holder，存放了项目全局用到的变量。双检锁单例模式实现
 */
@Slf4j
public class RpcApplication {
    // volatile 确保线程间的可见性和禁止指令重排序优化
    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = newRpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        //在JVM关闭时，自动调用registry.destroy()方法，下线当前节点
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化配置环境
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取 RPC 配置
     * 双检锁单例模式 的经典实现，支持在获取配置时才调用 init 方法实现懒加载。
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            // 双检锁
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
