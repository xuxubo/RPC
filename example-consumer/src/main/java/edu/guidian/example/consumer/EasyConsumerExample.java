package edu.guidian.example.consumer;

import edu.guidian.example.common.model.User;
import edu.guidian.example.common.service.UserService;
import edu.guidian.yurpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {

        //UserService userService = new UserServiceProxy();
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("test");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user==null");
        }
    }
}
