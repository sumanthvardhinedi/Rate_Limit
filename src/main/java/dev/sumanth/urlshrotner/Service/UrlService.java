package dev.sumanth.urlshrotner.Service;

public interface UrlService {

    String shorten(String originalUrl);
    String resolve(String code);
}