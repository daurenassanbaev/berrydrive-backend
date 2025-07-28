package kz.berrydrive.auth.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void setTokenWithTTL(String key, String value, long ttl, TimeUnit timeUnit);
    boolean hasToken(String token);
}
