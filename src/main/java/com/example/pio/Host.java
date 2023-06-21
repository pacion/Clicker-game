package com.example.pio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {

    private static final Leaderboard leaderboard = new Leaderboard();

    private static final int PORT = 8080;
    private static int counterOfPlayers = 0;

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);

                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }

                    String[] parts = message.split(":");

                    if (parts.length == 5) {
                        String coinUser = parts[0].trim();
                        String nickUser = parts[1].trim();
                        String ipUser = parts[2].trim();
                        String counter = parts[3].trim();
                        String seconds = parts[4].trim();

                        if(Integer.valueOf(counter) == 1) {
                            counterOfPlayers++;
                            //System.out.println("JEST TYLE: " + counterOfPlayers + "\n\n");
                        }

                        try {
                            Double value = Double.parseDouble(coinUser);

                            synchronized (leaderboard) {
                                leaderboard.addOrUpdatePlayerScore(ipUser, value, nickUser, String.valueOf(counterOfPlayers), seconds);
                            }

                            if(Integer.valueOf(seconds) >= 10) {
                                out.println(leaderboard.toString());
                            }else {
                                out.println(leaderboard.toString());
                            }

                            //System.out.println("\nHOST: " + leaderboard.toString());

                        } catch (NumberFormatException e) {
                            out.println("Invalid integer format. Expected an integer value.");
                        }
                    } else {
                        out.println("Invalid message format. Expected key:value.");
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

