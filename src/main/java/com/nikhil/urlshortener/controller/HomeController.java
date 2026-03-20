package com.nikhil.urlshortener.controller;

import com.nikhil.urlshortener.dto.ShortenUrlRequest;
import com.nikhil.urlshortener.dto.ShortenUrlResponse;
import com.nikhil.urlshortener.entity.ShortUrl;
import com.nikhil.urlshortener.service.UrlShortenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class HomeController {

  private final UrlShortenService urlShortenService;

  public HomeController(UrlShortenService urlShortenService) {
    this.urlShortenService = urlShortenService;
  }

  @GetMapping("/")
  public String home(Model model) {
    if (!model.containsAttribute("shortenUrlRequest")) {
      model.addAttribute("shortenUrlRequest", new ShortenUrlRequest());
    }
    return "index";
  }

  @PostMapping("/shorten")
  public String shorten(
      @Valid @ModelAttribute("shortenUrlRequest") ShortenUrlRequest request,
      BindingResult bindingResult,
      HttpServletRequest httpServletRequest,
      Model model) {
    if (bindingResult.hasErrors()) {
      return "index";
    }

    ShortUrl saved = urlShortenService.createShortUrl(request.getUrl());
    String baseUrl = getBaseUrl(httpServletRequest);

    ShortenUrlResponse response = new ShortenUrlResponse();
    response.setShortCode(saved.getShortCode());
    response.setOriginalUrl(saved.getOriginalUrl());
    response.setCreatedAt(saved.getCreatedAt());
    response.setExpiresAt(saved.getExpiresAt());
    response.setShortUrl(baseUrl + "/" + saved.getShortCode());

    model.addAttribute("result", response);
    return "index";
  }

  @GetMapping("/{shortcode}")
  public String redirect(@PathVariable String shortcode) {
    Optional<String> original = urlShortenService.getOriginalUrl(shortcode);
    if (original.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found");
    }
    return "redirect:" + original.get();
  }

  private static String getBaseUrl(HttpServletRequest request) {
    String scheme = request.getScheme();
    String host = request.getServerName();
    int port = request.getServerPort();

    boolean isDefaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
        || ("https".equalsIgnoreCase(scheme) && port == 443);

    if (isDefaultPort) {
      return scheme + "://" + host;
    }
    return scheme + "://" + host + ":" + port;
  }
}

