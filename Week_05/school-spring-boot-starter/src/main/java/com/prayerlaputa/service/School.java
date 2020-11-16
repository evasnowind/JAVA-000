package com.prayerlaputa.service;

import com.prayerlaputa.bean.Klass;
import com.prayerlaputa.bean.Student;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@Data
public class School implements ISchool {

    // Resource 
    @Autowired(required = true) //primary
    private Klass class1;

    @Resource(name = "student100")
    private Student student100;

    public void ding() {
        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student100);
    }

}
