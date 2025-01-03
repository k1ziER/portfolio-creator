package com.back.portfolio.controllers;

import com.back.portfolio.models.Image;
import com.back.portfolio.reopsitories.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageRepository imageRepository;

    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImageById(@PathVariable Long id) {
        log.info("Запрос изображения с id: {}", id);
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            log.info("Изображение найдено: {}", image.getOriginalFileName());
            String contentType = image.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                log.warn("Content-Type для изображения id {} не установлен. Устанавливаем значение по умолчанию.", id);
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            MediaType mediaType;
            try {
                mediaType = MediaType.parseMediaType(contentType);
            } catch (InvalidMediaTypeException e) {
                log.error("Некорректный MIME-тип '{}' для изображения id {}", contentType, id);
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Некорректный тип содержимого изображения");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getOriginalFileName() + "\"")
                    .contentType(mediaType)
                    .contentLength(image.getSize())
                    .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
        } else {
            log.warn("Изображение с id {} не найдено", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Изображение не найдено");
        }
    }
}

