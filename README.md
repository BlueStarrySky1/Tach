# Tach

[简体中文](https://github.com/BlueStarrySky1/Tach/blob/main/README_zh.md) | [English](https://github.com/BlueStarrySky1/Tach/blob/main/README.md)

## What is Tach
[Tach](https://github.com/BlueStarrySky1/Tach) is a Java Powered Multi-thread Chat Room made by [@BlueStarrySky1](https://github.com/BlueStarrySky1)

It doesn't has GUI, it can only executed by command prompts

## Tach's Command Prompts
### Tach Server
`[] = not required`
`{} = required`

To Start the server, you need to run this:

`java -jar server.jar [port]`

Tach Server will bind on the port you give it, default 10818

___

For example, you want to open the server on port 7777, you need to run

`java -jar server.jar 7777`

And when you see the icon, the server started on port 7777

___

### Tach Client
To Connect to a specific host, you need to run this

`java -jar client.jar [addr] [port] [username]`

The client will connect to the addr:port you give it, default localhost:10818

The client will also use the username you give, default Anonymous

`java -jar client.jar [{addr} {port} {username} [-o] {msg}]`

This is a new feature, by changing the msg, the client will join, send, and then quit the server, this is called one-time send.

`java -jar client.jar [{addr} {port} {username} [-l | -r] [fileDir]]`

This is also a new feature, "-l" receives the last message, if provided, it will write in the file. "-r" will keep receiving message and write it in the file, default .\receivedData.log.

___

For example, you want to connect to local 7777 server and use the name 'Tach', you run

`java -jar client.jar 127.0.0.1 7777 Tach`

And when you see the icon and connected info, you have connected



## Releases
See all releases [here](https://github.com/BlueStarrySky1/Tach/releases)

___

### 1.2 Release
Changelog:

-Three new conditions has been added:

[-o {msg}] - connect to the server, send the message and quit.

[-l [file]] - receive the last message, if you give a file name, it will write the message to the file (default .\receivedData.log).

[-r [file]] - receiver, keep receiving message and write in the file (default .\receivedData.log).

[Download 1.2 Release](https://github.com/BlueStarrySky1/Tach/releases/tag/Release)

___

### 1.1 Pre Release
Changelog:

-Tach 1.1 Pre-Release:

-Add AES encrypt function

-Add username function

-Add version check function

[Download 1.1 Pre-Release](https://github.com/BlueStarrySky1/Tach/releases/tag/Pre-Release)

___

### 1.0 Snapshot
Changelog:

-Tach 1.0 Snapshot:

-Multi-thread forwarding

-Command conditions required

[Download 1.0 Snapshot](https://github.com/BlueStarrySky1/Tach/releases/tag/Snapshot)
