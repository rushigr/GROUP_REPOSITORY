package com.projektsekai;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BookmarkManager { // 5 methods
    
    public static void saveBookmark(String username, String pdfName, int page) {
        try {
            List <String> lines = Files.readAllLines(Paths.get("bookmarks.txt"));
            String newEntry = username + "," + pdfName + "," + page;

            for (String line : lines) {
                if (line.equals(newEntry)) {
                    return;
                }
            }

            try (FileWriter fw = new FileWriter("bookmarks.txt", true);
                BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write(newEntry);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
        }
    }

    public static void deleteBookmark(String username, String pdfName, int page) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("bookmarks.txt"));
            List<String> updatedLines = new ArrayList<>();

            String target = username + "," + pdfName + "," + page; 

            for (String line: lines) {
                if (!line.equals(target)) {
                    updatedLines.add(line);
                }
            }

            Files.write(Paths.get("bookmarks.txt"), updatedLines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getBookmarks(String username, String pdfName) {
        List<Integer> bookmarks = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("bookmarks.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(pdfName)) {
                    bookmarks.add(Integer.parseInt(parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookmarks;
    }

    public static void saveLastPage(String username, String pdfName, int lastPage) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("lastpages.txt"));
        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts[0].equals(username) && parts[1].equals(pdfName)) {
                lines.set(i, username + "," + pdfName + "," + lastPage);
                updated = true;
                break;
            }
        }

        if (!updated) {
            lines.add(username + "," + pdfName + "," + lastPage);
        }

        Files.write(Paths.get("lastpages.txt"), lines);

    }

    public static int getLastPage(String username, String pdfName) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("lastpages.txt"));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(pdfName)) {
                    return Integer.parseInt(parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}   
