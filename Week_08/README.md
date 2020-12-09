# week 8 作业

- （必做）设计对前面的订单表数据进行水平分库分表，拆分2个库，每个库16张表。并在新结构在演示常见的增删改查操作。代码、sql和配置文件，上传到github。  
    - homework1-data-sharding
- （必做）基于hmily TCC或ShardingSphere的Atomikos XA实现一个简单的分布式 事务应用demo（二选一），提交到github。
    - 本来想用spring boot的方式写，但遇到jar的问题，总是搞不定，时间成本上不划算，暂时放弃，从已提交作业里找一份走一下JDBC的方式，回头继续吧。
    - sharding-jdbc的例子实在太。。。。。。
- （选做）基于seata框架实现TCC或AT模式的分布式事务demo。 
    - homework2-option-seata
    - 这个是之前我学习seata时自己找例子写的demo，
    - 最近半年积累:
        - 框架开发技术demo汇总：https://github.com/evasnowind/framework-dev-learning 
        - 分布式技术demo汇总: https://github.com/evasnowind/distributed-dev-learning  
        欢迎围观^_^