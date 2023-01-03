package com.examp.minio.service;

import com.examp.minio.model.FileDto;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MinioService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.default.folder}")
    private String defaultFolder;

    public List<FileDto> getListObjects() {
        List<FileDto> objects = new ArrayList<>();
        try {
            Iterable<Result<Item>> result = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build());
            for (Result<Item> item : result) {
                objects.add(FileDto.builder()
                        .filename(item.get().objectName())
                        .size(item.get().size())
                        .url(getPreSignedUrl(item.get().objectName()))
                        .build());
            }
            return objects;
        } catch (Exception e) {
            log.error("Happened error when get list objects from minio: ", e);
        }

        return objects;
    }

    private String getPreSignedUrl(String filename) {
        return "http://192.168.18.207:9000/example-product-image/".concat(filename);
    }


    public FileDto uploadFile(FileDto request) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(request.getFilename())
                    .stream(request.getFile().getInputStream(), request.getFile().getSize(), -1)
                    .build());
        } catch (Exception e) {
            log.error("Happened error when upload file: ", e);
        }
        return FileDto.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .size(request.getSize())
//                .url(getPreSignedUrl(request.getFilename()))
                .filename(request.getFilename())
                .build();
    }

    public void uploadFileDto(String filename) throws IOException {

//        byte[] content = decode64(encodedContent);
//
//        String path = "C:\\decodedImage\\";

        String folder = defaultFolder;
        File file = new File(folder + filename + ".png");

        try {

            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .filename(file.getAbsolutePath())
                    .contentType("image/png")
                    .build());
        } catch (Exception e) {
            log.error("minio upload file exception : ", e);
        }
        log.info("minio : file with id {}, has been successfully uploaded", filename);
    }
    public byte[] decode64(String encodedString) throws IOException {

        //byte[] decodedBytes = Base64.decodeBase64(encodedString);
        //Files.write(Paths.get("C:\\decodedImage\\image.png"), decodedBytes);
        if (encodedString == null) return null;
        return Base64.decodeBase64(encodedString);
    }
}
