package com.prayerlaputa.assemblebean.autoinjection;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author chenglong.yu
 * created on 2020/11/13
 */
public class TestAutoInjection {

    @Test
    public void testConditionAnnotation() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BeanConfig.class);
        Person person = (Person)context.getBean("person");
        System.out.println("---------------------------------");
        System.out.println("当前person持有的宠物对象=" + person.getAnimal().getName());
        System.out.println("---------------------------------");
    }
}
