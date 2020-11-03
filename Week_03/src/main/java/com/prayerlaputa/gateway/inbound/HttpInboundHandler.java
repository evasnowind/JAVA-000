package com.prayerlaputa.gateway.inbound;

import com.prayerlaputa.gateway.outbound.HttpGatewayOutboundWithHookHandler;
import com.prayerlaputa.gateway.outbound.okhttp.OkHttpOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private String proxyServer;
    private List<String> proxyServerList;
    private HttpGatewayOutboundWithHookHandler handler;
    
    public HttpInboundHandler(String proxyServer) {
        this.proxyServer = proxyServer;
//        handler = new HttpOutboundHandler(this.proxyServer);
        handler = new OkHttpOutboundHandler(this.proxyServer);
    }

    public HttpInboundHandler(List<String> proxyServerList) {
        this.proxyServerList = proxyServerList;
        handler = new OkHttpOutboundHandler(proxyServerList);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            handler.handle(fullRequest, ctx);
        } catch(Exception e) {
            logger.error("HttpInboundHandler#channelRead: ", e);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
