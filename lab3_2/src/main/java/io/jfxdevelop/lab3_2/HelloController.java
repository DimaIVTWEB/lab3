package io.jfxdevelop.lab3_2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class HelloController {

    @FXML
    private TextField productNameField;
    @FXML
    private TextField productPriceField;
    @FXML
    private TextField productQuantityField;
    @FXML
    private TextField productIdField;
    @FXML
    private TextField buyQuantityField;
    @FXML
    private VBox productsContainer;
    @FXML
    private Label statusLabel;

    private final StoreService storeService = new StoreService();

    @FXML
    protected void initialize() {
        refreshProductList();
    }

    @FXML
    protected void handleAddProduct() {
        String name = productNameField.getText().trim();
        String priceStr = productPriceField.getText().trim();
        String quantityStr = productQuantityField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            setStatus("Все поля должны быть заполнены!", true);
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr).setScale(2, RoundingMode.HALF_UP);
            int quantity = Integer.parseInt(quantityStr);

            if (storeService.addProduct(name, price, quantity)) {
                setStatus("Товар '" + name + "' успешно добавлен.", false);
                clearAddFields();
                refreshProductList();
            } else {
                setStatus("Ошибка при добавлении товара.", true);
            }
        } catch (NumberFormatException e) {
            setStatus("Цена или количество указаны неверно.", true);
        }
    }

    @FXML
    protected void handleBuyProduct() {
        String idStr = productIdField.getText().trim();
        String qtyStr = buyQuantityField.getText().trim();

        if (idStr.isEmpty() || qtyStr.isEmpty()) {
            setStatus("Укажите ID и количество для покупки.", true);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            int qty = Integer.parseInt(qtyStr);

            if (storeService.buyProduct(id, qty)) {
                setStatus("Покупка успешно оформлена.", false);
                clearBuyFields();
                refreshProductList();
            } else {
                setStatus("Не удалось купить товар. Проверьте ID и наличие на складе.", true);
            }
        } catch (NumberFormatException e) {
            setStatus("ID или количество указаны неверно.", true);
        }
    }

    @FXML
    protected void handleRemoveProduct() {
        String idStr = productIdField.getText().trim();

        if (idStr.isEmpty()) {
            setStatus("Укажите ID товара для удаления.", true);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            if (storeService.removeProduct(id)) {
                setStatus("Товар с ID " + id + " успешно удален.", false);
                clearBuyFields();
                refreshProductList();
            } else {
                setStatus("Товар с ID " + id + " не найден.", true);
            }
        } catch (NumberFormatException e) {
            setStatus("ID указан неверно.", true);
        }
    }

    private void refreshProductList() {
        productsContainer.getChildren().clear();
        List<Product> products = storeService.getAllProducts();

        if (products.isEmpty()) {
            Text emptyText = new Text("Нет товаров в магазине.");
            emptyText.setStyle("-fx-font-style: italic;");
            productsContainer.getChildren().add(emptyText);
        } else {
            for (Product p : products) {
                Text productText = new Text(
                        "ID: " + p.getId() +
                                " | Название: " + p.getName() +
                                " | Цена: " + p.getPrice() +
                                " | Количество: " + p.getQuantity()
                );
                productText.setStyle("-fx-font-family: monospace;");
                productsContainer.getChildren().add(productText);
            }
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    private void clearAddFields() {
        productNameField.clear();
        productPriceField.clear();
        productQuantityField.clear();
    }

    private void clearBuyFields() {
        productIdField.clear();
        buyQuantityField.clear();
    }
}