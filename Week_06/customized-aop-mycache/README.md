## 自定义实现一个@MyCache注解  

类说明：
- MyCache：定义注解
- MyCacheAdvice: 注解具体实现
    - 内部采用了guava的缓存作为本地缓存，方便扩展。  
    - 通过使用java的延迟队列、启动一个调度任务线程池来定时扫描延迟队列的方式来实现过期清理本地缓存的目的。
    - 后续可以进一步可扩展的地方：
        - 本地缓存目前直接写死了最大空间为100，这个地方可以通过spring configuration的方式从配置文件读取、做成可配置
        - 进一步给该注解添加功能（可以参考guava缓存有什么功能），比如不同的缓存过期策略，并且同样的，通过spring configuration的方式让这些新功能可以通过配置文件配置
        - 进一步优化缓存删除的实现方式，可以采用惰性删除的思路，等某个key被调用时才去判断该key是否已经过期，这样的话目前的代码可能需要改动的多一些。
        - 基本就这样吧，主要时间比较紧张，不可能每个作业都做到尽善尽美。
- MyCacheDemoController, MyCacheDemoService
    - 测试代码

## 测试说明  
项目中定义了一个GET接口/api/get/val，用于测试缓存注解，该接口需传递一个参数key以及对应值val，MyCacheDemoService中的业务逻辑getValByCache会将val拼接上一个递增的序号后返回。  
启动SpringAopApplication，使用浏览器或POSTMAN访问本机http://localhost:8080/api/get/val?key=aaa 。