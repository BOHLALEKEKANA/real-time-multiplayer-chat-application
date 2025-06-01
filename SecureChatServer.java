import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class SecureChatServer {
    private static final int PORT = 12345;
    private static final String SECRET_KEY = "MySecretKey12345"; // 16 bytes for AES
    private static final Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Enter username:");
                username = in.readLine();
                broadcastMessage(username + " joined the chat!", this);

                String message;
                while ((message = in.readLine()) != null) {
                    if (!message.isEmpty()) {
                        String encryptedMessage = encryptMessage(message);
                        broadcastMessage(username + ": " + encryptedMessage, this);
                    }
                }
            } catch (Exception e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try {
                    clients.remove(this);
                    broadcastMessage(username + " left the chat!", this);
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client: " + e.getMessage());
                }
            }
        }

        private String encryptMessage(String message) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        }

        void sendMessage(String message) {
            try {
                String decryptedMessage = decryptMessage(message.split(": ", 2)[1]);
                out.println(message.split(": ", 2)[0] + ": " + decryptedMessage);
            } catch (Exception e) {
                out.println("Error decrypting message: " + e.getMessage());
            }
        }

        private String decryptMessage(String encryptedMessage) throws Exception {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        }
    }
}