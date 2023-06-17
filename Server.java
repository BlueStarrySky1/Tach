

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

public class server {
    private static String key = null;
    private static String initVector = null;
    static ArrayList<Socket> clientSockets = new ArrayList<>();
    static ArrayList<String> users = new ArrayList<>();
    public static String icon = " _________  ________  ________  ___  ___     \n|\\___   ___\\\\   __  \\|\\   ____\\|\\  \\|\\  \\    \n\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\   \n     \\ \\  \\ \\ \\   __  \\ \\  \\    \\ \\   __  \\  \n      \\ \\  \\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \n       \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\n        \\|__|  \\|__|\\|__|\\|_______|\\|__|\\|__|\n";
    public static String info = "TACH Core (Server) v1.3 Release\n";
    public static String ver = "1.3";
    public static String lastMsg = null;
    public static Boolean chatHistory = true;
    public static Integer historyNum = 30;
    public static List<String> historyList = new ArrayList<>();
    //key generation
    public static String getRandomString(int length) {

        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        //customization
        historyNum = args.length > 2 && args[1].equals("-h") ? Integer.parseInt(args[2]) : 30;
        chatHistory = historyNum.equals(0) ? false : true;
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 10818;
        //info output
        System.out.println(icon+info);
        System.out.println("Starting server on port " + port);
        //gene key
        key = getRandomString(16);
        //System.out.println(key);
        //server open
        ServerSocket serverSocket = new ServerSocket(port);

        while(true) {
            //accept and open threads
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    private static class ClientHandler implements Runnable {
        Boolean clientReady = false;
        Socket socket;
        BufferedReader reader;
        PrintWriter writer;

        String name;

        //joined
        public ClientHandler(Socket socket) throws Exception {

            this.socket = socket;
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inputStreamReader);

            writer = new PrintWriter(socket.getOutputStream(), true);

            // get username
            name = reader.readLine();
            users.add(name);
            clientSockets.add(socket);
            System.out.println("Client "+socket.getRemoteSocketAddress()+" joined, username: "+name);
            sendUsers();
        }

        //receive
        @Override
        public void run() {

            String message;

            try {


                    while ((message = reader.readLine()) != null) {
                        if (!message.equals("/close") && !message.equals("/lastMsg") && !message.equals("/ready")) {
                            System.out.println("Message Received and Handled：" + message);
                            lastMsg = message;
                            if(chatHistory) { chatHistory(message); }

                        }
                        sendMessage(message);
                    }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                users.remove(name);
                clientSockets.remove(socket);
                sendUsers();
            }
        }

        //send userlist
        private void sendUsers() {
            String userList = "USERLIST";
            for(String user : users) {
                userList += "," + user;
            }

            for(Socket soc : clientSockets) {
                try {
                    PrintWriter writer = new PrintWriter(soc.getOutputStream(), true);
                    writer.println("/key "+key+"|"+ver);
                    //Thread.sleep(1000);
                    //sendChatHistory();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        //save chat
        private void chatHistory(String message) {
            if (historyList.size() == historyNum) {
                historyList.remove(0);
                historyList.add(message);
            }else {
                historyList.add(message);

            }
            //System.out.println(historyList);
        }

        //send history
        private void sendChatHistory() {
            int num = historyList.size() < historyNum ? historyList.size() : historyNum;
            for (int i = 0; i < num; i++) {
                writer.println("/history"+historyList.get(i));
            }

        }

        //forward and action for specific msg
        private void sendMessage(String message) {


            String response = null;
            try {
                response = message;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for(Socket soc : clientSockets) {
                if(message.equals("/close")) {
                    try {
                        socket.close();
                        System.out.println("Socket "+socket.getRemoteSocketAddress()+" Disconnected");
                        clientReady = false;
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (message.equals("/lastMsg")) {
                    try {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println(lastMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (message.equals("/ready")) {
                    clientReady = true;
                    sendChatHistory();
                    return;
                }
                if(soc != socket) {
                    try {
                        PrintWriter writer = new PrintWriter(soc.getOutputStream(), true);
                        writer.println(response);
                        System.out.println("Forwarded");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }

        //encrypt
        public static String encrypt(String sSrc, String sKey) throws Exception {
            if (sKey == null) {
                System.err.print("Key is null");
                return null;
            }
            if (sKey.length() != 16) {
                System.err.print("Key length must be 16 bytes");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        }
    }
}
