package com.prj.furni_shop.providers.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {
    Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("image/jpg"))
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(500,500)
                .outputFormat("jpeg")
                .outputQuality(0.7)
                .toOutputStream(outputStream);

        File tempFile = File.createTempFile("upload", ".jpeg");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(outputStream.toByteArray());
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
        tempFile.delete();
        return uploadResult.get("url").toString();
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        return publicIdWithExtension.split("\\.")[0];
    }
}
