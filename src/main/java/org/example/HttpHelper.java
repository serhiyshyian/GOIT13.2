package org.example;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class HttpHelper {

    public static HttpURLConnection openConnection(String url) throws IOException {
        java.net.URL urlObj = new java.net.URL(url);
        return (HttpURLConnection) urlObj.openConnection();
    }

    public static String sendGetRequest(String url) throws IOException {
        HttpURLConnection connection = openConnection(url);
        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }
        return response.toString();
    }

    public static String sendPostRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = openConnection(url);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
        }

        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }

        return response.toString();
    }

    public static String sendPutRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = openConnection(url);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
        }

        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }

        return response.toString();
    }

    public static int sendDeleteRequest(String url) throws IOException {
        HttpURLConnection connection = openConnection(url);
        connection.setRequestMethod("DELETE");
        return connection.getResponseCode();
    }
}
