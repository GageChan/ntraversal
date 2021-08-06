## 简介
Ntraversl一款基于netty实现的内网穿透工具。依赖netty核心包、hutool工具包、log4j日志包，轻量无过度依赖。

如果你感兴趣，请点击[Star](https://github.com/GageChan/ntraversal)。

## 特征

- [x] 代码简洁，没有过度依赖。
- [x] 支持HTTP、TCP协议穿透。包含但不局限于SSH协议、MYSQL协议、REDIS协议。
- [x] 自定义协议处理tcp半包粘包。
- [x] 灵活部署。
- [ ] 对UDP协议的支持。
- [ ] 可视化界面。
- [ ] 服务端支持docker部署。



## USAGE

**1.环境准备**

需要一台具备公网ip的服务器。server端与agent端均需要jre1.8+运行环境，[download](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)。

```shell
# GET START.
wget https://github.com/GageChan/ntraversal/releases/download/1.0/ntraversal_1.0.zip
```

**2.server端**

```shell
java -jar ntraversal-server-1.0.jar -ap 9527 -pp 9000
# -ap 指定服务端启动端口，默认9000
# -pp 指定外部访问端口
```

**3.agent端**

```shell
# agent默认配置位于./config/ 支持自定义公网服务端启动ip、端口。以及本地期望被代理的tcp端口
java -jar ntraversal-agent-1.0.jar
```



## CHANGE_LOG

### V1.0.0

- 自定义tcp通信协议。
- 支持自定义启动配置。
- 全链路通过内网穿透主流程。

## 特别鸣谢

- [Netty](https://github.com/netty/netty)
- [hutool](https://github.com/dromara/hutool)