package divar.aut.backend.service;

import divar.aut.backend.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Handles storage and retrieval of image files uploaded for ads.
 */
@Service
public class ImageStorageService {

    private static final String UPLOAD_DIRECTORY = "uploads/";

    /**
     * Save an uploaded file and return its unique filename.
     * Throws ApiException if the file is empty or cannot be saved.
     */
    public String save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw ApiException.badRequest("فایل انتخاب‌شده خالی است");
            }

            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(UPLOAD_DIRECTORY);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                originalFileName = "image";
            }
            String fileExtension = originalFileName.substring(
                    originalFileName.lastIndexOf('.') >= 0 ? originalFileName.lastIndexOf('.') : originalFileName.length());
            String uniqueFileName = UUID.randomUUID() + fileExtension;

            // Save file
            Path filePath = uploadDir.resolve(uniqueFileName);
            Files.write(filePath, file.getBytes());

            return uniqueFileName;
        } catch (IOException e) {
            throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error saving file: " + e.getMessage());
        }
    }
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(UPLOAD_DIRECTORY).resolve(fileName));
        } catch (IOException ignored) {
        }
    }

}
