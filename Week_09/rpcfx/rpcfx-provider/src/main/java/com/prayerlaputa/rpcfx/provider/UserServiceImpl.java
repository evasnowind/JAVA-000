package com.prayerlaputa.rpcfx.provider;

import com.prayerlaputa.rpcfx.api.User;
import com.prayerlaputa.rpcfx.api.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
