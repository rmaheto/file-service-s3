package com.codemaniac.filestorageservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    private final String FILE1 ="file1.txt";
    private final String FILE2 ="file2.txt";

    @Before
    public void setup() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "test");
        when(fileUtilWrapper.convertMultiPartFileToFile(any(MultipartFile.class))).thenReturn(file);
    }

    @Test
    public void testUploadFile() throws IOException {

        when(multipartFile.getOriginalFilename()).thenReturn(FILE1);
        byte[] mockBytes = new byte[] { 1, 2, 3 };

        String result = s3Service.uploadFile(multipartFile);

        verify(amazonS3Client).putObject(any(PutObjectRequest.class));
        verify(file).delete();

        Assert.assertTrue(result.contains("File uploaded :"));
    }

    @Test
    public void testUploadMultipleFiles() {
        MultipartFile file1 = new MockMultipartFile("files", FILE1, "text/plain", "test1".getBytes());
        MultipartFile file2 = new MockMultipartFile("files", FILE2, "text/plain", "test2".getBytes());
        List<MultipartFile> files = Arrays.asList(file1, file2);

        s3Service.uploadMultipleFiles(files);

        verify(fileUtilWrapper, times(2)).convertMultiPartFileToFile(any(MultipartFile.class));
        verify(amazonS3Client, times(2)).putObject(any(PutObjectRequest.class));
        verify(file, times(2)).delete();
    }

    @Test
    public void testDownloadFile() throws IOException {

        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream inputStream = mock(S3ObjectInputStream.class);
        byte[] content = new byte[] { 1, 2, 3 };

        when(amazonS3Client.getObject(anyString(), eq(FILE1))).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(inputStream);
        when(fileUtilWrapper.toByteArray(inputStream)).thenReturn(content);

        byte[] result = s3Service.downloadFile(FILE1);

        verify(amazonS3Client).getObject(anyString(), eq(FILE1));
        verify(s3Object).getObjectContent();

        Assert.assertArrayEquals(content, result);
    }

    @Test
    public void testListFileNamesInBucket() {
        ObjectListing objectListing = mock(ObjectListing.class);
        S3ObjectSummary s3ObjectSummary1 = new S3ObjectSummary();
        s3ObjectSummary1.setKey(FILE1);
        S3ObjectSummary s3ObjectSummary2 = new S3ObjectSummary();
        s3ObjectSummary2.setKey(FILE2);
        List<S3ObjectSummary> summaries = Arrays.asList(s3ObjectSummary1, s3ObjectSummary2);

        when(amazonS3Client.listObjects(anyString())).thenReturn(objectListing);
        when(objectListing.getObjectSummaries()).thenReturn(summaries);

        List<String> result = s3Service.listFileNamesInBucket();

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(FILE1));
        Assert.assertTrue(result.contains(FILE2));
    }

    @Test
    public void testDeleteFile() {

        String result = s3Service.deleteFile(FILE1);

        verify(amazonS3Client).deleteObject(anyString(), eq(FILE1));
        Assert.assertEquals(FILE1 + " removed...", result);
    }
}