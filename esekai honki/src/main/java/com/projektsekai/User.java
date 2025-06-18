package com.projektsekai;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;    

    public User(String username, String password) {
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
}