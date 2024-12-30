package edu.guidian.example.provider;

import edu.guidian.example.common.service.UserService;
import edu.guidian.yurpc.registry.LocalRegistry;
import edu.guidian.yurpc.server.HttpServer;
import edu.guidian.yurpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        /**
         * 注册服务
         */
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        /**
         * 开启网络服务
         */
        HttpServer httpServer = new VertxHttpServer();
        //监听端口8080
        httpServer.doStart(8080);

    }
}
