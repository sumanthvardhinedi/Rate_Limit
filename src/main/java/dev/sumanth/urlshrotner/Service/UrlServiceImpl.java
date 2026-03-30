package dev.sumanth.urlshrotner.Service;

import dev.sumanth.urlshrotner.Models.UrlMap;
import dev.sumanth.urlshrotner.Repo.UrlRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;
    private final UrlRepo urlRepo;

    @Override
    public String shorten(String originalUrl) {
        UrlMap mapping = new UrlMap();
        mapping.setOriginalUrl(originalUrl);
        mapping.setCreatedAt(java.time.LocalDateTime.now());
        UrlMap saved = urlRepo.save(mapping);

        String shortCode = dev.sumanth.urlshrotner.Util.Base62Encoder.encode(saved.getId());
        saved.setShortCode(shortCode);
        urlRepo.save(saved);
        // Cache immediately if Redis is available
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(shortCode, originalUrl, Duration.ofHours(24));
        }
        return shortCode;
    }
    public String resolve(String shortCode) {
        if (redisTemplate != null) {
            String cached = redisTemplate.opsForValue().get(shortCode);
            if (cached != null) return cached;
        }
        UrlMap mapping = urlRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(shortCode, mapping.getOriginalUrl(), Duration.ofHours(24));
        }

        return mapping.getOriginalUrl();
    }
}
