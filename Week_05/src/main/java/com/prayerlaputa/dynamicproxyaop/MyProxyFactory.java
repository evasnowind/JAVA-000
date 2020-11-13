package com.prayerlaputa.dynamicproxyaop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author chenglong.yu
 * created on 2020/11/13
 */
public class MyProxyFactory {

    public Object createProxyObject(Object targetObject, BeforeAdvice beforeAdvice, AfterAdvice afterAdvice) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        //获取当前类型所实现的所有接口类型
        Class[] interfaces = targetObject.getClass().getInterfaces();

        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                /**
                 * 在调用代理对象的方法时，会执行这里的内容
                 */
                if(beforeAdvice != null) {
                    beforeAdvice.before();
                }
                Object result = method.invoke(targetObject, args);//调用目标对象的目标方法
                //执行后续增强
                afterAdvice.after();

                //返回目标对象的返回值
                return result;
            }
        };
        /**
         * 2、得到代理对象
         */
        return Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }


    public static void main(String[] args) {
        MyProxyFactory proxyFactory = new MyProxyFactory();
        MyWaiter waiter = new MyWaiter();

        Object proxy = proxyFactory.createProxyObject(waiter, new BeforeAdvice() {
            @Override
            public void before() {
                System.out.println("上帝您好！");
            }
        }, new AfterAdvice() {
            @Override
            public void after() {
                System.out.println("上帝再见！");
            }
        });
        //proxy call
        Waiter proxyWaiter = (Waiter) proxy;
        proxyWaiter.service();
    }
}
