# Tach

[简体中文](https://github.com/PaperCake-Studio/Tach/blob/main/README_zh.md) | [English](https://github.com/PaperCake-Studio/Tach/blob/main/README.md) | [繁体中文](https://github.com/PaperCake-Studio/Tach/blob/main/README_zht.md)

- [Tach](#tach)
  - [What is Tach](#what-is-tach)
  - [Tach's Command Prompts](#tachs-command-prompts)
    - [Tach Server](#tach-server)
    - [Tach Client](#tach-client)
  - [Properties File](#properties-file)
    - [Server](#server)
    - [Client](#client)
  - [Releases](#releases)

## What is Tach
[Tach](https://github.com/PaperCake-Studio/Tach) is a Java Powered Multi-thread Chat Room made by [@PaperCake-Studio](https://github.com/PaperCake-Studio)

It doesn't has GUI, it can only executed by command prompts

## Tach's Command Prompts
### Tach Server
`[] = not required`
`{} = required`

To Start the server, you need to run this:

```bash
java -jar server.jar [-c {Config Path}]
```

Tach Server will use the config you give it, default ./config.properties

___

### Tach Client
To Connect to a specific host, you need to run this

```bash
java -jar client.jar [-c {Config Path}]
```

The client will use the config you give it, default ./clientConfig.properties


```bash
java -jar client.jar [{-c {Config Path}} [[-o] {msg}]]
```

This is a new feature, by changing the msg, the client will join, send, and then quit the server, this is called one-time send.

```bash
java -jar client.jar [{-c {Config Path}} [[-l | -r] [fileDir]]]
```

This is also a new feature, "-l" receives the last message, if provided, it will write in the file. "-r" will keep receiving message and write it in the file, default .\receivedData.log.

*Remind: -o, -l, and -r are silent conditions, as you run them, they won't show any icons or infos.*


## Properties File
### Server


```properties
port=10818
historyNum=30
lang=eng
```

`port` is the port that the server will use.

`historyNum` is the number of history the server will save.

`lang` is the language you use.

### Client

```properties
serverAddr=localhost
serverPort=10818
username=Tach
silent=false
fileDir=./receivedData.log
lang=eng
```

`serverAddr` is the address of the server you want to connect.

`serverPort` is the port of that server.

`username` is the username you want.

`silent` is the switch of client silent mode.

`fileDir` is when you don't give a file directory when client asks, client uses this.

`lang` is the language you use.

___

## Releases
See all releases [here](https://github.com/PaperCake-Studio/Tach/releases)
