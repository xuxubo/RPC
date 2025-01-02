package edu.guidian.example.provider;

import edu.guidian.example.common.model.User;
import edu.guidian.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    public User getUser(User user) {
        System.out.println("用户名" + user.getName());
        return user;
    }

}
