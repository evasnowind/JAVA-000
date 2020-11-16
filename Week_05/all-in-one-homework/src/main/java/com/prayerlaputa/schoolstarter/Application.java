package com.prayerlaputa.schoolstarter;

/**
 * @author chenglong.yu
 * created on 2020/11/16
 */

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
//@EnableSchoolService
public class Application implements CommandLineRunner {


    @Resource
    private RunStarter runStarter;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        runStarter.testSchool();
    }
}
