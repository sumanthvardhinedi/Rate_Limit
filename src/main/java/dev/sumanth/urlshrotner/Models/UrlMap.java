package dev.sumanth.urlshrotner.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
public class UrlMap {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String originalUrl;
        private String shortCode;
        private LocalDateTime createdAt;
    }
