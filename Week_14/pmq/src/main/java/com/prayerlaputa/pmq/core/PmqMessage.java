package com.prayerlaputa.pmq.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@AllArgsConstructor
@Data
public class PmqMessage<T> {

    private HashMap<String,Object> headers;

    private T body;

}
