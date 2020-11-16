# Week 5 Homework

[TOC]



## 必做题目

1. 写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）, 提交到 Github。
2. 给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。
3. 研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：
   1）使用 JDBC 原生接口，实现数据库的增删改查操作。
   2）使用事务，PrepareStatement 方式，批处理方式，改进上述操作。
   3）配置 Hikari 连接池，改进上述操作。提交代码到 Github。

上述必做题目已全部完成，作业说明如下：

### 1. 写代码实现spring bean的装配

#### XML配置注入  
- 代码：`com.prayerlaputa.assemblebean.xmlconfig`
- 配置文件：`resources/xmlbeans.xml`
- 测试：`test.com.prayerlaputa.assemblebean.xmlconfig.TestXmlDependencyInject` 
- 程序说明：创建了两个bean，CollectionBean用于演示使用XML注入数组、List、Map、Properties类型的成员变量；UserService对象则演示bean之间的注入, UserDAO对象注入到UserService中。

#### 半自动注解配置  
- 代码：`com.prayerlaputa.assemblebean.halfauto`
- 配置文件：`resources/halfautoannotation.xml`
- 测试：`test.com.prayerlaputa.assemblebean.halfauto.TestHalfAutoAnnotationInjection` 
- 程序说明：分别创建HalfAutoUserDao、HalfAutoUserService两个bean，然后将HalfAutoUserDao自动注入到HalfAutoUserService。使用@Service @Repository等注解 + xml配置中指定bean目录。

#### java config配置
- 代码：`com.prayerlaputa.assemblebean.javaconfig`
- 配置文件：`resources/javaconfig.xml`
- 测试：`test.com.prayerlaputa.assemblebean.javaconfig.TestJavaConfig` 
- 程序说明：通过@Configuration，@Bean创建了两个bean，Car、Person，将Car注入到Person的bean中。


#### autoconfig配置  
- 代码：`com.prayerlaputa.assemblebean.autoinjection`
- 配置文件：无
- 测试：`test.com.prayerlaputa.assemblebean.autoinjection.TestAutoInjection` 
- 程序说明：通过@Configuration，@Bean 将创建2个bean，Person必然创建，而Cat、Dog只会创建其中之一、并注入到Person的animal成员变量中。Cat、Dog都是Animal子类，给Dog的创建方法上加@Conditional注解，具体判断条件由PetCondition类中定义，目前的实现逻辑是：如果已经有了person对象、且容器中没有cat对象，才会创建dog对象。
    - 此处只是一个示例，PetCondition可以进一步修改，比如通过配置文件，或是通过输入参数指定创建Cat还是Dog。这个就不继续写了。
    - 其他类似的注解参见下面。

```text
@ConditionalOnSingleCandidate	当给定类型的bean存在并且指定为Primary的给定类型存在时,返回true
@ConditionalOnMissingBean	当给定的类型、类名、注解、昵称在beanFactory中不存在时返回true.各类型间是or的关系
@ConditionalOnBean	与上面相反，要求bean存在
@ConditionalOnMissingClass	当给定的类名在类路径上不存在时返回true,各类型间是and的关系
@ConditionalOnClass	与上面相反，要求类存在
@ConditionalOnCloudPlatform	当所配置的CloudPlatform为激活时返回true
@ConditionalOnExpression	spel表达式执行为true
@ConditionalOnJava	运行时的java版本号是否包含给定的版本号.如果包含,返回匹配,否则,返回不匹配
@ConditionalOnProperty	要求配置属性匹配条件
@ConditionalOnJndi	给定的jndi的Location 必须存在一个.否则,返回不匹配
@ConditionalOnNotWebApplication	web环境不存在时
@ConditionalOnWebApplication	web环境存在时
@ConditionalOnResource	要求制定的资源存在
```


