package io.jfxdevelop.lab3_1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.IOException;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    private Button selectSourceButton;
    @FXML
    private Button selectTargetButton;

    private String selectedSourceFile = null;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onSelectSourceFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите исходный файл");
        File selectedFile = fileChooser.showOpenDialog((Stage) welcomeText.getScene().getWindow());
        if (selectedFile != null) {
            selectedSourceFile = selectedFile.getAbsolutePath();
            welcomeText.setText("Сохранить в..." + selectedSourceFile);
        }
    }

    @FXML
    protected void onSelectTargetFile() {
        if (selectedSourceFile == null) {
            showAlert("Ошибка", "Сначала выберите исходный файл!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите место для сохранения копии");
        // Имя файла по умолчанию
        String defaultName = "copy_of_" + new File(selectedSourceFile).getName();
        fileChooser.setInitialFileName(defaultName);

        File selectedFile = fileChooser.showSaveDialog((Stage) welcomeText.getScene().getWindow());
        if (selectedFile != null) {
            String targetPath = selectedFile.getAbsolutePath();
            try {
                FileManager.copyFile(selectedSourceFile, targetPath);
                welcomeText.setText("Файл скопирован: " + targetPath);
                showAlert("Успех", "Файл успешно скопирован!\n" + targetPath);
            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось скопировать файл:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}