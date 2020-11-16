## 使用Java动态代理实现一个简单的AOP  

找了个现成的例子，稍微改改，原始例子参见 https://www.jianshu.com/p/aaeb2355ec5c

将Java动态代理生成代理对象的过程封装成MyProxyFactory类，然后在这个类中，使用接口Waiter、实现类MyWaiter简单写了写动态代理过程，测试代码参见MyProxyFactory类#main方法。

复习一下。  

