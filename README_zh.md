# Tach

[简体中文](https://github.com/BlueStarrySky1/Tach/blob/main/README_zh.md) | [English](https://github.com/BlueStarrySky1/Tach/blob/main/README.md)

## What is Tach
[Tach](https://github.com/BlueStarrySky1/Tach) 是一个Java多线程聊天室，由 [@BlueStarrySky1](https://github.com/BlueStarrySky1) 制作

Tach 没有 GUI，您可以使用命令行

## Tach 命令行参数
### Tach 服务器
`[] = 非必须`
`{} = 必须`

想开一个 Tach 聊天服务器，您需要查看以下参数:

`java -jar server.jar [端口]`

Tach 服务器会在指定的端口开启（默认10818）

___

例子：将服务器开在 7777 端口

`java -jar server.jar 7777`

看见 Tach 图标后，服务器开启成功

___

### Tach 客户端
想连接到指定服务器，您需要查看以下参数

`java -jar client.jar [地址] [端口] [用户名]`

Tach 客户端会连接到指定的地址与端口（默认连接 localhost:10818）

Tach 客户端同样也会使用指定的用户名（默认 Anonymous）

`java -jar client.jar [{地址} {端口} {用户名} [-o] {要发送的消息}]`

这是一项新功能，改变要发送的消息，客户端会连接，发送，最后断开连接，这叫“一次发送”

`java -jar client.jar [{地址} {端口} {用户名} [-l | -r] [文件绝对路径]]`

这还是新功能，-l 将获取最后一条消息，如果有提供，客户端将把消息写入文件。-r 会一直把获取到的消息写入文件，默认路径为运行目录下receivedData.log文件。

___

例子：以用户 “Tach” 的身份连接到 127.0.0.1:7777 服务器

`java -jar client.jar 127.0.0.1 7777 Tach`

看见 Tach 图标与连接信息后，连接成功



## 版本
所有版本在 [这里](https://github.com/BlueStarrySky1/Tach/releases)

___

### 1.2 Release
日志:

-三个新的参数:

[-o {消息}] - 连接，发送，最后断开。

[-l [文件路径]] - 接收最后一条消息，如果提供，将写入文件（默认路径为运行目录下receivedData.log文件）。

[-r [文件路径]] - 一直将获取的消息写入文件（默认路径为运行目录下receivedData.log文件）。

[下载](https://github.com/BlueStarrySky1/Tach/releases/tag/Release)

___

### 1.1 Pre Release
日志:

-Tach 1.1 Pre-Release:

-增加AES加密功能

-增加用户名功能

-增加版本核对功能

[下载](https://github.com/BlueStarrySky1/Tach/releases/tag/Pre-Release)

___

### 1.0 Snapshot
日志:

-Tach 1.0 Snapshot:

-增加多线程处理

-使用命令行

[下载](https://github.com/BlueStarrySky1/Tach/releases/tag/Snapshot)
