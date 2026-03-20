package com.nikhil.urlshortener.service;

import com.nikhil.urlshortener.entity.ShortUrl;
import com.nikhil.urlshortener.repository.ShortUrlRepository;
import com.nikhil.urlshortener.util.ShortcodeGenerator;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlShortenService {

  private static final int MAX_GENERATION_ATTEMPTS = 25;

  private final ShortUrlRepository shortUrlRepository;
  private final ShortcodeGenerator shortcodeGenerator;

  public UrlShortenService(ShortUrlRepository shortUrlRepository, ShortcodeGenerator shortcodeGenerator) {
    this.shortUrlRepository = shortUrlRepository;
    this.shortcodeGenerator = shortcodeGenerator;
  }

  @Transactional
  public ShortUrl createShortUrl(String originalUrl) {
    Optional<ShortUrl> existing = shortUrlRepository.findByOriginalUrl(originalUrl);
    if (existing.isPresent()) {
      return existing.get();
    }

    for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
      String code = shortcodeGenerator.generate();

      if (shortUrlRepository.findByShortCode(code).isPresent()) {
        continue;
      }

      ShortUrl shortUrl = new ShortUrl();
      shortUrl.setOriginalUrl(originalUrl);
      shortUrl.setShortCode(code);

      try {
        return shortUrlRepository.save(shortUrl);
      } catch (DataIntegrityViolationException e) {
        // Most likely a unique constraint race/collision; retry with a new code.
      }
    }

    throw new IllegalStateException("Could not generate a unique short code after retries");
  }

  @Transactional(readOnly = true)
  public Optional<String> getOriginalUrl(String shortCode) {
    return shortUrlRepository.findByShortCode(shortCode).map(ShortUrl::getOriginalUrl);
  }
}

