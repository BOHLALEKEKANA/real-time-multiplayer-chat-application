# real-time multiplayer chat application

A real-time multiplayer chat application with end-to-end encryption using Java. This project will feature a server-client architecture, real-time message broadcasting, and AES encryption for secure communication. The client will have a Swing-based GUI, and the server will handle multiple client connections concurrently.

To run this project, you'll need to:

Start the Server: Compile and run SecureChatServer.java first. It listens on port 12345 and handles multiple client connections.
Start Clients: Compile and run SecureChatClient.java on multiple instances (on the same or different machines). Each client prompts for a username and connects to the server.
Dependencies: This project uses Java's built-in libraries (no external dependencies like Maven are required).

Features:

Real-Time Communication: Messages are broadcast to all connected clients instantly using a multithreaded server.
End-to-End Encryption: Messages are encrypted with AES (ECB mode with PKCS5 padding) before transmission and decrypted on receipt.
Swing GUI: The client features a simple GUI with a chat area and input field.
Concurrent Clients: The server handles multiple clients using a thread-per-client model.
Error Handling: Basic error handling for network issues and encryption/decryption errors.

Challenges:

Implementing secure AES encryption/decryption.
Managing concurrent client connections with thread safety.
Handling real-time message broadcasting.
Ensuring the GUI remains responsive with network operations.

To Extend:

Use a real key exchange mechanism (e.g., Diffie-Hellman) instead of a hardcoded key.
Add private messaging functionality.
Implement a database (e.g., SQLite) for message history.
Enhance the GUI with features like message timestamps or user status indicators.

Note: The AES key is hardcoded for simplicity (MySecretKey12345). In a production environment, use a secure key exchange protocol. Also, ECB mode is used for simplicity but is not the most secure; consider CBC mode with an IV for production.