

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class server {
    private static String key = null;
    private static String initVector = null;
    static ArrayList<Socket> clientSockets = new ArrayList<>();
    static ArrayList<String> users = new ArrayList<>();
    public static String icon = " _________  ________  ________  ___  ___     \n|\\___   ___\\\\   __  \\|\\   ____\\|\\  \\|\\  \\    \n\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\   \n     \\ \\  \\ \\ \\   __  \\ \\  \\    \\ \\   __  \\  \n      \\ \\  \\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \n       \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\n        \\|__|  \\|__|\\|__|\\|_______|\\|__|\\|__|\n";
    public static String info = "TACH Core (Server) v1.1 Pre-Release\n";
    public static String ver = "1.1";
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

        // Customize port
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 10818;
        System.out.println(icon+info);
        System.out.println("Starting server on port " + port);
        key = getRandomString(16);

        ServerSocket serverSocket = new ServerSocket(port);

        while(true) {

            Socket socket = serverSocket.accept();


            ClientHandler clientHandler = new ClientHandler(socket);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    private static class ClientHandler implements Runnable {

        Socket socket;
        BufferedReader reader;
        PrintWriter writer;

        String name;

        public ClientHandler(Socket socket) throws Exception {

            this.socket = socket;
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inputStreamReader);

            writer = new PrintWriter(socket.getOutputStream(), true);

            // 获取用户名
            name = reader.readLine();
            users.add(name);
            clientSockets.add(socket);
            System.out.println("Client "+socket.getRemoteSocketAddress()+" joined, username: "+name);
            sendUsers();
        }

        @Override
        public void run() {

            String message;

            try {
                while((message = reader.readLine()) != null) {

                    System.out.println("Message Received and Handled：" + message);

                    sendMessage(message);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                users.remove(name);
                clientSockets.remove(socket);
                sendUsers();
            }
        }

        private void sendUsers() {
            String userList = "USERLIST";
            for(String user : users) {
                userList += "," + user;
            }

            for(Socket soc : clientSockets) {
                try {
                    PrintWriter writer = new PrintWriter(soc.getOutputStream(), true);
                    writer.println("/key "+key+"|"+ver);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        private void sendMessage(String message) {


            String response = null;
            try {
                response = message;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            for(Socket soc : clientSockets) {
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
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        }
    }
}
