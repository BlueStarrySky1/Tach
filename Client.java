
import jdk.jfr.events.FileReadEvent;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

public class client {
    private static String key = null;
    private static String initVector = null;
    public static String icon = " _________  ________  ________  ___  ___     \n|\\___   ___\\\\   __  \\|\\   ____\\|\\  \\|\\  \\    \n\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\   \n     \\ \\  \\ \\ \\   __  \\ \\  \\    \\ \\   __  \\  \n      \\ \\  \\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \n       \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\n        \\|__|  \\|__|\\|__|\\|_______|\\|__|\\|__|\n";
    public static String info = "TACH Core (Client) v1.3 Release\n";
    public static String ver = "1.3";
    public static String sendWord = null;
    public static Boolean isConnected = false;
    public static Boolean receiveLast = false;
    public static String fileDir = "";
    public static Boolean receiver = false;
    public static Boolean silent = false;
    public static Properties langKeyConf = null;
    public static void main(String[] args) throws Exception {
        KeyPair keyPair = getKeyPair();
        PublicKey publicKey = getPublicKey(keyPair);
        PrivateKey privateKey = getPrivateKey(keyPair);
        //read config
        String configPath = args.length > 1 && args[0].equals("-c") ? args[1] : "./clientConfig.properties";
        Properties config = new Properties();
        BufferedReader configBR = new BufferedReader(new FileReader(configPath));
        config.load(configBR);
        //customization
        silent = args.length > 2 ? true : Boolean.valueOf(config.getProperty("silent"));
        if (args.length > 3) {
            sendWord = args[2].equals("-o") ? args[3] : null;
        }
        receiveLast = args.length > 2 && args[2].equals("-l") ? true : false;
        receiver = args.length > 2 && args[2].equals("-r") ? true : false;
        if (args.length > 3 && receiveLast || receiver) { fileDir = config.getProperty("fileDir").isEmpty() ? fileDir : config.getProperty("fileDir"); }

        String serverAddress = config.getProperty("serverAddr");
        int serverPort = Integer.parseInt(config.getProperty("serverPort"));
        String userName = config.getProperty("username");
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

        //open socket
        Socket socket = new Socket(serverAddress, serverPort);
        if (!silent) {
            System.out.println(icon);
            System.out.println(langKeyConf.getProperty("cinfo") + " " + ver + " " + langKeyConf.getProperty("release")+" " + langKeyConf.getProperty("lang"));
        }
        // Get username

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println(userName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(publicKey);
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        //thread
        Thread thread = new Thread(new Runnable() {
            //receive
            @Override
            public void run() {
                String message;

                try {

                    BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));



                    //System.out.println(publicKey);
                    while((message = socketReader.readLine()) != null) {
                        File file = null;
                        if (message.startsWith("/ver")) {
                            if (!message.substring(4).equals(ver)){
                                if (!silent) {
                                    System.err.println(langKeyConf.getProperty("cerror1") + langKeyConf.getProperty("cerror1_2") + ver + "\n"+langKeyConf.getProperty("cerror1_3") + message.substring(22));
                                }
                                writer.println("/close");
                                socket.close();
                                System.exit(0);
                                break;
                            }else {
                                writer.println("/ready");
                            }
                        }else if (message.startsWith("/key")) {
                            key = rsaDecrypt(message.substring(4), privateKey);

                            if (isConnected == false) {
                                if (!silent) {
                                    System.out.println(langKeyConf.getProperty("cconnect") + socket.getRemoteSocketAddress());
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
                            //history
                            String decryptedMsg = null;
                            if (message.equals("/history")) {
                                String decryptHistoryMsg = decrypt(socketReader.readLine(), key);
                                System.out.println(langKeyConf.getProperty("chistory") + " | " + decryptHistoryMsg);
                                decryptedMsg = langKeyConf.getProperty("chistory") + " | " + decryptHistoryMsg;
                            }else {
                                System.out.println(decrypt(message, key));
                                decryptedMsg = decrypt(message, key);
                            }

                            if (!receiver) {
                                if (args.length > 4 && receiver || receiveLast) {
                                    file = new File(fileDir);
                                    try (FileWriter fileWriter = new FileWriter(file, true)) {
                                        fileWriter.append(decryptedMsg+"\n");
                                    }
                                    System.out.println(langKeyConf.getProperty("cwriten"));
                                }else {


                                }

                            }else {
                                file = new File(fileDir);

                                System.out.println(langKeyConf.getProperty("cwriten"));

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
                    System.out.println(langKeyConf.getProperty("csent"));
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




    //encrypt
    public static String encrypt(String plaintext, String key) throws Exception {
        String AES_ALGORITHM = "AES";
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // decrypt
    public static String decrypt(String ciphertext, String key) throws Exception {
        String AES_ALGORITHM = "AES";
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    //Get Key Pair
    public static KeyPair getKeyPair() throws Exception {
        String ALGORITHM = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    //Get Public Key
    public static PublicKey getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        return publicKey;
    }

    //Get Private Key
    public static PrivateKey getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        return privateKey;
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

    //Decrypt By RSA
    public static String rsaDecrypt(String encryptedMsg, Key key) throws Exception {
        int MAX_DECRYPT_BLOCK = 128;
        String ALGORITHM = "RSA";
        String UTF8 = StandardCharsets.UTF_8.name();
        byte[] decodeMsg = Base64.getDecoder().decode(encryptedMsg);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = doCodec(cipher, decodeMsg, MAX_DECRYPT_BLOCK);
        return new String(decryptedBytes);
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
