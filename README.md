# Tach

## What is Tach
[Tach](https://github.com/BlueStarrySky1/Tach) is a Java Powered Multi-thread Chat Room made by [@BlueStarrySky1](https://github.com/BlueStarrySky1)

It doesn't has GUI, it can only executed by command prompts

___

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
___
For example, you want to connect to local 7777 server and use the name 'Tach', you run

`java -jar client.jar 127.0.0.1 7777 Tach`

And when you see the icon and connected info, you have connected

___

## Releases
See all releases [here](https://github.com/BlueStarrySky1/Tach/releases)

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
