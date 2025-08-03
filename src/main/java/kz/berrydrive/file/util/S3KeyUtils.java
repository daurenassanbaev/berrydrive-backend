package kz.berrydrive.file.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class S3KeyUtils {

    private static final String KEY_FORMAT = "%d/%s_%s";

    public static String generateKey(String fileName, Long userId) {
        return KEY_FORMAT.formatted(userId, UUID.randomUUID(), fileName);
    }
}
