package ciaf.clienteservidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class Server {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clientWriters = new HashMap<>(); // Mapa para almacenar escritores de clientes (nombres de usuario y sus flujos de salida)

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);
            while (true) {
                new ClientHandler(serverSocket.accept()).start(); // Espera nuevas conexiones de clientes y lanza un nuevo hilo para manejar cada cliente
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String username; // Nombre de usuario del cliente

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine(); // Lee el nombre de usuario del cliente
                System.out.println(username + " connected.");
                broadcast(username + " joined the chat."); // Anuncia la conexión de un nuevo usuario

                clientWriters.put(username, out); // Almacena el nombre de usuario y el flujo de salida del cliente

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(username + ": " + message); // Imprime el mensaje recibido del cliente con su nombre de usuario
                    broadcast(username + ": " + message); // Retransmite el mensaje a todos los otros clientes
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    clientWriters.remove(username); // Elimina al cliente desconectado del mapa
                }
                try {
                    socket.close(); // Cierra el socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
                broadcast(username + " left the chat."); // Anuncia que un usuario se desconectó
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(message); // Envía el mensaje a todos los clientes conectados
            }
        }
    }
}
