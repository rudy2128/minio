package com.examp.minio.controller;

import com.examp.minio.model.FileDto;
import com.examp.minio.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@Slf4j
public class FileController {
    @Autowired
    private MinioService minioService;

    @GetMapping(value = "/getAllFile")
    public ResponseEntity<Object> getFiles()throws NullPointerException {
        return ResponseEntity.ok(minioService.getListObjects());
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<Object> upload(@ModelAttribute FileDto request)throws NullPointerException {
        return ResponseEntity.ok().body(minioService.uploadFile(request));
    }

    @CrossOrigin
    @PostMapping(path = "/upload-files", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map<String, String> uploadFile(@RequestPart(value = "file", required = false) MultipartFile files) throws IOException {
        minioService.uploadFileDto(files.getOriginalFilename());
        Map<String, String> result = new HashMap<>();
        result.put("imageUrl", files.getOriginalFilename());
        return result;
    }
}
