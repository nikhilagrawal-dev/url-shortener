package com.nikhil.urlshortener.util;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class ShortcodeGenerator {

  private static final char[] BASE62 =
      "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private static final int CODE_LENGTH = 6;

  private final SecureRandom secureRandom = new SecureRandom();

  public String generate() {
    char[] code = new char[CODE_LENGTH];
    for (int i = 0; i < CODE_LENGTH; i++) {
      code[i] = BASE62[secureRandom.nextInt(BASE62.length)];
    }
    return new String(code);
  }
}

