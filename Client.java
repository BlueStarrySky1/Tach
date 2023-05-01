package com.dqa.tach;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Client {
    static Socket socket;
    public static String icon = " _________  ________  ________  ___  ___     \n|\\___   ___\\\\   __  \\|\\   ____\\|\\  \\|\\  \\    \n\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\   \n     \\ \\  \\ \\ \\   __  \\ \\  \\    \\ \\   __  \\  \n      \\ \\  \\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \n       \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\n        \\|__|  \\|__|\\|__|\\|_______|\\|__|\\|__|\n";
    public static String info = "TACH Core (Server) v1.0 Snapshot\n";
    public static void main(String[] args) throws Exception{
        socket = new Socket(args[0],Integer.parseInt(args[1]));
        System.out.println(icon+info+"[Info] Connected to "+socket.getRemoteSocketAddress());
        Send send = new Send(socket);
        Receive receive = new Receive(socket);
        new Thread(send).start();
        new Thread(receive).start();
    }

}


class Send implements Runnable{
    //Get data from keyboard
    private BufferedReader br;
    private DataOutputStream dos;
    private boolean flag=true;

    public Send() {
        br=new BufferedReader(new InputStreamReader(System.in));
    }
    public Send(Socket socket){
        this();
        try{
            dos=new DataOutputStream(socket.getOutputStream());
        }catch (Exception e){
            flag=false;
            CloseUtil.CloseAll(dos,socket);
            e.printStackTrace();
        }
    }


    private String getMessage(){
        String str="";
        try{
            str=br.readLine();
        }catch (IOException e){
            flag=false;
            CloseUtil.CloseAll(br);
        }
        return str;
    }
    private void send(String str){
        try {
            dos.writeUTF(Client.socket.getLocalAddress()+": "+str);

            dos.flush();
        } catch (IOException e) {
            flag=false;
            CloseUtil.CloseAll(dos);
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (flag){
            this.send(getMessage());

        }
    }
}


class Receive implements Runnable{
    //Receive Data Stream
    private DataInputStream dis;
    private boolean flag=true;


    public Receive(Socket socket){
        try {
            dis = new DataInputStream(socket.getInputStream());
        }catch (Exception e){
            flag=false;
            CloseUtil.CloseAll(dis,socket);
        }
    }
    private String getMessage(){
        String str="";
        try {
            str=dis.readUTF();
        } catch (IOException e) {
            flag=false;
            CloseUtil.CloseAll(dis);
            e.printStackTrace();
        }
        return str;
    }
    @Override
    public void run() {
        while (flag){
            System.out.println(this.getMessage());
        }
    }
}