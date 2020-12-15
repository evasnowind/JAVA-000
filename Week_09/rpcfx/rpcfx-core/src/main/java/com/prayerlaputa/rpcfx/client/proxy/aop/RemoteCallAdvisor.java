package com.prayerlaputa.rpcfx.client.proxy.aop;

import com.alibaba.fastjson.JSON;
import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.client.Rpcfx;
import com.prayerlaputa.rpcfx.common.RpcfxException;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenglong.yu
 * created on 2020/12/13
 */
@Slf4j
public class RemoteCallAdvisor {

    private final Class<?> serviceClass;
    private final String url;
    private CloseableHttpAsyncClient httpclient;

    public RemoteCallAdvisor(final Class<?> serviceClass, final String url,
                             final CloseableHttpAsyncClient httpAsyncClient) {
        this.serviceClass = serviceClass;
        this.url = url;
        this.httpclient = httpAsyncClient;
    }

    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method,
                                  @AllArguments @RuntimeType Object[] args) throws RpcfxException {

        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass(this.serviceClass);
        request.setMethod(method.getName());
        request.setParams(args);

        /*
        TODO 进一步扩展：若失败，则重试，到达重试次数之后再抛出异常
        此处留待后续扩展。
         */
        RpcfxResponse response = null;
        try {
            response = post(request, url);
        } catch (InterruptedException e) {
            log.error("[byteBuddyInvoke post] error:", e);
            throw new RpcfxException(e);
        }

        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException

        return JSON.parse(response.getResult().toString());
    }


    // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
    // int byte char float double long bool
    // [], data class

    private String respJson = null;

    private RpcfxResponse post(RpcfxRequest req, String url) throws InterruptedException {
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: " + reqJson);

        final HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpPost.setHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        //给httpPost设置JSON格式的参数
        StringEntity requestEntity = new StringEntity(reqJson, StandardCharsets.UTF_8);
        requestEntity.setContentEncoding(StandardCharsets.UTF_8.name());
        httpPost.setEntity(requestEntity);

        /*
        TODO 如何优雅的将httpclient异步请求的结果返回给上层的代理
        有待进一步查一下，今天先用CountDownLatch跑个结果出来吧。
         */
        CountDownLatch countDownLatch = new CountDownLatch(1);

        httpclient.execute(httpPost, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse response) {
                respJson = handleResponse(response);
                countDownLatch.countDown();
            }

            @Override
            public void failed(Exception ex) {
                httpPost.abort();
                countDownLatch.countDown();
                log.error("[httpclient post] error:", ex);
            }

            @Override
            public void cancelled() {
                httpPost.abort();
                countDownLatch.countDown();
                log.warn("[httpclient post]  cancelled!");
            }
        });

        log.info("http countdown lock before....");
        countDownLatch.await();
        log.info("http countdown lock after");

        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client
        log.info("resp json: " + respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }

    private String handleResponse(final HttpResponse endpointResponse) {
        String content = "";

        try {
            HttpEntity entity = endpointResponse.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent()) {
                    final StringBuilder sb = new StringBuilder();
                    final char[] tmp = new char[1024];
                    final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    int l;
                    while ((l = reader.read(tmp)) != -1) {
                        sb.append(tmp, 0, l);
                    }
                    content = sb.toString();
                }
            }
        } catch (ParseException | IOException e) {
            log.error("[handleResponse] error:", e);
        }

        return content;
    }

}
