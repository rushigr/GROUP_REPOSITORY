package com.projektsekai;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption; 
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager { // 5 methods

    public boolean isFavoriteAlready(String entry) {
        try {
            List <String> lines = Files.readAllLines(Paths.get("favorites.txt"));
            return lines.contains(entry);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean addToFavorite(String username, String pdfName) {
        String entry = username + "," + pdfName;  
            if (!isFavoriteAlready(entry)) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("favorites.txt", true))) {
                    writer.write(entry);
                    writer.newLine();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }            
            } 
            return false;
    }

    public boolean removeFromFavorites(String username, String pdfName) {
        File file = new File("favorites.txt");
        if (!file.exists()) return false;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (!line.equals(username + "," + pdfName)) {
                    updatedLines.add(line);
                }
            }

            Files.write(file.toPath(), updatedLines, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> showOnlyFavorites(String username) {
        List<String> favorites = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get("favorites.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(Login.user.getUsername())) {
                    favorites.add(parts[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return favorites;
    }

    public boolean isFavorite(String username, String pdfName) {
        return isFavoriteAlready(username + "," + pdfName);
    }
}
