package edu.guidian.example.provider;

import edu.guidian.example.common.service.UserService;
import edu.guidian.yurpc.RpcApplication;
import edu.guidian.yurpc.bootstrap.ProviderBootstrap;
import edu.guidian.yurpc.config.RegistryConfig;
import edu.guidian.yurpc.config.RpcConfig;
import edu.guidian.yurpc.model.ServiceMetaInfo;
import edu.guidian.yurpc.model.ServiceRegisterInfo;
import edu.guidian.yurpc.registry.LocalRegistry;
import edu.guidian.yurpc.registry.Registry;
import edu.guidian.yurpc.registry.RegistryFactory;
import edu.guidian.yurpc.server.HttpServer;
import edu.guidian.yurpc.server.VertxHttpServer;
import edu.guidian.yurpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {

    public static void main(String[] args) {


        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);


//        //RPC框架初始化
//        RpcApplication.init();
//        //注册服务
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName, UserServiceImpl.class);
//        //注册服务到注册中心
//
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//        try {
//            registry.register(serviceMetaInfo);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        //启动Web服务
////        HttpServer httpServer = new VertxHttpServer();
////        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
//        //启动tcp服务器
//        VertxTcpServer vertxTcpServer = new VertxTcpServer();
//        vertxTcpServer.doStart(8080);

    }
}
