# Tach

[简体中文](https://github.com/PaperCake-Studio/Tach/blob/main/README_zh.md) | [English](https://github.com/PaperCake-Studio/Tach/blob/main/README.md) | [繁体中文](https://github.com/PaperCake-Studio/Tach/blob/main/README_zht.md)

- [Tach](#tach)
  - [簡介](#簡介)
  - [Tach 命令行參數](#tach-命令行參數)
    - [Tach 服務器](#tach-服務器)
    - [Tach 客戶端](#tach-客戶端)
  - [配置文件](#配置文件)
    - [服務器](#服務器)
    - [客戶端](#客戶端)
  - [版本](#版本)

## 簡介
[Tach](https://github.com/PaperCake-Studio/Tach) 是一個Java多線程聊天室，由 [@PaperCake-Studio](https://github.com/PaperCake-Studio) 制作

Tach 沒有 GUI，您可以使用命令行

## Tach 命令行參數
### Tach 服務器
`[] = 非必須`
`{} = 必須`

想開一個 Tach 聊天服務器，您需要查看以下參數:

```bash
java -jar server.jar [-c {配置文件}]
```

Tach 服務器會使用此文件（默認 ./config.properties ）

___

### Tach 客戶端
想連接到指定服務器，您需要查看以下參數

```bash
java -jar client.jar [-c {配置文件}]
```

Tach 客戶端會使用此文件（默認 ./clientConfig.properties ）

```bash
java -jar client.jar [{-c {配置文件}} [-o] {要發送的消息}]
```

這是一項新功能，改變要發送的消息，客戶端會連接，發送，最後斷開連接，這叫“一次發送”

```bash
java -jar client.jar [{-c {配置文件}} [-l | -r] [文件絕對路徑]]
```

這還是新功能，-l 將獲取最後一條消息，如果有提供，客戶端將把消息寫入文件。-r 會一直把獲取到的消息寫入文件，默認路徑為運行目錄下receivedData.log文件。

*註意： -o，-l，和-r都屬於靜默參數，使用時不會出現 Tach 圖標與連接信息。*


## 配置文件
### 服務器


```properties
port=10818
historyNum=30
lang=eng
```

`port` 服務器端口

`historyNum` 歴史記錄儲存數量

`lang` 語言

### 客戶端

```properties
serverAddr=localhost
serverPort=10818
username=Tach
silent=false
fileDir=./receivedData.log
lang=eng
```

`serverAddr` 連接的服務器地址

`serverPort` 連接的服務器端口

`username` 用戶名

`silent` 靜默模式

`fileDir` 使用 -l，-r 時，如果不提供文件，則使用這個

`lang` 語言

___

## 版本
所有版本在 [這裏](https://github.com/PaperCake-Studio/Tach/releases)
