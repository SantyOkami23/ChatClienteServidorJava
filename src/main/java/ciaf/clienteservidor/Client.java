package ciaf.clienteservidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);  // Se crea un nuevo socket y se establece la conexión con el servidor
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Se crea un lector de entrada para recibir mensajes del servidor
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Se crea un escritor de salida para enviar mensajes al servidor
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) { // Se crea un lector de entrada para recibir mensajes desde la consola

            System.out.println("Connected to server. Enter your username:");
            String username = userInput.readLine(); // Solicita al usuario ingresar su nombre de usuario
            out.println(username); // Envía el nombre de usuario al servidor

            System.out.println("Welcome, " + username + "! You can start typing messages:");

            Thread inputThread = new Thread(() -> {
                String userInputMessage;
                try {
                    while ((userInputMessage = userInput.readLine()) != null) {
                        out.println(userInputMessage); // Envía los mensajes del usuario al servidor
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage); // Imprime mensajes recibidos del servidor
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
