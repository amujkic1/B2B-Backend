package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SupabaseStorageService implements StorageService {

    private final UserRepository userRepository;

    public SupabaseStorageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String uploadResume(MultipartFile file, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String fileName = "user_" + user.getId() + "_resume.pdf";
        String uploadUrl = supabaseUrl + "/storage/v1/object/resumes/" + fileName;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("Content-Type", file.getContentType())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String publicUrl = supabaseUrl + "/storage/v1/object/public/resumes/" + fileName;
                user.setResumeUrl(publicUrl);
                userRepository.save(user);
                return publicUrl;
            } else {
                throw new RuntimeException("Supabase upload failed: " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Supabase", e);
        }
    }
}