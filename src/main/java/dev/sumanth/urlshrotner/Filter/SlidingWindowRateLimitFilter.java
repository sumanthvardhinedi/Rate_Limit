package dev.sumanth.urlshrotner.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
@Profile("slding-window") // only active in sliding-window profile
@Component
@Order(2) // higher priority
public class SlidingWindowRateLimitFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW = 60; // seconds

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (redisTemplate == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        String key = "sliding:" + ip;

        long now = System.currentTimeMillis();
        long windowStart = now - (WINDOW * 1000);

        // remove old requests
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        Long count = redisTemplate.opsForZSet().zCard(key);

        if (count != null && count >= MAX_REQUESTS) {
            response.setStatus(429);
            response.getWriter().write("Sliding Window Limit Exceeded");
            return;
        }

        // add current request
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);

        redisTemplate.expire(key, Duration.ofSeconds(WINDOW));

        filterChain.doFilter(request, response);
    }
}
