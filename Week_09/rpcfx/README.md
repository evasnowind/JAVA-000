# 手写一个简单的RPC

目前已完成的扩展内容：

- [X] 生成代理对象时采用AOP的方式，替代java动态代理
- [X] 发送请求时使用httpclient发送
- [X] 考虑封装一个全局的RpcfxException
- [ ] server端返回请求结果时，只需序列化一次

后续可以进一步扩展：
- [ ] 使用netty，进一步优化请求的发送、响应过程
- [ ] 优化服务端处理
    - 目前实际上利用spring boot简化了服务端的处理，真正RPC请求需要处理在途请求的问题，即记录当前有哪个客户端发送请求给我、以便将结果发送给正确的客户端，并且个别客户端发送的请求可能超时，也需要服务端及时清理掉，避免超时请求占用本地内存、导致程序崩溃。
    - 这块内容我之前看的是李玥老师的《消息队列高手课》，可以参考https://github.com/liyue2008/simple-rpc-framework  里的 NettyTransport
- [ ] 自定义序列化、反序列化
    - 这个如果使用netty就会很方便，可以使用MessageToMessageCodec ，具体可以参考之前我写过的netty-im（netty入门与实战 小册子的内容） https://github.com/evasnowind/netty-im
- [ ] 模仿dubbo，支持使用注解简化RPC调用