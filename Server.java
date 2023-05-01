package com.dqa.tach;

import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    public static List<MyChannel> list=new ArrayList<>();
    public static String icon = " _________  ________  ________  ___  ___     \n|\\___   ___\\\\   __  \\|\\   ____\\|\\  \\|\\  \\    \n\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\   \n     \\ \\  \\ \\ \\   __  \\ \\  \\    \\ \\   __  \\  \n      \\ \\  \\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \n       \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\n        \\|__|  \\|__|\\|__|\\|_______|\\|__|\\|__|\n";
    public static String info = "TACH Core (Server) v1.0 Snapshot\n";
    public static void main(String[] args) throws Exception {
        //Create Serversocket object
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        System.out.println(icon+info+"[Info] Server Bound on port "+args[0]+"\n[Info] Local IP Addr: "+serverSocket.getLocalSocketAddress());
        while (true){

            //Add clients into the list and remind
            Socket client = serverSocket.accept();
            System.out.println("[Info] Client "+client.getInetAddress()+" Joined the Server");
            MyChannel myChannel = new MyChannel(client);
            list.add(myChannel);
            new Thread(myChannel).start();
        }
    }
}

class CloseUtil {
    public static void CloseAll(Closeable... closeable){
        for(Closeable c:closeable){
            if (c != null) {
                try {
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class MyChannel implements Runnable{
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean flag=true;

    public MyChannel(Socket socket) {
        try{
            dis=new DataInputStream(socket.getInputStream());
            dos=new DataOutputStream(socket.getOutputStream());
        }catch (IOException e){
            flag=false;
            CloseUtil.CloseAll(dis,dos);
        }
    }
    //Method of Receiving Data
    private String receive(){
        String str="";
        try{
            str= dis.readUTF();
            System.out.println("[Msg] Message Received: \""+str+"\"");
        }catch (IOException e){
            flag=false;
            CloseUtil.CloseAll(dis,dos);
            Server.list.remove(this);
        }
        return str;
    }
    //Method of Sending Data
    private void send(String str){
        try {
            if (str != null && str.length() != 0) {
                dos.writeUTF(str);
                dos.flush();
                System.out.println("[Info] Message Forwarded");
            }
        }catch (Exception exception){
            flag=false;
            CloseUtil.CloseAll(dos,dis);
            Server.list.remove(this);
        }
    }
    //Method of Transforming / Exchanging / Forwarding Data
    private void sendToOther(){
        String str=this.receive();
        List<MyChannel> list = Server.list;
        for (MyChannel other:list) {
            if(other==list){
                continue;//Not send data to myself
            }
            //Transforming / Exchanging / Forwarding Data to other clients
            other.send(str);
        }
    }

    @Override
    public void run() {
        while (flag){
            sendToOther();
        }
    }
}

