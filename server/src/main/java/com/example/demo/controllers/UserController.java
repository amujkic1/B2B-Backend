package com.example.demo.controllers;

import com.example.demo.repositories.UserRepository;
import com.example.demo.services.StorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final StorageService storageService;
    private final UserRepository userRepository;

    public UserController(StorageService storageService, UserRepository userRepository) {
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file, Principal principal) {

        String publicUrl = storageService.uploadResume(file, principal.getName());

        return ResponseEntity.ok("Resume uploaded successfully! Link: " + publicUrl);
    }
}