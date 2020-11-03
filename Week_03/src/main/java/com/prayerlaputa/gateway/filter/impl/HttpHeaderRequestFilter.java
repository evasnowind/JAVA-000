package com.prayerlaputa.gateway.filter.impl;

import com.prayerlaputa.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * @author chenglong.yu
 * created on 2020/11/2
 */
public class HttpHeaderRequestFilter implements HttpRequestFilter {

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        if (null != fullRequest) {
            /*
            FullHttpRequest 的构造方法会保证headers不为null，此处省去判空处理
             */
            HttpHeaders httpHeaders = fullRequest.headers();
            httpHeaders.set("nio", "hello!");
        }
    }
}
