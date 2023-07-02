# Tach

[简体中文](https://github.com/PaperCake-Studio/Tach/blob/main/README_zh.md) | [English](https://github.com/PaperCake-Studio/Tach/blob/main/README.md)

- [Tach](#tach)
  - [简介](#简介)
  - [Tach 命令行参数](#tach-命令行参数)
    - [Tach 服务器](#tach-服务器)
    - [Tach 客户端](#tach-客户端)
  - [配置文件](#配置文件)
    - [服务器](#服务器)
    - [客户端](#客户端)
  - [版本](#版本)

## 简介
[Tach](https://github.com/PaperCake-Studio/Tach) 是一个Java多线程聊天室，由 [@PaperCake-Studio](https://github.com/PaperCake-Studio) 制作

Tach 没有 GUI，您可以使用命令行

## Tach 命令行参数
### Tach 服务器
`[] = 非必须`
`{} = 必须`

想开一个 Tach 聊天服务器，您需要查看以下参数:

```bash
java -jar server.jar [-c {配置文件}]
```

Tach 服务器会使用此文件（默认 ./config.properties ）

___

### Tach 客户端
想连接到指定服务器，您需要查看以下参数

```bash
java -jar client.jar [-c {配置文件}]
```

Tach 客户端会使用此文件（默认 ./clientConfig.properties ）

```bash
java -jar client.jar [{-c {配置文件}} [-o] {要发送的消息}]
```

这是一项新功能，改变要发送的消息，客户端会连接，发送，最后断开连接，这叫“一次发送”

```bash
java -jar client.jar [{-c {配置文件}} [-l | -r] [文件绝对路径]]
```

这还是新功能，-l 将获取最后一条消息，如果有提供，客户端将把消息写入文件。-r 会一直把获取到的消息写入文件，默认路径为运行目录下receivedData.log文件。

*注意： -o，-l，和-r都属于静默参数，使用时不会出现 Tach 图标与连接信息。*


## 配置文件
### 服务器


```properties
port=10818
historyNum=30
lang=eng
```

`port` 服务器端口

`historyNum` 历史记录储存数量

`lang` 语言

### 客户端

```properties
serverAddr=localhost
serverPort=10818
username=Tach
silent=false
fileDir=./receivedData.log
lang=eng
```

`serverAddr` 连接的服务器地址

`serverPort` 连接的服务器端口

`username` 用户名

`silent` 静默模式

`fileDir` 使用 -l，-r 时，如果不提供文件，则使用这个

`lang` 语言

___

## 版本
所有版本在 [这里](https://github.com/PaperCake-Studio/Tach/releases)
