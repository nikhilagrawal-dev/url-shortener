package com.nikhil.urlshortener.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlResponse {
  private String shortCode;
  private String shortUrl;
  private String originalUrl;
  private Instant createdAt;
  private Instant expiresAt;
}

