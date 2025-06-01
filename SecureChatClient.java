import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class SecureChatClient {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public SecureChatClient() {
        initializeUI();
        connectToServer();
    }

    private void initializeUI() {
        frame = new JFrame("Secure Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        frame.setVisible(true);
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            username = JOptionPane.showInputDialog(frame, "Enter your username:");
            out.println(username);

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        chatArea.append(message + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Connection lost: " + e.getMessage() + "\n");
                }
            }).start();
        } catch (IOException e) {
            chatArea.append("Failed to connect: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            messageField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SecureChatClient::new);
    }
}