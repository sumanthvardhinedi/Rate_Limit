package dev.sumanth.urlshrotner.Repo;

import dev.sumanth.urlshrotner.Models.UrlMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UrlRepo extends JpaRepository<UrlMap, Long> {
        Optional<UrlMap> findByShortCode(String shortCode);
}