package com.back.portfolio.controllers;

import com.back.portfolio.models.File;
import com.back.portfolio.models.Image;
import com.back.portfolio.reopsitories.FileRepository;
import com.back.portfolio.reopsitories.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileRepository fileRepository;

    @GetMapping("/file/{id}")
    public ResponseEntity<?> getFileById(@PathVariable Long id) {
        log.info("Запрос файла с id: {}", id);
        Optional<File> fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            File file = fileOptional.get();
            log.info("Файл найден: {}", file.getFileName());
            String contentType = file.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                log.warn("Content-Type для файла id {} не установлен. Устанавливаем значение по умолчанию.", id);
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            MediaType mediaType;
            try {
                mediaType = MediaType.parseMediaType(contentType);
            } catch (InvalidMediaTypeException e) {
                log.error("Некорректный MIME-тип '{}' для файла id {}", contentType, id);
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Некорректный тип содержимого файла");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .contentType(mediaType)
                    .contentLength(file.getSize())
                    .body(file.getBytes());
        } else {
            log.warn("Файл с id {} не найдено", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Файл не найден");
        }
    }
}
