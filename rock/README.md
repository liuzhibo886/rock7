
#rock 0.0.1 技术架构

#开发规范
-	遵循阿里巴巴开发规范

## 基于springboot 2.2.1,集成如下模块
-	采用nacos注册中心和配置中心
-   集成mybatis 3.2.0,mybatis plus 3.2.0 ,支持多数据源，可增加mycat分库分表
-   集成 ehcache 2.10.6本地缓存
-   集成 momgodb,提供objectID转换以及多数据源
-   集成 redis,提供RedisMapper 集成工具类
-   集成 rocketmq 封装生产消费者工具类，配合rock-delay实现任意时间延时消息
-   集成 shiro 后台权限管理
-   集成 beetl 模板语言,快速后台功能开发
-   集成 Swagger2 文档
-   集成 fegin 远程调用
-   集成 fastjson 以及 fegin等一切序列化均统一采用fastjson 序列化

#错误码枚举规范
系统错误码  -1000至 2000

#系统异常

### 相关地址

### 使用
-	拉取源码，运行 mvn install，打包至本地仓库,或者个人maven私服,配合业务架构 rock-system,rock-netty,rock-admin,rock-delay,使用

###### 源码地址
