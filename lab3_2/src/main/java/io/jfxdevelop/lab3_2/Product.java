package io.jfxdevelop.lab3_2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private final int id;
    private final String name;
    private final BigDecimal price;
    private int quantity;

    // Формат для записи цены - всегда с точкой, так как в csv-файле разделитель запятая
    private static final DecimalFormat PRICE_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        PRICE_FORMAT = new DecimalFormat("0.00", symbols);
        PRICE_FORMAT.setGroupingUsed(false);
    }

    public Product(int id, String name, BigDecimal price, int quantity) {
        this.id = id;
        this.name = name != null ? name : "";
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.quantity = Math.max(0, quantity);
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }

    // Сеттеры
    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    // Экранируем название
    public String toCsvLine() {
        return String.format("%d,%s,%s,%d",
                id,
                escape(name),
                PRICE_FORMAT.format(price),
                quantity);
    }

    // Учитывает экранирование и локаль
    public static Product fromCsvLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        List<String> parts = parseCsvLine(line);
        if (parts.size() != 4) {
            System.err.println("Неверное количество полей в строке: '" + line + "' (ожидалось 4, получено " + parts.size() + ")");
            return null;
        }

        try {
            int id = Integer.parseInt(parts.get(0).trim());
            String name = unescape(parts.get(1));
            BigDecimal price = parsePrice(parts.get(2).trim()); // Только точка
            int qty = Integer.parseInt(parts.get(3).trim());

            return new Product(id, name, price, qty);
        } catch (NumberFormatException | ParseException e) {
            System.err.println("Ошибка парсинга числа/цены в строке: '" + line + "' - " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка при парсинге строки: '" + line + "'");
            return null;
        }
    }

    // Парсинг цены
    private static BigDecimal parsePrice(String priceStr) throws ParseException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("#.##", symbols);
        format.setParseBigDecimal(true);

        return (BigDecimal) format.parse(priceStr);
    }

    // Разбирает CSV-строку с учётом кавычек и экранирования
    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Экранированная кавычка
                    currentField.append('"');
                    i++; // Пропускаем следующую кавычку
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Конец поля
                fields.add(currentField.toString());
                currentField.setLength(0); // Очищаем
            } else {
                currentField.append(c);
            }
        }

        // Добавляем последнее поле
        fields.add(currentField.toString());

        return fields;
    }

    // Экранируем строку для CSV
    private static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    // Убираем экранирование
    private static String unescape(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + ", quantity=" + quantity + "}";
    }
}