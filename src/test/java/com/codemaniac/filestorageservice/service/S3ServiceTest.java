package com.codemaniac.filestorageservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3Client;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private FileUtilWrapper fileUtilWrapper;

    @Mock
    private File file;

    @InjectMocks
    private S3Service s3Service;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "test");
        when(fileUtilWrapper.convertMultiPartFileToFile(any(MultipartFile.class))).thenReturn(file);
    }

    @Test
    public void testUploadFile() throws IOException {

        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        byte[] mockBytes = new byte[] { 1, 2, 3 };
//        // Use anyString() to match the String parameter
        doNothing().when(fileUtilWrapper).writeFile(any(File.class), eq(mockBytes));

        // Testing the method
        String result = s3Service.uploadFile(multipartFile);

        // Verifying behavior
        verify(amazonS3Client).putObject(any(PutObjectRequest.class));
        verify(file).delete();

        // Assertion using JUnit's Assert
        Assert.assertTrue(result.contains("File uploaded :"));
    }
}