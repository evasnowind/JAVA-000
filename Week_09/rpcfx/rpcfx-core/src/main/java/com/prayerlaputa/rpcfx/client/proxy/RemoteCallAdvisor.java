package com.prayerlaputa.rpcfx.client.proxy;

import com.alibaba.fastjson.JSON;
import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author chenglong.yu
 * created on 2020/12/13
 */
public class RemoteCallAdvisor {


    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    private final Class<?> serviceClass;
    private final String url;

    public RemoteCallAdvisor(final Class<?> serviceClass, final String url) {
        this.serviceClass = serviceClass;
        this.url = url;
    }

    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method,
                                  @AllArguments @RuntimeType Object[] args) throws Throwable {

        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass(this.serviceClass.getName());
        request.setMethod(method.getName());
        request.setParams(args);

        RpcfxResponse response = post(request, url);

        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException

        return JSON.parse(response.getResult().toString());
    }


    // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
    // int byte char float double long bool
    // [], data class

    private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: "+reqJson);

        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSONTYPE, reqJson))
                .build();
        String respJson = client.newCall(request)
                .execute()
                .body()
                .string();
        System.out.println("resp json: "+respJson);
        return JSON.parseObject(respJson, RpcfxResponse.class);
    }


}
