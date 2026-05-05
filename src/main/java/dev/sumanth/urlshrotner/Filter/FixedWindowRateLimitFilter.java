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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
@Order(1) // lower priority
@Profile("fixed-window") // only active in fixed-window profile
public class FixedWindowRateLimitFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(1);

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
        String key = "fixed:" + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        if (count != null && count > MAX_REQUESTS) {
            response.setStatus(429);
            response.getWriter().write("Fixed Window Limit Exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }
}