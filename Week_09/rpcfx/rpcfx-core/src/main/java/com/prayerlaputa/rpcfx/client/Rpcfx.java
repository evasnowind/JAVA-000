package com.prayerlaputa.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.client.proxy.ByteBuddyProxyFactory;
import com.prayerlaputa.rpcfx.common.RpcException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC客户端：
 * 生成代理对象
 * 常见方式：
 * 1. 动态代理
 * 2. AOP(字节码生成)
 *
 */
public final class Rpcfx {



    static {
        ParserConfig.getGlobalInstance().addAccept("com.prayerlaputa");
    }


    public static <T> T create(final Class<T> serviceClass, final String url) {
        // 0. 替换动态代理 -> AOP
        T proxy = null;

        try {
            ByteBuddyProxyFactory proxyFactory = new ByteBuddyProxyFactory();
            proxy = proxyFactory.createProxy(serviceClass, url);
        } catch (RpcException e) {
            e.printStackTrace();
        }
        return proxy;

//        // 0. 替换动态代理 -> AOP
//        return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url));

    }
    
    public static class RpcfxInvocationHandler implements InvocationHandler {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final Class<?> serviceClass;
        private final String url;
        public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url) {
            this.serviceClass = serviceClass;
            this.url = url;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(params);

            RpcfxResponse response = post(request, url);

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcfxException

            return JSON.parse(response.getResult().toString());
        }

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
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("resp json: "+respJson);
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
    }
}
