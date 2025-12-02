package io.jfxdevelop.lab3_2;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreService {
    private static final String FILE_NAME = "products.csv";
    private final Path filePath;
    private List<Product> products = new ArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public StoreService() {
        // Используем папку проекта (или рабочую директорию)
        this.filePath = Paths.get(FILE_NAME).toAbsolutePath().normalize();
        System.out.println("Путь к файлу товаров: " + filePath);
        loadProducts();
    }

    //Если файла нет - создаём новый
    private void loadProducts() {
        products.clear();
        if (!Files.exists(filePath)) {
            System.out.println();
            try {
                Files.createFile(filePath);
                try (BufferedWriter w = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                    w.write("# ID, Name, Price, Quantity\n");
                }
            } catch (IOException e) {
                System.err.println("Ошибка создания файла: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                Product p = Product.fromCsvLine(line);
                if (p != null) {
                    products.add(p);
                    nextId.accumulateAndGet(p.getId(), Math::max);
                }
            }
            nextId.incrementAndGet(); // следующий ID будет на 1 больше
            System.out.println("Загружено " + products.size() + " товаров.");
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
            products = new ArrayList<>();
        }
    }

    private void saveProducts() {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("products_", ".tmp");

            try (BufferedWriter w = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
                w.write("# ID, Name, Price, Quantity\n");
                for (Product p : products) {
                    w.write(p.toCsvLine());
                    w.newLine();
                }
            }

            Files.move(tempFile, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Успешно сохранено " + products.size() + " товаров в " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения! Данные могут быть утеряны.");
            e.printStackTrace();
            if (tempFile != null && Files.exists(tempFile)) {
                try {
                    Files.delete(tempFile);
                } catch (IOException ignored) {}
            }
        }
    }

    public boolean addProduct(String name, BigDecimal price, int quantity) {
        if (name == null || name.isBlank() || price == null || price.compareTo(BigDecimal.ZERO) <= 0 || quantity < 0) {
            System.err.println("Попытка добавить некорректный товар: " + name);
            return false;
        }
        Product p = new Product(nextId.getAndIncrement(), name, price, quantity);
        products.add(p);
        saveProducts();
        return true;
    }

    public boolean removeProduct(int id) {
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) {
            saveProducts();
            System.out.println("Товар ID=" + id + " удалён.");
        } else {
            System.out.println("Товар ID=" + id + " не найден для удаления.");
        }
        return removed;
    }

    public boolean buyProduct(int id, int amount) {
        Optional<Product> productOpt = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();

        if (productOpt.isPresent() && productOpt.get().getQuantity() >= amount) {
            Product p = productOpt.get();
            p.setQuantity(p.getQuantity() - amount);
            saveProducts();
            System.out.println("Куплено " + amount + " шт. товара ID=" + id + ". Остаток: " + p.getQuantity());
            return true;
        }
        System.err.println("Невозможно купить " + amount + " шт. товара ID=" + id);
        return false;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

}