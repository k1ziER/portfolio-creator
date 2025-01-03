package com.back.portfolio.reopsitories;

import com.back.portfolio.models.File;
import com.back.portfolio.models.Image;
import com.back.portfolio.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {}
