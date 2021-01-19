# pmq

1、基于内存Queue实现生产和消费API（已经完成） 
1）创建内存Queue，作为底层消息存储 
2）定义Topic，支持多个Topic 
3）定义Producer，支持Send消息 
4）定义Consumer，支持Poll消息

2、去掉内存Queue，设计自定义Queue，实现消息确认和消费offset
1）自定义内存Message数组模拟Queue。
2）使用指针记录当前消息写入位置。
3）对于每个命名消费者，用指针记录消费位置

版本2实现思路参见[2-使用数组模拟queue的设计思路](2-使用数组模拟queue的设计思路.md)