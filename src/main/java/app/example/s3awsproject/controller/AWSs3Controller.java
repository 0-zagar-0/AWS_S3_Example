package app.example.s3awsproject.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import app.example.s3awsproject.service.AWSs3Service;

@RestController
@RequestMapping(value = "/aws")
public class AWSs3Controller {
    private final AWSs3Service awsService;

    public AWSs3Controller(AWSs3Service awsService) {
        this.awsService = awsService;
    }

    @PostMapping(value = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadFile(@RequestParam(name = "file") MultipartFile file) {
        return awsService.uploadFile(file);
    }

    @GetMapping(value = "/download/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        return awsService.downloadFile(fileName);
    }
}
