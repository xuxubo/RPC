package edu.guidian.example.consumer;

import edu.guidian.example.common.model.User;
import edu.guidian.example.common.service.UserService;
import edu.guidian.yurpc.config.RpcConfig;
import edu.guidian.yurpc.proxy.ServiceProxyFactory;
import edu.guidian.yurpc.utils.ConfigUtils;

public class ConsumerExample {

    public static void main(String[] args) {
        /**
         * 加载配置测试
         */
//        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpc);
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("test");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user==null");
        }
        long number = userService.getNumber();
        System.out.println(number);

    }
}
