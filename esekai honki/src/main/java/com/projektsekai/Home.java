package com.projektsekai;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Home { //20 methods

    @FXML
    private Label usernamelabel;

    @FXML 
    private ScrollPane scrollpane;

    @FXML 
    private VBox scrollContent;

    @FXML 
    private ImageView coverImageView;

    @FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private ImageView pdfPageView;

    @FXML
    private TextField pageField;

    @FXML
    private Label totalPagesLabel;

    @FXML
    private Label of; //sheesh

    @FXML
    private Label sf;

    @FXML 
    private Button bookmarkButton;
    
    @FXML 
    private Button viewBookmarksButton;

    @FXML
    private CheckBox favoritesOnlyCheckBox;

    @FXML
    private Button favoriteButton;

    @FXML
    private Button removeFavoriteButton;

    @FXML
    private Button manageAccountButton; 

    private PDDocument document;
    private PDFRenderer renderer;
    private File currentPdf;
    private int currentPage;
    private int currentPageIndex = 0;
    private int totalPages = 0;

    private final FavoriteManager favoriteManager = new FavoriteManager();
    private final AccountManager accountManager = new AccountManager();

    private List<File> pdfFiles = List.of(
        new File("Mushoku Tensei Jobless Reincarnation Vol. 1.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 2.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 3.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 4.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 5.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 6.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 7.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 8.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 9.pdf"),
        new File("Mushoku Tensei Jobless Reincarnation Vol. 10.pdf"),
        new File("Mushoku Tensei Redundant Reincarnation Vol. 1.pdf"),
        new File("Mushoku Tensei Redundant Reincarnation Vol. 2.pdf")
    );

    @FXML
    public void initialize() {
        
        String username = Login.user.getUsername();
        usernamelabel.setText(username);
        showCovers(null);
    
    }

    private void showCovers(List<String> filterTitles) { // ts pmo
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(30);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color:  linear-gradient(to bottom,  #47366D, #384589);");

        int col = 0, row = 0;

        for (int i = 0; i < pdfFiles.size(); i++) {
            File pdf = pdfFiles.get(i);
            String title = pdf.getName().replace(".pdf", "");

            if (filterTitles !=null && !filterTitles.contains(pdf.getName())) {
                continue;
            }

            try { // loop to check and render the 1st page of each page pdf and convert to fxImage
                PDDocument doc = PDDocument.load(pdf);
                PDFRenderer renderer = new PDFRenderer(doc);
                BufferedImage cover = renderer.renderImageWithDPI(0, 100);
                Image fxImage = SwingFXUtils.toFXImage(cover, null);
                doc.close();

                ImageView coverView = new ImageView(fxImage);
                coverView.setFitWidth(200);
                coverView.setPreserveRatio(true);

                Label titleLabel = new Label(title);
                titleLabel.setFont(new Font("Arial", 12));
                titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                titleLabel.setTextFill(Color.WHITE);
                titleLabel.setWrapText(true);
                titleLabel.setMaxWidth(200);
                titleLabel.setAlignment(Pos.CENTER);
                titleLabel.setTextAlignment(TextAlignment.CENTER);

                VBox itemBox = new VBox(10, coverView, titleLabel);
                itemBox.setAlignment(Pos.CENTER);
                itemBox.setCursor(Cursor.HAND);

                final int index = i;
                itemBox.setOnMouseClicked(e -> showPDF(index));

                grid.add(itemBox, col++, row);
                if (col == 3) { //limit to 3 rows and columns
                    col = 0;
                    row++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        scrollContent.getChildren().clear();
        scrollContent.getChildren().add(grid);
        scrollContent.setPrefHeight(Region.USE_COMPUTED_SIZE);
        scrollContent.setPrefWidth(Region.USE_COMPUTED_SIZE);

    }

    private void showPDF(int index) { // set control buttons visibility to true ! ! !
        try {
            document = PDDocument.load(pdfFiles.get(index));
            renderer = new PDFRenderer(document);
            totalPages = document.getNumberOfPages();
            //currentPageIndex = 0;
            
            currentPdf = pdfFiles.get(index);         
            currentPage = BookmarkManager.getLastPage(Login.user.getUsername(), currentPdf.getName()); 
            currentPageIndex = currentPage;           

            renderPage(currentPageIndex);
            updateFavoriteButtons();

            pageField.setText(String.valueOf(currentPageIndex + 1));
            totalPagesLabel.setText(String.valueOf(totalPages));

            backButton.setVisible(true);
            nextButton.setVisible(true);
            prevButton.setVisible(true);
            pageField.setVisible(true);
            of.setVisible(true);
            totalPagesLabel.setVisible(true);
            bookmarkButton.setVisible(true);
            viewBookmarksButton.setVisible(true);
            favoriteButton.setVisible(true);
            removeFavoriteButton.setVisible(true);
            manageAccountButton.setVisible(false);
            favoritesOnlyCheckBox.setVisible(false);
            sf.setVisible(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    } 

    private void renderPage(int pageIndex) { // loop to render each pdf page one by one and convert to fxImage
        try {
            if (pageIndex < 0 || pageIndex >= totalPages) return;

            BufferedImage pageImage = renderer.renderImageWithDPI(pageIndex, 150);
            Image fxImage = SwingFXUtils.toFXImage(pageImage, null);
            ImageView pageView = new ImageView(fxImage);
            pageView.setFitWidth(620);
            pageView.setPreserveRatio(true);

            scrollContent.getChildren().clear();
            scrollContent.getChildren().add(pageView);

            pageField.setText(String.valueOf(pageIndex + 1));
            totalPagesLabel.setText(String.valueOf(totalPages));

            currentPage = pageIndex;

            nextButton.setDisable(pageIndex >= document.getNumberOfPages() - 1);
            prevButton.setDisable(pageIndex <= 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPageEntered() { // default to current page when page entered is out of bounds or invalid
        try {
            int requestedPage = Integer.parseInt(pageField.getText()) - 1;
            if (requestedPage >= 0 && requestedPage < totalPages) {
                currentPageIndex = requestedPage;
                renderPage(currentPageIndex);
            } else {
                pageField.setText(String.valueOf(currentPageIndex + 1));
            }
        } catch (NumberFormatException e) {
            pageField.setText(String.valueOf(currentPageIndex + 1));
        }
    }

    @FXML
    private void NextPage() {
        if (currentPageIndex < document.getNumberOfPages() - 1) {
            currentPageIndex++;
            renderPage(currentPageIndex);
        }
    }

    @FXML
    private void PrevPage() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
            renderPage(currentPageIndex);
        }
    }
    
    @FXML
    private void onBackClicked() { // also sets all control buttons visibility to false ! ! !
    try {
        if (document != null) document.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

    try {
        if (Login.user != null && currentPdf != null) {
            BookmarkManager.saveLastPage(Login.user.getUsername(), currentPdf.getName(), currentPage);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    if (favoritesOnlyCheckBox.isSelected()) {
            showCovers(favoriteManager.showOnlyFavorites(Login.user.getUsername()));
        } else {
            showCovers(null);
        }

    pageField.setVisible(false);
    of.setVisible(false);
    totalPagesLabel.setVisible(false);
    backButton.setVisible(false);
    nextButton.setVisible(false);
    prevButton.setVisible(false);
    bookmarkButton.setVisible(false);
    viewBookmarksButton.setVisible(false);
    favoriteButton.setVisible(false);
    removeFavoriteButton.setVisible(false);
    manageAccountButton.setVisible(true);
    favoritesOnlyCheckBox.setVisible(true);
    sf.setVisible(true);
    }

    @FXML
    private void onBookmarkClicked() { // also calls bookmark ! ! !
        if (Login.user != null && currentPdf != null) {
            int pageToBookmark = currentPage + 1;
            String username = Login.user.getUsername();
            String pdfName = currentPdf.getName();

            List<Integer> bookmarks = BookmarkManager.getBookmarks(username, pdfName);

            if (bookmarks.contains(pageToBookmark)) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Bookmark");
                alert.setHeaderText(null);
                alert.setContentText("Page " + pageToBookmark + " is already bookmarked.");
                alert.showAndWait();
            } else {
                BookmarkManager.saveBookmark(username, pdfName, pageToBookmark);
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Bookmark");
                alert.setHeaderText(null);
                alert.setContentText("Page " + pageToBookmark + " has been bookmarked.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onViewBookmarks() { // BOOKMARK MANAGER CALLER
        if (Login.user != null && currentPdf != null) {
            String username = Login.user.getUsername();
            String pdfName = currentPdf.getName();

            List<Integer> bookmarks = BookmarkManager.getBookmarks(username, pdfName);

            if (!bookmarks.isEmpty()) {

                ChoiceDialog<Integer> dialog = new ChoiceDialog<>(bookmarks.get(0), bookmarks);
                dialog.setTitle("View or Delete a Bookmark");
                dialog.setHeaderText("Select a bookmark");
                dialog.setContentText("Page:");

                dialog.showAndWait().ifPresent(selectedPage -> {

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Bookmark Selected");
                    alert.setHeaderText(null);
                    alert.setHeaderText("What do you want to do with bookmark page  " + selectedPage + "?");
                    alert.setContentText("Choose an option:");

                    ButtonType goToPageButton = new ButtonType("Go to Page");
                    ButtonType deleteButton = new ButtonType("Delete Bookmark");
                    ButtonType cancelButton = new ButtonType("Cancel");

                    alert.getButtonTypes().setAll(goToPageButton, deleteButton, cancelButton);

                    alert.showAndWait().ifPresent(response -> {
                        if (response == goToPageButton) {
                            currentPageIndex = selectedPage - 1;
                            renderPage(currentPageIndex);
                        } else if (response == deleteButton) {
                            BookmarkManager.deleteBookmark(username, pdfName, selectedPage);

                            Alert deleted = new Alert(AlertType.INFORMATION);
                            deleted.setTitle("Bookmark Deleted");
                            deleted.setHeaderText(null);
                            deleted.setContentText("Bookmark for page " + selectedPage + " has been deleted.");
                            deleted.showAndWait();
                        }
                    });
                });

            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Bookmarks");
                alert.setHeaderText(null);
                alert.setContentText("You have no bookmarks for this PDF.");
                alert.showAndWait();
            }
        }
    }

    private void showAlert(String title, String message) { // shortcut method for showing alerts
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

    @FXML
    private void onSetAsFavorite() {
        if (Login.user != null && currentPdf != null) { // FAVORITE MANAGER CALLER
            String username = Login.user.getUsername();
            String pdfName = currentPdf.getName();

            if (favoriteManager.addToFavorite(username, pdfName)) {
                showAlert("Success", "Added to favorites");
            } else {
                showAlert("Info", "This Novel is already marked as favorite");
            }
           updateFavoriteButtons();
        }
    }

    @FXML
    private void onRemoveFromFavorite() {
        if (Login.user != null && currentPdf != null) { //yes
            String username = Login.user.getUsername();
            String pdfName = currentPdf.getName();
            
            if (favoriteManager.removeFromFavorites(username, pdfName)) {
                showAlert("Removed", "Novel removed from favorites.");
            } else {
            showAlert("Error", "Failed to remove favorite.");
            }
            updateFavoriteButtons();
        } 
    }

    private void updateFavoriteButtons() { // set favorite button inactive on pdfs set on favorite and vice versa
        if (Login.user != null && currentPdf != null) {
            String username = Login.user.getUsername();
            String pdfName = currentPdf.getName();
            boolean isFavorite = favoriteManager.isFavorite(username, pdfName);

            favoriteButton.setDisable(isFavorite);
            removeFavoriteButton.setDisable(!isFavorite);
        } else {
            favoriteButton.setDisable(true);
            removeFavoriteButton.setDisable(true);
        }
    }

    @FXML
    private void onFavoritesToggle() {
        if (favoritesOnlyCheckBox.isSelected()) {
            showCovers(favoriteManager.showOnlyFavorites(Login.user.getUsername()));
        } else {
            showCovers(null);
        }
    }

    @FXML
    private void ManageAccount() { // ACCOUNT MANAGER CALLER
        if (Login.user == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Manage Account");
        alert.setHeaderText("What would you like to do?");
        alert.setContentText("Choose an option:");

        ButtonType changeUsernameButton = new ButtonType("Change Username");
        ButtonType changePasswordButton = new ButtonType("Change Password");        
        ButtonType logoutButton = new ButtonType("Log Out");
        ButtonType deleteAccountButton = new ButtonType("Delete Account");
        ButtonType cancelButton = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(changeUsernameButton, changePasswordButton, logoutButton, deleteAccountButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == logoutButton) {
                Login.user = null;
                showAlert("Logged Out", "You have been logged out.");
                    logout();
            } else if (response == deleteAccountButton) {
                confirmDeleteAccount();
            } else if (response == changeUsernameButton) {
                ChangeUsername();
            } else if (response == changePasswordButton) {
                ChangePassword();
            }
        });
    }

    private void ChangeUsername() {
        TextInputDialog dialog = new TextInputDialog(Login.user.getUsername());
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Enter your new username:");

        dialog.showAndWait().ifPresent(newUsername -> {
            if (newUsername.trim().isEmpty()) {
                showAlert("Invalid", "Username cannot be empty");
                return;
            }

            if (accountManager.usernameExists(newUsername)) {
                showAlert("Error", "Username already exists. Choose another username.");
                return;
            }

            if (accountManager.updateAccount(Login.user.getUsername(), newUsername, null)) {
                Login.user.setUsername(newUsername);
                showAlert("Updated", "Username updated succesfully");
                usernamelabel.setText(newUsername);
            } else {
                showAlert("Error", "Failed to update username.");
            }
        });
    }

    private void ChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new Password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                showAlert("Invalid", "Password cannot be empty.");
                return;
            }

            if (accountManager.updateAccount(Login.user.getUsername(), null, newPassword)) {
            Login.user.setPassword(newPassword);
            showAlert("Updated", "Password updated succesfully.");
            } else {
                showAlert("Error", "Failed to update password.");
            }
        });
    }

    private void confirmDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete your account?");
        confirm.setContentText("This action cannot be undone.");

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        confirm.getButtonTypes().setAll(yes, no);

        confirm.showAndWait().ifPresent(choice -> {
            if (choice == yes) {
                accountManager.deleteUserAccount(Login.user.getUsername());
                Login.user = null;
                showAlert("Account Deleted", "Your account has been successfully deleted.");
                logout();
            }
        });
    }

    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) scrollpane.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}