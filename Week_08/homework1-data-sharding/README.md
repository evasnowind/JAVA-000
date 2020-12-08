
## 分库分表后的订单表设计  

订单表结构不变，表的schema参见末尾。

### 分库分表方案  

订单中包含用户id、订单id，因此可以采用用户id（member_id）进行分库、使用订单id分表。

本次创建2个库、每个库16个订单。分库时使用用户id % 2取余，订单id % 16取余即可。


## 程序说明   

### 启动准备  

准备数据库、表，目前配置文件里配置的是：
- 两个库，分别是order_0, order_1
- 每个库16张表，分别是oms_order_0, oms_order_1, ......, oms_order_15
- 订单的主键id字段采用snowflake生成

批量创建库的脚本参见[batch_create_table_script.md](src/main/resources/script/batch_create_table_script.md)， 该脚本每次可以创建一个数据库、并在库中批量创建相同前缀的表。

也可以直接导入已生成好的SQL，参见`src/main/resources/script/order_0.sql` `src/main/resources/script/order_1.sql` 

### 测试  

准备好数据库后，修改配置文件中的数据库连接地址，然后启动ShardingDbTableReadWriteApplication.java。  
目前使用spring boot写了CRUD接口，方便起见，接口都采用了GET请求方式、且很多参数都直接写死了，毕竟我们的目的是联系sharding-jdbc分库分表的使用。

已完成的接口如下：

- 插入数据
    - 示例：http://localhost:8084/api/order/add?username=bbb&memberId=52&payment=100
- 查询所有订单
    - 示例：http://localhost:8084/api/order/list
- 查询某用户的所有订单（按用户id查询）
    - http://localhost:8084/api/order/listByMemberId?memberId=52
- 查询单个订单
    - http://localhost:8084/api/order/get?memberId=52&orderId=543033976919425024
- 更新某个订单
    - http://localhost:8084/api/order/update/amount?memberId=52&orderId=543033990689325057&amount=1023.11
- 删除某个订单
    - http://localhost:8084/api/order/delete?memberId=52&orderId=543033990689325057

## 问题  

遇到一个比较奇怪的问题，实在没时间深入看：
手工通过上面URL，测试单个增删查改操作时，没有问题，但通过sb连续压测（其实只是想连续多次执行），遇到这个帖子中的问题：
[ShardingSphere报Sharding value must implements Comparable.的解决过程](https://blog.csdn.net/qiaoqiyu6416/article/details/107044664)

希望有时间再深入研究下这个问题吧，本周作业先这样，本周实在太赶了，有时间的话我觉得还是先去复习比较好，先这样。

## 附录：订单表SCHEMA
```
CREATE TABLE `oms_order_XX` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键，记录唯一标识',
  `member_id` bigint(20) NOT NULL COMMENT '用户id',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '优惠券id',
  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime DEFAULT NULL COMMENT '提交时间',
  `member_username` varchar(64) DEFAULT NULL COMMENT '用户帐号',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '应付金额（实际支付金额）',
  `freight_amount` decimal(10,2) DEFAULT NULL COMMENT '运费金额',
  `promotion_amount` decimal(10,2) DEFAULT NULL COMMENT '促销优化金额（促销价、满减、阶梯价）',
  `integration_amount` decimal(10,2) DEFAULT NULL COMMENT '积分抵扣金额',
  `coupon_amount` decimal(10,2) DEFAULT NULL COMMENT '优惠券抵扣金额',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT '管理员后台调整订单使用的折扣金额',
  `pay_type` int(1) DEFAULT NULL COMMENT '支付方式：0->未支付；1->支付宝；2->微信',
  `source_type` int(1) DEFAULT NULL COMMENT '订单来源：0->PC订单；1->app订单',
  `status` int(1) DEFAULT NULL COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `order_type` int(1) DEFAULT NULL COMMENT '订单类型：0->正常订单；1->秒杀订单',
  `delivery_company` varchar(64) DEFAULT NULL COMMENT '物流公司(配送方式)',
  `delivery_sn` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `auto_confirm_day` int(11) DEFAULT NULL COMMENT '自动确认时间（天）',
  `integration` int(11) DEFAULT NULL COMMENT '可以获得的积分',
  `growth` int(11) DEFAULT NULL COMMENT '可以活动的成长值',
  `promotion_info` varchar(100) DEFAULT NULL COMMENT '活动信息',
  `bill_type` int(1) DEFAULT NULL COMMENT '发票类型：0->不开发票；1->电子发票；2->纸质发票',
  `bill_header` varchar(200) DEFAULT NULL COMMENT '发票抬头',
  `bill_content` varchar(200) DEFAULT NULL COMMENT '发票内容',
  `bill_receiver_phone` varchar(32) DEFAULT NULL COMMENT '收票人电话',
  `bill_receiver_email` varchar(64) DEFAULT NULL COMMENT '收票人邮箱',
  `receiver_name` varchar(100) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `receiver_post_code` varchar(32) DEFAULT NULL COMMENT '收货人邮编',
  `receiver_province` varchar(32) DEFAULT NULL COMMENT '省份/直辖市',
  `receiver_city` varchar(32) DEFAULT NULL COMMENT '城市',
  `receiver_region` varchar(32) DEFAULT NULL COMMENT '区',
  `receiver_detail_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `confirm_status` int(1) DEFAULT NULL COMMENT '确认收货状态：0->未确认；1->已确认',
  `delete_status` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态：0->未删除；1->已删除',
  `use_integration` int(11) DEFAULT NULL COMMENT '下单时使用的积分',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'
```

