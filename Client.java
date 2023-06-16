
import jdk.jfr.events.FileReadEvent;

import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import javax.crypto.Cipher;
import java.util.Base64;
public class client {
    private static String key = null;
    private static String initVector = null;
    public static String icon = " _________  ________  ________  ___  ___     \n|\\___   ___\\\\   __  \\|\\   ____\\|\\  \\|\\  \\    \n\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\   \n     \\ \\  \\ \\ \\   __  \\ \\  \\    \\ \\   __  \\  \n      \\ \\  \\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \n       \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\n        \\|__|  \\|__|\\|__|\\|_______|\\|__|\\|__|\n";
    public static String info = "TACH Core (Client) v1.2 Release\n";
    public static String ver = "1.2";
    public static String sendWord = null;
    public static Boolean isConnected = false;
    public static Boolean receiveLast = false;
    public static String fileDir = ".\\receivedData.log";
    public static Boolean receiver = false;
    public static Boolean silent = false;
    public static void main(String[] args) throws Exception {
        silent = args.length > 3 ? true : false;
        if (args.length > 4) {
            sendWord = args[3].equals("-o") ? args[4] : null;
        }
        receiveLast = args.length > 3 && args[3].equals("-l") ? true : false;
        receiver = args.length > 3 && args[3].equals("-r") ? true : false;
        fileDir = args.length > 4 && receiver || receiveLast ? args[4] : fileDir;
        // Customize port, addr and username
        String serverAddress = args.length > 0 ? args[0] : "localhost";
        int serverPort = args.length > 1 ? Integer.parseInt(args[1]) : 10818;
        String userName = args.length > 2 ? args[2] : "Anonymous";

        Socket socket = new Socket(serverAddress, serverPort);
        if (!silent) {
            System.out.println(icon + info);
        }
        // Get username

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(userName);

        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                try {

                    BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));



                    while((message = socketReader.readLine()) != null) {
                        File file = null;
                        if (message.startsWith("/key ")) {
                            key = message.substring(5,21);
                            if (!message.substring(22).equals(ver)){
                                if (!silent) {
                                    System.err.println("Error: Not the same version!\nClient Version: " + ver + "\nServer Version: " + message.substring(22));
                                }
                                writer.println("/close");
                                socket.close();
                                System.exit(0);
                                break;
                            }
                            if (isConnected == false) {
                                if (!silent) {
                                    System.out.println("Connect to server " + socket.getRemoteSocketAddress());
                                }
                                if (receiver || args.length > 4 && receiveLast) {
                                    file = new File(fileDir);
                                    file.delete();
                                    if (!file.exists()) {
                                        file.createNewFile();
                                    }
                                }
                                isConnected = true;
                            }
                        }else if (!message.equals("/lastMsg") && !message.equals("/close")){
                            String decryptedMsg = decrypt(message, key);
                            if (!receiver) {
                                if (args.length > 4 && receiver || receiveLast) {
                                    file = new File(fileDir);
                                    try (FileWriter fileWriter = new FileWriter(file, true)) {
                                        fileWriter.append(decryptedMsg+"\n");
                                    }
                                    System.out.println("Writen");
                                }else {
                                    // decrypt
                                    System.out.println(decryptedMsg);
                                }

                            }else {
                                file = new File(fileDir);

                                System.out.println("Writen");

                                try (FileWriter fileWriter = new FileWriter(file, true)) {
                                    fileWriter.append(decryptedMsg+"\n");
                                }

                            }

                            if (receiveLast == true){
                                writer.println("/close");
                                socket.close();
                                System.exit(0);
                                break;
                            }

                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        //send
        while(true) {
            String sendMessage = null;
            if (isConnected) {
                if (sendWord != null) {
                    //encrypt
                    String encryptedMessage = encrypt(userName + ": " + sendWord, key);
                    writer.println(encryptedMessage);
                    System.out.println("Sent");
                    writer.println("/close");
                    System.exit(0);
                } else {
                    if (receiveLast != true && receiver != true) {
                        sendMessage = reader.readLine();
                    }else {
                        writer.println("/lastMsg");
                        break;
                    }
                    if (sendMessage.equals("/close"))
                    {
                        writer.println("/close");
                        socket.close();
                        System.exit(0);
                        break;
                    }else if (sendMessage.equals("/lastMsg")) {
                        writer.println("/lastMsg");
                    }else {
                        //encrypt
                        String encryptedMessage = encrypt(userName + ": " + sendMessage, key);
                        writer.println(encryptedMessage);
                    }

                }
            }

        }

    }





    public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.err.println("Key is null");
            return null;
        }

        if (sKey.length() != 16) {
            System.err.println("Key length must be 16 bytes");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // decrypt
    public static String decrypt(String sSrc, String sKey) throws Exception {
        try {

            if (sKey == null) {
                System.err.println("Key is null");
                return null;
            }

            if (sKey.length() != 16) {
                System.err.println("Key length must be 16 bytes");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc);//first base64
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }
}
