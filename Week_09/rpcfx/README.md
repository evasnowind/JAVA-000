# 手写一个简单的RPC

目前已完成的扩展内容：

- [X] 生成代理对象时采用AOP的方式，替代java动态代理
- [X] 考虑封装一个全局的RpcfxException
- [X] server端返回请求结果时，只需序列化一次
- [X] 使用Netty+HTTP作为client端传输方式
    - 另外也实现了httpclient作为客户端传输方式，因为没看到老师作业题目更新了，不过多做点也挺好
- [X] 添加请求超时判断逻辑
    - 参考了李玥老师的《消息队列高手课》 https://github.com/liyue2008/simple-rpc-framework  里的 NettyTransport

后续可以进一步扩展：
- [ ] 自定义序列化、反序列化
    - 这个如果使用netty就会很方便，可以使用MessageToMessageCodec ，具体可以参考之前我写过的netty-im（netty入门与实战 小册子的内容） https://github.com/evasnowind/netty-im
- [ ] 模仿dubbo，支持使用注解简化RPC调用
- [ ] 负载均衡
- [ ] 其他扩展