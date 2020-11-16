package com.prayerlaputa.schoolstarter;

import com.prayerlaputa.service.ISchool;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chenglong.yu
 * created on 2020/11/16
 */
@Component
public class RunStarter {


    @Resource
    private ISchool school;

    public void testSchool() {
        System.out.println();
        System.out.println("--------------业务方法开始执行---------------");
        school.ding();
        System.out.println("--------------业务方法执行结束---------------");
        System.out.println();
    }
}
