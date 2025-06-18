package com.projektsekai;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login {

    @FXML
    TextField usernametextfield;

    @FXML
    PasswordField passwordfield;

    @FXML
    TextField regUsernameTextfield;

    @FXML
    TextField regPasswordTextfield;

    @FXML
    Button loginbutton;
    
    @FXML
    Button registerMode;

    @FXML
    Button registerButton;

    @FXML
    Label header;

    @FXML
    Label Question;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public static User user;

    @FXML
    private void loginbuttonhandler(ActionEvent event) throws IOException { // 4 methods
        
        String username = usernametextfield.getText();

        String password = passwordfield.getText();

        user = new User(username, password);

        File accountsfile = new File("accounts.txt");

        if (accountsfile.exists()) {

            boolean userFound = false;
            boolean passwordCorrect = false;

            try (Scanner filescanner = new Scanner(accountsfile)) {

                while (filescanner.hasNextLine()) {
                    String data = filescanner.nextLine();

                    String[] parts = data.split(",");
                    if (parts.length < 2) continue;

                    String username_from_file = parts[0];
                    String password_from_file = parts[1];

                    if (username_from_file.equals(user.getUsername())) {
                        userFound = true;
                        if (password_from_file.equals(user.getPassword())) {
                            passwordCorrect = true;
                        }
                        break;
                    }
                }

                if (userFound && passwordCorrect) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setContentText("Login Successful!");
                    alert.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                    root = loader.load();

                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();

                } else if (userFound) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("Error");
                    alert.setContentText("Incorrect Password.");
                    alert.showAndWait();

                } else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("Error");
                    alert.setContentText("No user found.");
                    alert.showAndWait();
                }
            }

        } else {
            System.out.println("file does not exist");
        }
    }

    private void showAlert(String title, String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

    @FXML
    private void RegisterAccount(ActionEvent event) {
        if (registerMode.getText().equals("Register")) {
            regUsernameTextfield.setVisible(true);
            regPasswordTextfield.setVisible(true);
            usernametextfield.setVisible(false);
            passwordfield.setVisible(false);;
            registerButton.setVisible(true);

            Question.setText("Have an account?");
            header.setText("Register Account");
            registerMode.setText("Login");
        } else if (registerMode.getText().equals("Login")) {
            regUsernameTextfield.setVisible(false);
            regPasswordTextfield.setVisible(false);
            usernametextfield.setVisible(true);
            passwordfield.setVisible(true);
            registerButton.setVisible(false);

            Question.setText("Don't have an account?");
            header.setText("Login");
            registerMode.setText("Register");
        }
    }

    @FXML
    private void registerButtonHandler(ActionEvent event) throws IOException {
        String username = regUsernameTextfield.getText().trim();
        String password = regPasswordTextfield.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Registration Failed", "Username or password caanot be empty.");
            return;
        }

        File accountsFile = new File("accounts.txt");

        if (accountsFile.exists()) {
            try (Scanner scanner = new Scanner(accountsFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");

                    if (parts.length >= 1 && parts[0].equals(username)) {
                        showAlert("Registration Failed", "Username already Exists.");
                        return;
                    }
                }
            }
        }

        try (java.io.FileWriter fw = new java.io.FileWriter(accountsFile, true)) {
            fw.write(username + "," + password + "\n");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to write to file.");
            return;
        }

        showAlert("Registration Successful", "Account created! You can now log in.");

    }

}
