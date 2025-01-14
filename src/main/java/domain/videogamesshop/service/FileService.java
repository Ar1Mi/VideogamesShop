package domain.videogamesshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    // Путь к папке, куда складываем загруженные файлы.
    // Например, "uploads" в корне проекта или абсолютный путь на диске.
    private final Path rootLocation = Paths.get("uploads");

    public FileService() {
        try {
            // Убедимся, что папка существует (или создадим)
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для загрузок", e);
        }
    }

    /**
     * Сохранить файл (вернуть относительный путь, который потом запишем в БД).
     */
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Генерируем уникальное имя файла (например, UUID + оригинальное имя).
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + (originalFilename != null ? originalFilename : "image");

        try {
            // Путь, по которому фактически сохраним файл:
            Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize();
            // Сохраняем файл на диск
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Вернём относительный путь, который можно сохранить в БД
            // Например, "uploads/uuid_filename.jpg" или просто "uuid_filename.jpg"
            return uniqueFilename;
        } catch (IOException e) {
//            log.error("Ошибка сохранения файла: {}", e.getMessage());
            throw new RuntimeException("Ошибка сохранения файла", e);
        }
    }

    /**
     * Удалить файл (если он есть).
     */
    public void deleteFile(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            Path filePath = this.rootLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {

        }
    }
}
