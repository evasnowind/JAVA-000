package com.prayerlaputa.rpcfx.client.proxy;

import com.prayerlaputa.rpcfx.common.RpcException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @author chenglong.yu
 * created on 2020/12/13
 */
public class ByteBuddyProxyFactory implements ProxyFactory {
    @Override
    public <T> T createProxy(Class<T> serviceClass, String url) throws RpcException {

        T proxy = null;

        try {
            RemoteCallAdvisor handler = new RemoteCallAdvisor(serviceClass, url);

            Class<? extends T> cls = new ByteBuddy()
                    .subclass(serviceClass)
                    .method(ElementMatchers.isDeclaredBy(serviceClass))
                    .intercept(MethodDelegation.to(handler, "handler"))
                    .make()
                    .load(serviceClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            proxy = cls.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return proxy;
    }

}
