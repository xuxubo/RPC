package edu.guidian.example.consumer;

import edu.guidian.example.common.model.User;
import edu.guidian.example.common.service.UserService;
import edu.guidian.yurpc.RpcApplication;
import edu.guidian.yurpc.bootstrap.ConsumerBootstrap;
import edu.guidian.yurpc.proxy.ServiceProxyFactory;


public class ConsumerExample {

    public static void main(String[] args) {
        /**
         * 加载配置测试
         */
        //RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");

//        System.out.println(rpc);
        //RpcApplication.init();
        ConsumerBootstrap.init();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("test");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user==null");
        }
        //short number = userService.getNumber();
        System.out.println(userService.getNumber());

    }
}
