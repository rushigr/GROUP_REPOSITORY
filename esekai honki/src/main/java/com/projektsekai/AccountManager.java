package com.projektsekai;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AccountManager { // 4 methods

    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String[] USER_FILES = {
        "accounts.txt", "bookmarks.txt", "favorites.txt", "lastpages.txt"
    };

    public boolean usernameExists(String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(ACCOUNTS_FILE));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch  (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAccount(String currentUsername, String newUsername, String newPassword) {
        boolean success = true;

        try {
            File file = new File(ACCOUNTS_FILE);
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(currentUsername)) {
                    String updatedUsername = (newUsername != null) ? newUsername : parts[0];
                    String updatedPassword = (newPassword != null) ? newPassword : parts[0];
                    updatedLines.add(updatedUsername + "," + updatedPassword);
                } else {
                    updatedLines.add(line);
                }
            }

            Files.write(file.toPath(), updatedLines, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        
        if (newUsername != null && !newUsername.equals(currentUsername)) {
            for (String fileName : USER_FILES) {
                if (!fileName.equals(ACCOUNTS_FILE)) {
                    try {
                        Path path = Paths.get(fileName);
                        if (!Files.exists(path)) continue;

                        List<String> lines = Files.readAllLines(path);
                        List<String> updatedLines = new ArrayList<>();

                        for (String line : lines) {
                            if (line.startsWith(currentUsername + ",")) {
                                updatedLines.add(line.replaceFirst(currentUsername, newUsername));
                            } else {
                                updatedLines.add(line);
                            }
                        }

                        Files.write(path, updatedLines, StandardOpenOption.TRUNCATE_EXISTING);

                    } catch (IOException e) {
                        e.printStackTrace();
                        success = false;
                    }
                }
            }
        }
        return success;
    }

    public void deleteUserAccount(String username) {
        for (String file : USER_FILES) {
            deleteUserDataFromFile(file, line -> !line.startsWith(username + ","));
        }
    }

    private void deleteUserDataFromFile(String fileName, Predicate<String> keepCondition) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) return;

            List<String> filteredLines = Files.lines(path).filter(keepCondition).collect(Collectors.toList());

            Files.write(path, filteredLines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}