#### 参考资料  
- [spring依赖注入与注解实例](https://blog.csdn.net/feinifi/article/details/88839443)
- [spring使用JavaConfig进行配置](https://blog.csdn.net/peng86788/article/details/81188049)
- [SpringBoot基础篇Bean之条件注入@Condition使用姿势](https://blog.csdn.net/liuyueyi25/article/details/83244263)



### 2. 自己实现一个 Student/Klass/School的spring boot starter

单独创建了一个项目来实现spring boot starter，参见目录：`JAVA-000/Week_05/school-spring-boot-starter`

目前使用了如下特性：

- 通过@Configuration注解，初始化节点创建了Student, Klass, School对象，并将进行了注入操作（Student -> Klass, Klass->School）
- 使用@ConditionalOnMissingBean @ConditionalOnBean 判断bean是否已创建
- 自定义一个@EnableSchoolService， 通过@Import注解主动将SchoolAutoConfiguration导入、替代spring factories机制
- 支持通过配置文件动态修改Student的id, name属性值。默认值为id=99, name=Jackie

school-spring-boot-starter的使用：

1. 编译，打包school-spring-boot-starter项目：`mvn clean package install`，使其进入到本地maven仓库

2. 在想要使用school-spring-boot-starter的项目中，引入该包，方式如下：

   ```text
           <dependency>
               <groupId>com.prayerlaputa</groupId>
               <artifactId>school-spring-boot-starter</artifactId>
               <version>1.0-SNAPSHOT</version>
           </dependency>
   ```

   

3. 通过@Resource或是@Autowire即可自动注入 ISchool服务。

4. 如有必要，可以在配置文件中修改Student对象的值，如下面`application.yml`中配置是将Student的id置为100、name置为Prayer.Bright

   ```yml
   tiny-school:
     id: 100
     name: Prayer.Bright
   ```

   

   **测试代码参见: com.prayerlaputa.schoolstarter.Application**





### 3. JDBC的使用

使用 JDBC 原生接口、使用PrepareStatement 写增删改查，Hikari的使用做了，其他的，如使用JDBC批量处理、事务处理，实在太基础，不太想做了，找了篇帖子复习了下，参见这个 https://www.cnblogs.com/Mrchengs/p/9780985.html

已完成代码参见：

- 原生JDBC的使用：com.prayerlaputa.jdbc.DBUtil
- PrepareStatement 的使用：com.praylaputa.jdbc.DBUtil2
- Hikari的使用：com.prayerlaputa.jdbc.HikariDemo   配置了下hikari，再详细写CRUD感觉必要性不大。





## 选做题目

已完成如下选做题目：

1.  使用java动态代理实现AOP，参见com.prayerlaputa.dynamicproxyaop，测试在MyProxyFactory类中，较为基础，不写说明了。
2. （中级挑战）尝试使用 ByteBuddy 实现一个简单的基于类的 AOP；

程序代码在`com.prayerlaputa.bytebuddy.aop`下面。

参考了多个资料（如[字节码编程](https://bugstack.cn/itstack/itstack-demo-bytecode.html)），目前写了3个通过ByteBuddy实现AOP代理的demo。

### 方法注解，方法执行前后拦截  
- 代码：src/main/java/com/prayerlaputa/bytebuddyaop/methodaop1
- 测试：ByteBuddyAopDemo
- 代码说明：通过ByteBuddy自定义了MyLog注解，在MyService的doBusiness方法前后拦截

### 方法注解，方法执行结束时通过AOP输出方法入参、出参、耗时等  
- 代码：src/main/java/com/prayerlaputa/bytebuddyaop/methodaop2
- 测试：ByteBuddyAopDemo2
- 代码说明：通过ByteBuddy生成一个代理，将MyService2的doBusiness方法交给ByteBuddyMonitorDemo来执行，ByteBuddyMonitorDemo中的代理方法执行完业务方法后，还会输出方法入参、出参、耗时等信息。

### 类和方法注解，继承一个抽象类并实现抽象方法  
- 代码：src/main/java/com/prayerlaputa/bytebuddyaop/classandmethodaop
- 测试：ByteBuddyAopDemo3
- 代码说明：直接通过ByteBuddy生成一个类，该类继承抽象类Repository，并添加了自定义的两个注解，将抽象方法委托给UserRepositoryInterceptor来执行。



