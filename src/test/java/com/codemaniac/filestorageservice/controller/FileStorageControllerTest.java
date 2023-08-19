package com.codemaniac.filestorageservice.controller;

import com.codemaniac.filestorageservice.service.S3Service;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class FileStorageControllerTest {


    @Mock
    private S3Service s3Service;

    @InjectMocks
    private FileStorageController fileStorageController;

    private MockMvc mockMvc;

    private final String FILE1 ="file1.txt";
    private final String FILE2 ="file2.txt";

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(fileStorageController).build();
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", FILE1, "text/plain", "some content".getBytes());
        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("File uploaded : "+FILE1);

        mockMvc.perform(multipart("/file/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded : "+FILE1));

        verify(s3Service).uploadFile(any(MultipartFile.class));
    }


    @Test
    public void testUploadMultipleFiles() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", FILE1, MediaType.TEXT_PLAIN_VALUE, "test1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", FILE2, MediaType.TEXT_PLAIN_VALUE, "test2".getBytes());

        this.mockMvc.perform(multipart("/file/upload-multiple")
                        .file(file1)
                        .file(file2))
                .andExpect(status().isOk());

        Mockito.verify(s3Service, Mockito.times(1)).uploadMultipleFiles(Mockito.any());
    }

    @Test
    public void testDownloadFile() throws Exception {

        byte[] content = new byte[] { 1, 2, 3 };
        when(s3Service.downloadFile(eq(FILE1))).thenReturn(content);

        mockMvc.perform(get("/file/download/" + FILE1))
                .andExpect(status().isOk())
                .andExpect(content().bytes(content));

        verify(s3Service).downloadFile(eq(FILE1));
    }

    @Test
    public void testListFiles() throws Exception {
        List<String> fileList = Arrays.asList(FILE1, FILE2);
        when(s3Service.listFileNamesInBucket()).thenReturn(fileList);

        mockMvc.perform(get("/file/file-listing"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"file1.txt\", \"file2.txt\"]"));

        verify(s3Service).listFileNamesInBucket();
    }
}