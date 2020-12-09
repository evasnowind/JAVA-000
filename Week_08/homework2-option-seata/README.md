## seata AT模式 demo

### 准备工作
- 先部署好seata服务端，并修改配置文件中的seata值
- 部署mysql服务器，并导入data.sql，此时将会创建3个数据库，seat_orders, seata_account，seata_product

### 使用
- 启动account-service, product-service, order-service
- 使用POSTMAN，发送POST请求到http://localhost:28081/order/create，  请求参数中指定 userId productId price。