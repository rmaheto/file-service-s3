package com.codemaniac.filestorageservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class S3Service {

    @Value("${application.s3-bucket.name}")
    private String bucketName;

    private final AmazonS3 amazonS3Client;
    private final FileUtilWrapper fileUtilWrapper;

    @Autowired
    public S3Service(AmazonS3 amazonS3Client, FileUtilWrapper fileUtilWrapper) {
        this.amazonS3Client = amazonS3Client;
        this.fileUtilWrapper = fileUtilWrapper;
    }

    public String uploadFile(MultipartFile file) {
        File fileObj = fileUtilWrapper.convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
        return "File uploaded : " + fileName;
    }

    public void uploadMultipleFiles(List<MultipartFile> files) {
        if (files != null) {
            files.forEach(multipartFile -> {
                uploadFile(multipartFile);
            });
        }
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = amazonS3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            log.debug("error converting inputstream from s3 bucket to byte array: ", e);
        }
        return null;
    }

    public List<String> listFileNamesInBucket() {

        ObjectListing objectListing = amazonS3Client.listObjects(bucketName);

        return objectListing.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    public String deleteFile(String fileName) {
        amazonS3Client.deleteObject(bucketName, fileName);
        return  fileName + " removed...";
    }



}
