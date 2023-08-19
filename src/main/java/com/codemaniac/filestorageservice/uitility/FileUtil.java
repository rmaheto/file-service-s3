package com.codemaniac.filestorageservice.uitility;

import com.amazonaws.util.IOUtils;
import com.codemaniac.filestorageservice.service.FileUtilWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Component
@Slf4j
public class FileUtil implements FileUtilWrapper {

    @Override
    public void writeFile(File file, byte[] content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content);
        }
    }

    @Override
    public File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            writeFile(convertedFile, file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    @Override
    public byte[] toByteArray(InputStream inputStream) throws IOException {
        return IOUtils.toByteArray(inputStream);
    }
}
