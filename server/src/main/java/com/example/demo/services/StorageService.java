package com.example.demo.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadResume(MultipartFile file, String email);
}