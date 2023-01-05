# 1.RocketMQ简介

## (1) RocketMQ 特点
- 是一个队列模型的消息中间件，具有高性能、高可靠、高实时、分布式特点。
- Producer、Consumer、队列都可以分布式。
- Producer向一些队列轮流发送消息，队列集合称为Topic，Consumer如果做广播消费，则一个consumer实例消费这个Topic对应的所有队列，如果做集群消费，则多个Consumer实例平均消费这个topic对应的队列集合。
- 能够保证严格的消息顺序
- 提供丰富的消息拉取模式
- 高效的订阅者水平扩展能力
- 实时的消息订阅机制
- 亿级消息堆积能力
- 较少的依赖

## (2) RocketMQ 物理部署结构
![image](http://img3.tbcdn.cn/5476e8b07b923/TB18GKUPXXXXXXRXFXXXXXXXXXX)

如上图所示， RocketMQ的部署结构有以下特点：

- Name Server是一个几乎无状态节点，可集群部署，节点之间无任何信息同步。
- Broker部署相对复杂，Broker分为Master与Slave，一个Master可以对应多个Slave，但是一个Slave只能对应一个Master，Master与Slave的对应关系通过指定相同的BrokerName，不同的BrokerId来定义，BrokerId为0表示Master，非0表示Slave。Master也可以部署多个。每个Broker与Name Server集群中的所有节点建立长连接，定时注册Topic信息到所有Name Server。
- Producer与Name Server集群中的其中一个节点（随机选择）建立长连接，定期从Name Server取Topic路由信息，并向提供Topic服务的Master建立长连接，且定时向Master发送心跳。Producer完全无状态，可集群部署。
- Consumer与Name Server集群中的其中一个节点（随机选择）建立长连接，定期从Name Server取Topic路由信息，并向提供Topic服务的Master、Slave建立长连接，且定时向Master、Slave发送心跳。Consumer既可以从Master订阅消息，也可以从Slave订阅消息，订阅规则由Broker配置决定。

## (3) RocketMQ 逻辑部署结构

![image](http://img3.tbcdn.cn/5476e8b07b923/TB1lEPePXXXXXX8XXXXXXXXXXXX)

如上图所示，RocketMQ的逻辑部署结构有Producer和Consumer两个特点。

### Producer Group

- 用来表示一个发送消息应用，一个Producer Group下包含多个Producer实例，可以是多台机器，也可以是一台机器的多个进程，或者一个进程的多个Producer对象。一个Producer Group可以发送多个Topic消息

Producer Group作用如下：

1. 标识一类Producer
2. 可以通过运维工具查询这个发送消息应用下有多个Producer实例
3. 发送分布式事务消息时，如果Producer中途意外宕机，Broker会主动回调Producer Group内的任意一台机器来确认事务状态。

### Consumer Group

- 用来表示一个消费消息应用，一个Consumer Group下包含多个Consumer实例，可以是多台机器，也可以是多个进程，或者是一个进程的多个Consumer对象。一个Consumer Group下的多个Consumer以均摊方式消费消息，如果设置为广播方式，那么这个Consumer Group下的每个实例都消费全量数据。

## (4) RocketMQ 数据存储结构

![image](http://img3.tbcdn.cn/5476e8b07b923/TB1Ali2PXXXXXXuXFXXXXXXXXXX)

# 2.CentOS7 部署

## (1) 下载&安装

### 依赖安装
```shell
# 安装 jdk1.8
> yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel
```

配置JDK环境
```
> vim /etc/profile

# set jdk path
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.242.b08-0.el7_7.x86_64
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:$CLASSPATH
export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
export PATH=$PATH:${JAVA_PATH}

> source /etc/profile
```

否则执行 bin/mqadmin 等RocketMQ命令会报错
```
> sh bin/mqadmin topicList -n 192.168.160.2:9876
RocketMQLog:WARN No appenders could be found for logger (io.netty.util.internal.PlatformDependent0).
RocketMQLog:WARN Please initialize the logger system properly.
org.apache.rocketmq.tools.command.SubCommandException: TopicListSubCommand command failed
	at org.apache.rocketmq.tools.command.topic.TopicListSubCommand.execute(TopicListSubCommand.java:113)
	at org.apache.rocketmq.tools.command.MQAdminStartup.main0(MQAdminStartup.java:139)
	at org.apache.rocketmq.tools.command.MQAdminStartup.main(MQAdminStartup.java:90)
```

### RocketMQ安装
```shell
# https://mirrors.tuna.tsinghua.edu.cn/apache/rocketmq 查看版本 目前使用 4.6.1

> wget https://mirrors.tuna.tsinghua.edu.cn/apache/rocketmq/4.6.1/rocketmq-all-4.6.1-bin-release.zip

> uzip rocketmq-all-4.6.1-bin-release.zip

> cd rocketmq-all-4.6.1-bin-release
```

## (2) 配置
使用默认提供的 conf/broker.conf 配置文件
```shell
> vim conf/broker.conf

brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
fileReservedTime = 48
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
```

指定 broker 自己监听的 IP 地址，以及 namesrv 的地址和端口，不然 rocketmq-console、mqadmin 客户端等会因为跨域而连接不上，只需要在`conf/broker.conf` 配置文件中添加 brokerIP1 和 namesrvAddr 配置即可：
```shell
# 支持跨域
brokerIP1 = 192.168.160.2
namesrvAddr = 192.168.160.2:9876
```

关闭selinux
```
# 首先查看 SELinux功能是否开启

> getenforce

# 如果显示Permissive 或者 Disabled 该步骤直接跳过，如果是enforcing ，进行下一步

> vim /etc/selinux/config # 或者 vim /etc/sysconfig/selinux

# 设置 SELINUX=enforcing 为 SELINUX=permissive（/disabled）

setenforce 0 # 让设置生效

getenforce # 查看
```

## (3) 启动&测试&停止 服务

### Start Name Server
```shell
> nohup sh bin/mqnamesrv &
> tail -f ~/logs/rocketmqlogs/namesrv.log
```

### Start Broker
```shell
> nohup sh bin/mqbroker -n localhost:9876 -c conf/broker.conf &
> tail -f ~/logs/rocketmqlogs/broker.log 
```

### Send & Receive Messages
```shell
> export NAMESRV_ADDR=localhost:9876
> sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
SendResult [sendStatus=SEND_OK, msgId= ...

> sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
ConsumeMessageThread_%d Receive New Messages: [MessageExt...
```

### Shutdown Servers
```shell
> sh bin/mqshutdown broker
The mqbroker(36695) is running...
Send shutdown request to mqbroker(36695) OK

> sh bin/mqshutdown namesrv
The mqnamesrv(36664) is running...
Send shutdown request to mqnamesrv(36664) OK
```

## (4) 端口相关问题

### 端口说明
- 9876  : NameServer 端口
- 10909 : vip 通道端口 | fastListenPort
- 10910 :
- 10911 : 核心端口 | Broker 对外服务的监听端口 | 非 vip 通道端口 | listenPort
- 10912 : xxx | haListenPort

```shell
> netstat -ntlp | grep java
tcp6       0      0 :::9876                 :::*                    LISTEN      2247/java           
tcp6       0      0 :::10909                :::*                    LISTEN      2277/java           
tcp6       0      0 :::10911                :::*                    LISTEN      2277/java           
tcp6       0      0 :::10912                :::*                    LISTEN      2277/java
```

### 防火墙设置
```
# 根据实际情况放开端口，NameServer 端口、Broker 监听端口，必设置
firewall-cmd --zone=public --add-port=9876/tcp --permanent
firewall-cmd --zone=public --add-port=10911/tcp --permanent
firewall-cmd --reload
```

### 外网端口映射

NameServer 的 9876 端口可正常映射

Broker 的 4 个端口需要内外网一致，即监听 13174，则该端口映射出去的端口也是 13174

broker 还会监听另外几个端口，同样的都是本地监听和映射的端口相同即可。
```shell
# 公网 IP
brokerIP1 = xx.xx.xx.xx
# 对应 broker 监听端口，默认为10911，调整后，影响4个端口
# 10909/10910/10911/10912 -> 13172/13173/13174/13175
listenPort = 13174

namesrvAddr = 192.168.160.2:9876
```

## (5) 待研究
client - consumer 端断开，broker log 会有如下Error日志，强迫症表示后续继续研究
```
> tail -f ~/logs/rocketmqlogs/broker.log 
2020-03-05 10:24:35 INFO NettyEventExecutor - NETTY EVENT: remove channel[] from ProducerManager groupChannelTable, producer group: CLIENT_INNER_PRODUCER
2020-03-05 10:24:35 WARN NettyEventExecutor - NETTY EVENT: remove not active channel[ClientChannelInfo [channel=[id: 0x63017fd9, L:/192.168.160.2:10911 ! R:/192.168.150.144:54648], clientId=192.168.126.1@DEFAULT, language=JAVA, version=335, lastUpdateTimestamp=1583375060273]] from ConsumerGroupInfo groupChannelTable, consumer group: Producer
2020-03-05 10:24:35 INFO NettyEventExecutor - unregister consumer ok, no any connection, and remove consumer group, Producer
2020-03-05 10:24:51 WARN PullMessageThread_4 - the consumer's group info not exist, group: Producer
2020-03-05 10:24:51 WARN PullMessageThread_5 - the consumer's group info not exist, group: Producer
2020-03-05 10:24:51 WARN PullMessageThread_1 - the consumer's group info not exist, group: Producer
2020-03-05 10:24:51 WARN PullMessageThread_3 - the consumer's group info not exist, group: Producer
2020-03-05 10:24:51 ERROR NettyServerNIOSelector_3_1 - processRequestWrapper response to /192.168.150.144:54648 failed
java.nio.channels.ClosedChannelException: null
	at io.netty.channel.AbstractChannel$AbstractUnsafe.write(...)(Unknown Source) ~[netty-all-4.0.42.Final.jar:4.0.42.Final]
2020-03-05 10:24:51 ERROR NettyServerNIOSelector_3_1 - RemotingCommand [code=11, language=JAVA, version=335, opaque=1040, flag(B)=0, remark=null, extFields={queueId=1, maxMsgNums=32, sysFlag=2, suspendTimeoutMillis=15000, commitOffset=0, topic=newTopic, queueOffset=75, expressionType=TAG, subVersion=1583374069385, consumerGroup=Producer}, serializeTypeCurrentRPC=JSON]
2020-03-05 10:24:51 ERROR NettyServerNIOSelector_3_1 - RemotingCommand [code=24, language=JAVA, version=335, opaque=1040, flag(B)=1, remark=the consumer's group info not exist
See http://rocketmq.apache.org/docs/faq/ for further details., extFields=null, serializeTypeCurrentRPC=JSON]
```

# 3.RocketMQ-client java demo

## (1) 创建 Spring boot 项目
在pom.xml中添加(使用jdk1.8)
```
<!-- RocketMq客户端相关依赖 -->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.6.1</version>
</dependency>
```
[resources文件中properties配置](https://github.com/JasonLiGit/rocketMQDemo/blob/master/src/main/resources/application.properties)

## (2) 代码编写

[Producer.java](https://github.com/JasonLiGit/rocketMQDemo/blob/master/src/main/java/com/xiaoi/rocketmq/demo/Producer.java)

[Consumer.java](https://github.com/JasonLiGit/rocketMQDemo/blob/master/src/main/java/com/xiaoi/rocketmq/demo/Consumer.java)

红色标识符，alt + enter 引入相应的包


# 4.参考链接

[Apache RocketMQ 官方文档](https://rocketmq.apache.org/docs/quick-start/)

[阿里中间件博客-RocketMQ](http://jm.taobao.org/2017/01/12/rocketmq-quick-start-in-10-minutes/)

[RocketMQ命令使用详解](https://blog.csdn.net/aa1215018028/article/details/83302438)

[RocketMQ Client踩坑](https://cloud.tencent.com/developer/article/1380139)
