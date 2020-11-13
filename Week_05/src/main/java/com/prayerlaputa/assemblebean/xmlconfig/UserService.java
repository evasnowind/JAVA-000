package com.prayerlaputa.assemblebean.xmlconfig;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * chenglong.yu
 */
public class UserService {

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void printService() {
        System.out.println("print Service....");

        userDao.printUserDao();
    }
}

