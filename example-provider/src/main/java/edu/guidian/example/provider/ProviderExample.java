package edu.guidian.example.provider;

import edu.guidian.example.common.service.UserService;
import edu.guidian.yurpc.RpcApplication;
import edu.guidian.yurpc.registry.LocalRegistry;
import edu.guidian.yurpc.server.HttpServer;
import edu.guidian.yurpc.server.VertxHttpServer;

public class ProviderExample {

    public static void main(String[] args) {
        RpcApplication.init();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

    }
}
