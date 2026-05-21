package com.example.fitnessapp;

import java.io.*;
import java.net.Socket;

public class NetworkClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public NetworkClient() {
        try {
            this.socket = new Socket(SERVER_HOST, SERVER_PORT);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Połączono z serwerem pomyślnie.");
        } catch (IOException e) {
            System.err.println("Nie można połączyć się z serwerem: " + e.getMessage());
        }
    }

    public String sendRequest(String request) {
        if (out == null || in == null) {
            return "NET_ERROR;Brak połączenia z serwerem. Czy serwer jest włączony?";
        }
        try {
            out.println(request); // Wysyłamy żądanie do serwera
            return in.readLine();  // Czekamy na odpowiedź sieciową (zablokowanie do czasu odpowiedzi)
        } catch (IOException e) {
            return "NET_ERROR;Błąd komunikacji: " + e.getMessage();
        }
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Zakończono połączenie z serwerem.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}