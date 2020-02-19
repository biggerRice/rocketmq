## Apache RocketMQ [![Build Status](https://travis-ci.org/apache/rocketmq.svg?branch=master)](https://travis-ci.org/apache/rocketmq) [![Coverage Status](https://coveralls.io/repos/github/apache/rocketmq/badge.svg?branch=master)](https://coveralls.io/github/apache/rocketmq?branch=master)

各版本下载地址：

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.rocketmq/rocketmq-all/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Corg.apache.rocketmq)
[![GitHub release](https://img.shields.io/badge/release-download-orange.svg)](https://rocketmq.apache.org/dowloading/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

**[Apache RocketMQ](https://rocketmq.apache.org) 是一个延迟低、高性能、灵活性高，高可用万亿级容量分布式消息流平台**

本版本为RocketMq4.3.2
,它提供多种特色功能:

* 发布/订阅消息模型
* 定时、延迟消息
* 通过时间或偏移量 消息回溯
* 日志收集
* 大数据整合
* 在同一队列中支持可靠的FIFO和严格有序的消息传递
* 高校的拉取、推送消费模型
* 支持多种消费协议，如JMS 、OpenMessaging
* 支持分布式可扩展的部署架构
* 轻量、快捷的批量消息传输
* 多种消息过滤器，如tag
* 丰富的后台管理，如配置、指标、监控等

----------

目录结构说明：

* broker：RocketMQ的Broker相关的代码，这里的代码可以用来启动Broker进程 
* client：里面就是RocketMQ的Producer、Consumer这些客户端的代码，生产消息、消费消息的代码
* common：这里放的是一些公共的代码 
* dev：开发相关的一些信息 
* distribution：用来部署RocketMQ的一些东西，比如bin目录 ，conf目录，等等 
* example：RocketMQ的一些例子，适合入门 
* filter：RocketMQ的一些过滤器的东西 
* logappender和logging：RocketMQ的日志打印相关
* namesvr：NameServer的源码 
* openmessaging：这是开放消息标准
* remoting：RocketMQ的远程网络通信模块的代码，基于netty实现
* srvutil：项目中工具类 
* store：消息在Broker上进行存储相关的一些源码
* style、test、tools：这里放的是checkstyle代码检查的东西，一些测试相关的类，还有就是tools里放的一些命令行监控工具类

----------

## 源码文章目录
 
* 1.GitHub 拉取RocketMq 源码并部署到IDEA 
* 2.IDEA Debug启动NameServer 
* 3.IDEA Debug启动Broker 
* 4.NameServer解析配置文件初始化过程



----------

## 文档链接
* Mailing Lists: <https://rocketmq.apache.org/about/contact/>
* Home: <https://rocketmq.apache.org>
* Docs: <https://rocketmq.apache.org/docs/quick-start/>
* Issues: <https://github.com/apache/rocketmq/issues>
* Ask: <https://stackoverflow.com/questions/tagged/rocketmq>
* Slack: <https://rocketmq-invite-automation.herokuapp.com/>
 

----------

## Apache RocketMQ Community
* [RocketMQ Community Projects](https://github.com/apache/rocketmq-externals)
