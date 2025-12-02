package io.jfxdevelop.lab3_1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        // Проверка на null или пустую строку
        if (sourcePath == null || sourcePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Исходный путь не может быть null или пустым.");
        }
        if (targetPath == null || targetPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Целевой путь не может быть null или пустым.");
        }

        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);

        // Проверка, что исходный путь указывает на существующий файл
        if (!Files.exists(source)) {
            throw new IOException("Исходный файл не существует: " + sourcePath);
        }
        if (!Files.isReadable(source)) {
            throw new IOException("Нет прав на чтение исходного файла: " + sourcePath);
        }

        // Проверка, что исходный путь не является директорией
        if (Files.isDirectory(source)) {
            throw new IOException("Исходный путь указывает на директорию, а не на файл: " + sourcePath);
        }

        // Создание родительской директории для целевого файла, если она не существует
        Files.createDirectories(target.getParent());

        // Копирование файла с опцией замены, если целевой файл уже существует
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}