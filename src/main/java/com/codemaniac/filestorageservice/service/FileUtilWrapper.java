package com.codemaniac.filestorageservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileUtilWrapper {
    void writeFile(File file, byte[] content) throws IOException;
    File convertMultiPartFileToFile(MultipartFile file);
    byte[] toByteArray(InputStream inputStream) throws IOException;
}
