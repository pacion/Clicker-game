package com.example.pio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Host {
    private static Map<String, Integer> dataMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080); // Server socket listening on port 8080
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept client connection
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Create a new thread for each client connection
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                // Create input and output streams for communication
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                while (true) {
                    String message = in.readLine(); // Read client's message

                    if (message == null) {
                        break; // Exit the loop if the client disconnects
                    }

                    String[] parts = message.split(":");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        int value = Integer.parseInt(parts[1].trim());

                        synchronized (dataMap) {
                            dataMap.put(key, value); // Store the key-value pair in the map
                        }

                        System.out.println("Received from client " + clientSocket.getInetAddress() +
                                ": " + key + " = " + value);

                        out.println("Data received successfully.");
                    } else {
                        out.println("Invalid message format. Expected key:value.");
                    }
                }

                // Close connections
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
