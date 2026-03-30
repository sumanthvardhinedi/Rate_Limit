package dev.sumanth.urlshrotner.Controller;

import dev.sumanth.urlshrotner.Service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor

public class UrlController {

    private final UrlService urlService;
    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestBody Map<String, String> body) {
        String shortCode = urlService.shorten(body.get("url"));
        return ResponseEntity.ok("http://localhost:8080/" + shortCode);
    }
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = urlService.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }
}
