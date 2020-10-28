package com.prayerlaputa.homework.nio.httpclient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author chenglong.yu
 * created on 2020/10/27
 */
public class OkHttpClientDemo {

    private static String requestGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void main(String[] args) throws IOException {
        String response = requestGet("http://localhost:8801");
//        String response = requestGet("http://localhost:18081");
        System.out.println(response);
    }

}
