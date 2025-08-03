package kz.berrydrive.file.config;

import kz.berrydrive.file.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials basicCredentials = AwsBasicCredentials.create(s3Properties.getAccessKeyId(), s3Properties.getSecretKey());
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }
}
