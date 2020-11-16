package com.prayerlaputa.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chenglong.yu
 * created on 2020/11/16
 */
@ConfigurationProperties(prefix = "tiny-school")
@Getter
@Setter
public class SchoolProperties {

    private String name = "Jackie";
    private Integer id = 99;
}
