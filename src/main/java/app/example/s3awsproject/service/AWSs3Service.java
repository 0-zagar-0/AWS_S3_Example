package app.example.s3awsproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AWSs3Service {
    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public AWSs3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            s3Client.putObject(bucketName, file.getOriginalFilename(), inputStream, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "File: " + file.getOriginalFilename() + " uploaded successfully";
    }

    public ResponseEntity<InputStreamResource> downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        String contentType = determineContentType(fileName);

        try (InputStream inputStream = s3Object.getObjectContent();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        ) {
            byte[] data = new byte[1024];
            int length;

            while ((length = inputStream.read(data)) != -1) {
                buffer.write(data, 0, length);
            }

            byte[] fileData = buffer.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
            InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\""
                    )
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private String determineContentType(String fileName) {
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream";
        }
    }
}
