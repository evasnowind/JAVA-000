package com.prayerlaputa.gateway.outbound.okhttp;

import com.prayerlaputa.gateway.filter.impl.HttpHeaderRequestFilter;
import com.prayerlaputa.gateway.outbound.HttpGatewayOutboundWithHookHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import io.netty.handler.codec.http.HttpUtil;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 */
public class OkHttpOutboundHandler extends HttpGatewayOutboundWithHookHandler {

    private static Logger logger = LoggerFactory.getLogger(OkHttpOutboundHandler.class);

    private OkHttpClient client;
    private String backendUrl;
    private List<String> endpointUrlList;

    public OkHttpOutboundHandler(String backendUrl) {
        this.backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;

        init();
    }

    public OkHttpOutboundHandler(List<String> proxyServerList) {
        this.endpointUrlList = new ArrayList<>();
        if (null != proxyServerList && proxyServerList.size() > 0) {
            for (String url : proxyServerList) {
                String tmp = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
                endpointUrlList.add(tmp);
            }
        }

        init();
    }

    private void init() {
        //添加filter
        this.addHookBeforeHandlingRequest(new HttpHeaderRequestFilter());

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                //TODO okhttp可以手工指定连接池，这里可以稍微瞅瞅怎么设置比较优雅
                .build();
    }


    @Override
    public void processRequest(FullHttpRequest fullRequest, ChannelHandlerContext ctx) throws Exception {
        String endpoint = this.httpEndpointRouter.route(endpointUrlList);
        System.out.println("endpoint url=" + endpoint);
        if (null == endpoint) {
            throw new IllegalAccessException("无法找到后端应用！");
        }
        final String url = endpoint + fullRequest.uri();
        processGetRequest(fullRequest, ctx, url);
    }


    private Request createOkHttpRequest(final FullHttpRequest fullHttpRequest, final String url) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        for (Map.Entry<String, String> entry : fullHttpRequest.headers().entries()) {
            builder.addHeader(entry.getKey(), entry.getValue());
//            System.out.println("request header <k,v>=(" + entry.getKey() + "," + entry.getValue() + ")");
        }
        return builder.build();
    }

    private void processGetRequest(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx, final String url) {
        Request request = createOkHttpRequest(fullHttpRequest, url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.error("processGetRequest failure:", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                forwardResponse(fullHttpRequest, ctx, response);
            }
        });
    }

    private void copyHttpHeader(HttpHeaders from, HttpHeaders to) {
        for (Map.Entry<String, String> entry : from.entries()) {
            to.add(entry.getKey(), entry.getValue());
        }
    }

    private void forwardResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, Response response) {
        FullHttpResponse resp = null;
        try {
            byte[] respBody = response.body().bytes();

//            System.out.println("收到数据：" + new String(respBody));

            resp = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(respBody));
            resp.headers().set("Content-Type", "application/json");

            Headers responseHeaders = response.headers();

            for (int i = 0; i < responseHeaders.size(); i++) {
                resp.headers().set(responseHeaders.name(i), responseHeaders.value(i));
//                System.out.println("header key="+ responseHeaders.name(i) + " value=" + responseHeaders.value(i));
            }
        } catch (Exception e) {
            logger.error("forwardResponse error:", e);
            resp = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (null != fullRequest) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(resp).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(resp);
                }
            }
            ctx.flush();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("error:", cause);
        ctx.close();
    }
}
