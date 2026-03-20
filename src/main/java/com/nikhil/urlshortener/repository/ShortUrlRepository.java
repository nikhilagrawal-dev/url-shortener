package com.nikhil.urlshortener.repository;

import com.nikhil.urlshortener.entity.ShortUrl;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
  Optional<ShortUrl> findByShortCode(String shortCode);

  Optional<ShortUrl> findByOriginalUrl(String originalUrl);
}

