
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.crypto.Cipher;
import java.util.Base64;
public class client {
    private static String key = null;
    private static String initVector = null;
    public static String icon = " _________  ________  ________  ___  ___     \n|\\___   ___\\\\   __  \\|\\   ____\\|\\  \\|\\  \\    \n\\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\\\\\  \\   \n     \\ \\  \\ \\ \\   __  \\ \\  \\    \\ \\   __  \\  \n      \\ \\  \\ \\ \\  \\ \\  \\ \\  \\____\\ \\  \\ \\  \\ \n       \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\ \\__\\ \\__\n        \\|__|  \\|__|\\|__|\\|_______|\\|__|\\|__|\n";
    public static String info = "TACH Core (Client) v1.1 Pre-Release\n";
    public static String ver = "1.1";

    public static void main(String[] args) throws Exception {

        // Customize port, addr and username
        String serverAddress = args.length > 0 ? args[0] : "localhost";
        int serverPort = args.length > 1 ? Integer.parseInt(args[1]) : 10818;
        String userName = args.length > 2 ? args[2] : "Anonymous";

        Socket socket = new Socket(serverAddress, serverPort);
        System.out.println(icon+info);
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

                        if (message.startsWith("/key ")) {
                            key = message.substring(5,21);
                            if (!message.substring(22).equals(ver)){
                                System.err.println("Error: Not the same version as server");
                                socket.close();
                                return;
                            }
                            System.out.println("Connect to server "+socket.getRemoteSocketAddress());
                        }else {
                            // decrypt
                            System.out.println(decrypt(message, key));
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        while(true) {
            String sendMessage = reader.readLine();

            //encrypt
            String encryptedMessage = encrypt(userName+": "+sendMessage,key);

            writer.println(encryptedMessage);
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
