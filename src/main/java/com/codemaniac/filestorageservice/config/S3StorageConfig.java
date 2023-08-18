package com.codemaniac.filestorageservice.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3StorageConfig {

    @Value("${aws.region.static}")
    private String region;
    @Value("${aws.credentials.accesskey}")
    private String accessKey;
    @Value("${aws.credentials.secretkey}")
    private String secretKey;


    @Bean
    public AmazonS3 amazonS3Client(){

        AWSCredentials credentials =  new BasicAWSCredentials(accessKey,secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

    }
}
