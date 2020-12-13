package com.prayerlaputa.rpcfx.client.proxy;

import com.prayerlaputa.rpcfx.common.RpcException;

/**
 * @author chenglong.yu
 * created on 2020/12/13
 */
public interface ProxyFactory {

    <T> T createProxy(final Class<T> serviceClass, final String url) throws RpcException;
}
