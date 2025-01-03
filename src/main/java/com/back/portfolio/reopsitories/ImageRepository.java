package com.back.portfolio.reopsitories;

import com.back.portfolio.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository  extends JpaRepository<Image, Long> {}
