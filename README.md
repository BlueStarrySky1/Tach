# Tach

[简体中文](https://github.com/BlueStarrySky1/Tach/blob/main/README_zh.md) | [English](https://github.com/BlueStarrySky1/Tach/blob/main/README.md)

## What is Tach
[Tach](https://github.com/BlueStarrySky1/Tach) is a Java Powered Multi-thread Chat Room made by [@PaperCake-Studio](https://github.com/PaperCake-Studio)

It doesn't has GUI, it can only executed by command prompts

## Tach's Command Prompts
### Tach Server
`[] = not required`
`{} = required`

To Start the server, you need to run this:

```bash
java -jar server.jar [port]
```

Tach Server will bind on the port you give it, default 10818

```bash
java -jar server.jar [{port} [{-h} {num}]]
```

By changing the number, you can control how many chat histories the server will save, default 30, 0 for no chat histories.

When a new client joins the server, it will receive chat histories and show like this: "History | Tach: Hi"

___

For example, you want to open the server on port 7777, and save 50 chats, you need to run

```bash
java -jar server.jar 7777 -h 50
```

And when you see the icon, the server started on port 7777

___

### Tach Client
To Connect to a specific host, you need to run this

```bash
java -jar client.jar [addr] [port] [username]
```

The client will connect to the addr:port you give it, default localhost:10818

The client will also use the username you give, default Anonymous

```bash
java -jar client.jar [{addr} {port} {username} [-o] {msg}]
```

This is a new feature, by changing the msg, the client will join, send, and then quit the server, this is called one-time send.

```bash
java -jar client.jar [{addr} {port} {username} [-l | -r] [fileDir]]
```

This is also a new feature, "-l" receives the last message, if provided, it will write in the file. "-r" will keep receiving message and write it in the file, default .\receivedData.log.

*Remind: -o, -l, and -r are silent conditions, as you run them, they won't show any icons or infos.*

___

For example, you want to connect to local 7777 server and use the name 'Tach', you run

```bash
java -jar client.jar 127.0.0.1 7777 Tach
```

And when you see the icon and connected info, you have connected.

___

## Releases
See all releases [here](https://github.com/BlueStarrySky1/Tach/releases)
