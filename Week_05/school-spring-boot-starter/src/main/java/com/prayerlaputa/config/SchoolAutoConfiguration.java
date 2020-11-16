package com.prayerlaputa.config;

import com.prayerlaputa.bean.Klass;
import com.prayerlaputa.properties.SchoolProperties;
import com.prayerlaputa.bean.Student;
import com.prayerlaputa.service.School;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/11/16
 */
@Configuration
@EnableConfigurationProperties(SchoolProperties.class)
public class SchoolAutoConfiguration {

    @Resource
    private SchoolProperties schoolProperties;


    @Bean(name="student100")
    @ConditionalOnMissingBean
    public Student initStudent100() {
        Student student = new Student(schoolProperties.getId(), schoolProperties.getName());
        System.out.println("---------------init student---------------");
        System.out.println("SchoolAutoConfiguration#student100() executed, student=" + student);
        System.out.println();
        return student;
    }

    @Bean
    @ConditionalOnBean(name = {"student100"})
    @ConditionalOnMissingBean(name = {"class1"})
    public Klass class1(Student student100) {
        List<Student> studentList = new ArrayList<>();
        studentList.add(student100);

        Klass klass = new Klass();
        klass.setStudents(studentList);
        System.out.println("---------------init class---------------");
        System.out.println("SchoolAutoConfiguration#class1() executed, klass=" + klass);
        System.out.println();
        return klass;
    }

    @Bean
    @ConditionalOnBean(name = "class1")
    @ConditionalOnMissingBean(name = {"school"})
    public School school(Klass class1) {
        School school = new School();
        school.setClass1(class1);
        System.out.println("---------------init school---------------");
        System.out.println("SchoolAutoConfiguration#school() executed, school=" + school);
        System.out.println();
        return school;
    }
}
