package id.petani.smartfarm.util;

import org.springframework.web.multipart.MultipartFile;
import id.petani.smartfarm.exception.FileStorageException;

import java.util.Arrays;
import java.util.List;

public class ImageValidator {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif"
    );
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File cannot be empty.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new FileStorageException("Invalid image file type. Only JPEG, PNG, GIF are allowed.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileStorageException("File size exceeds the maximum limit of 2MB.");
        }
    }
}
