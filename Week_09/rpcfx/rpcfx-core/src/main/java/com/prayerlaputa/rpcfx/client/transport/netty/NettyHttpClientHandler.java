package com.prayerlaputa.rpcfx.client.transport.netty;

import com.alibaba.fastjson.JSON;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.api.RpcfxResponseFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyHttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final InFlightRequests inFlightRequests;

    public NettyHttpClientHandler(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
        messageReceived(ctx, httpObject);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        ctx.close();
    }


    public void messageReceived(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {

        if (httpObject instanceof HttpResponse) {

            HttpResponse response = (HttpResponse) httpObject;

            if (HttpResponseStatus.OK.equals(response.status())) {
                if (httpObject instanceof HttpContent) {
                    HttpContent content = (HttpContent) httpObject;

                    String respStr = content.content().toString(CharsetUtil.UTF_8);

                    RpcfxResponse rpcfxResponse = JSON.parseObject(respStr, RpcfxResponse.class);

                    RpcfxResponseFuture<RpcfxResponse> responseFuture = inFlightRequests.remove(rpcfxResponse.getRequestId());
                    if (null != responseFuture) {
                        responseFuture.getFuture().complete(rpcfxResponse);
                    } else {
                        log.error("找不到requestId={}的请求信息！", rpcfxResponse.getRequestId());
                    }
                }
            }
        } else {
            log.error("错误的报文！");
        }
    }
}
