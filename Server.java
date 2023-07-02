

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.util.*;

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
    public static Properties langKeyConf = null;
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
        //read config
        String configPath = args.length > 1 && args[0].equals("-c") ? args[1] : "./config.properties";
        Properties config = new Properties();
        BufferedReader configBR = new BufferedReader(new FileReader(configPath));
        config.load(configBR);
        int port = config.getProperty("port") != null ? Integer.parseInt(config.getProperty("port")) : 10818;
        historyNum = config.getProperty("historyNum") != null ? Integer.parseInt(config.getProperty("historyNum")) : 30;
        chatHistory = historyNum.equals(0) ? false : true;
        //read language file path
        Properties langConf = new Properties();
        BufferedReader langBR = new BufferedReader(new FileReader("./lang/lang.properties"));
        langConf.load(langBR);
        String lang = config.getProperty("lang");
        String langFile = langConf.getProperty(config.getProperty("lang"));
        langBR.close();
        configBR.close();
        //read language file
        langKeyConf = new Properties();
        BufferedReader langKeyBR = new BufferedReader(new FileReader(langFile));
        langKeyConf.load(langKeyBR);

        //info output
        System.out.println(icon);

        System.out.println(langKeyConf.getProperty("info")+" "+ver+" "+langKeyConf.getProperty("release")+" "+langKeyConf.getProperty("lang"));

        System.out.println(langKeyConf.getProperty("serverPort")+port);
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
            System.out.println(langKeyConf.getProperty("client")+" "+socket.getRemoteSocketAddress()+" "+langKeyConf.getProperty("clientJoin")+name);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            PublicKey publicKey = (PublicKey) objectInputStream.readObject();
            //System.out.println(publicKey);
            writer.println("/key"+rsaEncrypt(key, publicKey));
            writer.println("/ver"+ver);
            sendUsers();
        }

        //receive
        @Override
        public void run() {

            String message;

            try {


                    while ((message = reader.readLine()) != null) {
                        if (!message.equals("/close") && !message.equals("/lastMsg") && !message.equals("/ready")) {
                            System.out.println(langKeyConf.getProperty("messageRecv") + message);
                            lastMsg = message;
                            if(chatHistory) {
                                chatHistory(message);
                            }

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

        }

        //send history
        private void sendChatHistory() {
            int num = historyList.size() < historyNum ? historyList.size() : historyNum;
            for (int i = 0; i < num; i++) {
                writer.println("/history");
                writer.println(historyList.get(i));
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
                        System.out.println(langKeyConf.getProperty("client")+" "+socket.getRemoteSocketAddress()+" "+langKeyConf.getProperty("disconnect"));
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
                        System.out.println(langKeyConf.getProperty("forwarded"));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }

        //encrypt
        public static String encrypt(String plaintext, String key) throws Exception {
            String AES_ALGORITHM = "AES";
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        //Encrypt By RSA
        public static String rsaEncrypt(String originalMsg, Key key) throws Exception {
            int MAX_ENCRYPT_BLOCK = 117;
            String ALGORITHM = "RSA";
            String UTF8 = StandardCharsets.UTF_8.name();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes = doCodec(cipher, originalMsg.getBytes(UTF8), MAX_ENCRYPT_BLOCK);
            return Base64.getEncoder().encodeToString(bytes);
        }

        public static byte[] doCodec(Cipher cipher, byte[] cont, int maxBlockSize) throws Exception {
            int contLength = cont.length;
            int offset = 0;
            byte[] temp;
            int i = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((contLength - offset) > 0) {
                if ((contLength - offset) > maxBlockSize) {
                    temp = cipher.doFinal(cont, offset, maxBlockSize);
                } else {
                    temp = cipher.doFinal(cont, offset, contLength - offset);
                }
                byteArrayOutputStream.write(temp,0,temp.length);
                i++;
                offset = i * maxBlockSize;
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        }
    }
}
