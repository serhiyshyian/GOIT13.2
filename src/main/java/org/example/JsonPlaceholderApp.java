package org.example;


import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Arrays;

public class JsonPlaceholderApp {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final Gson gson = new Gson();

        public static void main(String[] args) throws IOException {
            int userId = 1;
            JsonPlaceholderApp app = new JsonPlaceholderApp();
            app.getAndSaveCommentsAndTodosForLastPostOfUser(userId);

        try {
            // Створення нового користувача

            User newUser = new User(
                    "Hermann Hesse",
                    "hermannh",
                    "hermann.hesse@example.com",
                    new Address(
                            "123 Main St",
                            "Apt. 456",
                            "Cityville",
                            "12345",
                            new Geo("-12.3456", "78.9012")
                    ),
                    "555-1234",
                    "hermannh.com",
                    new Company(
                            "Bookworm Publishers",
                            "Exploring the world through literature",
                            "Publishing the best stories"
                    )
            );
            User createdUser = app.createUser(newUser);
            System.out.println("Created User: " + createdUser);




            // Отримання інформації про всіх користувачів
            User[] allUsers = app.getAllUsers();
            for (User user : allUsers) {
                System.out.println("User: " + user);
            }

            // Оотримання інформації по id

            int userIdToRetrieve = 1; //  id користувача
            User userById = app.getUserById(userIdToRetrieve);
            System.out.println("User by Id: " + userById);


            // Отримання інформації по username
            String usernameToRetrieve = "Bret"; //  username
            User userByUsername = app.getUserByUsername(usernameToRetrieve);
            System.out.println("User by Username: " + userByUsername);

            // Оновлення інформації про користувача
            int userIdToUpdate = 1; // Приклад id користувача
            User updatedUser = new User(
                    userIdToUpdate,
                    "Updated Name",
                    "updated_username",
                    "updated.email@example.com",
                    new Address(
                            "456 Elm St",
                            "Suite 789",
                            "Townsville",
                            "54321",
                            new Geo("12.3456", "-78.9012")
                    ),
                    "555-5678",
                    "updated-example.com",
                    new Company( "Innovative Tech",
                            "Driving innovation through technology",
                            "Creating the future"

                    )
            );
            User userAfterUpdate = app.updateUser(userIdToUpdate, updatedUser);
            System.out.println("User after Update: " + userAfterUpdate);

            // Видалення користувача
            int userIdToDelete = 1; // Приклад id користувача для видалення
            boolean deletionResult = app.deleteUser(userIdToDelete);
            System.out.println("User Deletion Result: " + deletionResult);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void getAndSaveCommentsAndTodosForLastPostOfUser(int userId) throws IOException {
        // Отримання всих постів користувача
        Post[] userPosts = getUserPosts(userId);

        if (userPosts.length > 0) {
            //Вибір останнього поста
            Post lastPost = userPosts[userPosts.length - 1];

            // Отримання коментарів для останнього коментаря
            Comment[] comments = getPostComments(lastPost.getId());

            // Отримання невиконаних задач користувача
            Todo[] openTodos = getOpenTodosForUser(userId);

            //  створення імені файла для коментарів
            String commentsFileName = "src/main/resources/data/User-" + userId + "-post-" + lastPost.getId() + "-comments.json";

            //  створення імені файла для невиконаних задач
            String todosFileName = "src/main/resources/data/User-" + userId + "-post-" + lastPost.getId() + "-todos.json";

            // Запис комментарів у файл
            System.out.println("Saving comments to file: " + commentsFileName);
            saveCommentsToFile(commentsFileName, comments);
            System.out.println("Comments for the last post of user " + userId + " saved to " + commentsFileName);

            // Запис невиконаних задач у файл
            System.out.println("Saving open todos to file: " + todosFileName);
            saveTodosToFile(todosFileName, openTodos);
            System.out.println("Open todos for user " + userId + " saved to " + todosFileName);

            // вивід відкритих задач
            System.out.println("Open todos for user " + userId + ":");
            for (Todo todo : openTodos) {
                System.out.println("Todo ID: " + todo.getId());
                System.out.println("Title: " + todo.getTitle());
                System.out.println("Completed: " + todo.isCompleted());
                System.out.println();
            }
        } else {
            System.out.println("User " + userId + " has no posts.");
        }
    }

    public void saveTodosToFile(String fileName, Todo[] todos) throws IOException {
        String todosJson = gson.toJson(todos);
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(todosJson);
        }
    }


    public Todo[] getOpenTodosForUser(int userId) throws IOException {
        String todosUrl = BASE_URL + "/users/" + userId + "/todos";
        String response = sendGetRequest(todosUrl);
        Todo[] allTodos = gson.fromJson(response, Todo[].class);
        return filterOpenTodos(allTodos);
    }

    private Todo[] filterOpenTodos(Todo[] todos) {
        return Arrays.stream(todos)
                .filter(todo -> !todo.isCompleted())
                .toArray(Todo[]::new);
    }
    public User[] getAllUsers() throws IOException {
        String usersUrl = BASE_URL + "/users";
        String response = sendGetRequest(usersUrl);
        return gson.fromJson(response, User[].class);
    }
    public Post[] getUserPosts(int userId) throws IOException {
        String postsUrl = BASE_URL + "/users/" + userId + "/posts";
        String response = sendGetRequest(postsUrl);
        return gson.fromJson(response, Post[].class);
    }

    public Comment[] getPostComments(int postId) throws IOException {
        String commentsUrl = BASE_URL + "/posts/" + postId + "/comments";
        String response = sendGetRequest(commentsUrl);
        return gson.fromJson(response, Comment[].class);
    }

    public void saveCommentsToFile(String fileName, Comment[] comments) throws IOException {
        String commentsJson = gson.toJson(comments);
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(commentsJson);
        }
    }

    public User getUserById(int userId) throws IOException {
        String userUrl = BASE_URL + "/users/" + userId;
        String response = sendGetRequest(userUrl);
        return gson.fromJson(response, User.class);
    }

    public User getUserByUsername(String username) throws IOException {
        String usersUrl = BASE_URL + "/users?username=" + username;
        String response = sendGetRequest(usersUrl);
        User[] users = gson.fromJson(response, User[].class);
        if (users.length > 0) {
            return users[0];
        }
        return null;
    }

    public User createUser(User user) throws IOException {
        String usersUrl = BASE_URL + "/users";
        String userJson = gson.toJson(user);
        String response = sendPostRequest(usersUrl, userJson);
        return gson.fromJson(response, User.class);
    }

    public User updateUser(int userId, User updatedUser) throws IOException {
        String userUrl = BASE_URL + "/users/" + userId;
        String userJson = gson.toJson(updatedUser);
        String response = sendPutRequest(userUrl, userJson);
        return gson.fromJson(response, User.class);
    }

    public boolean deleteUser(int userId) throws IOException {
        String userUrl = BASE_URL + "/users/" + userId;
        int responseCode = sendDeleteRequest(userUrl);
        return responseCode >= 200 && responseCode < 300;
    }

    private String sendGetRequest(String url) throws IOException {
        HttpURLConnection connection = HttpHelper.openConnection(url);
        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }
        return response.toString();
    }

    private String sendPostRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = HttpHelper.openConnection(url);
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

    private String sendPutRequest(String url, String requestBody) throws IOException {
        HttpURLConnection connection = HttpHelper.openConnection(url);
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

    private int sendDeleteRequest(String url) throws IOException {
        HttpURLConnection connection = HttpHelper.openConnection(url);
        connection.setRequestMethod("DELETE");
        return connection.getResponseCode();
    }


}