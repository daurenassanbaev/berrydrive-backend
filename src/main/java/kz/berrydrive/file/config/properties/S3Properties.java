package kz.berrydrive.file.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aws")
public class S3Properties {
    private String accessKeyId;
    private String secretKey;
    private String region;
    private String bucket;
}
