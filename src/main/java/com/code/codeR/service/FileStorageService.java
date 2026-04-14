package com.code.codeR.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${storage.local.input-dir:Storage/input}")
    private String inputDir;

    @Value("${storage.local.output-dir:Storage/output}")
    private String outputDir;

    @PostConstruct
    public void init() {
        try {
            Path inputPath = Paths.get(inputDir).toAbsolutePath();
            Path outputPath = Paths.get(outputDir).toAbsolutePath();
            
            Files.createDirectories(inputPath);
            Files.createDirectories(outputPath);
            
            System.out.println("📂 Storage Initialized At:");
            System.out.println("   Input:  " + inputPath);
            System.out.println("   Output: " + outputPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directories", e);
        }
    }

    public String saveInputFile(MultipartFile file) throws IOException {
        return saveFile(file, inputDir);
    }

    public String saveOutputFile(MultipartFile file) throws IOException {
        return saveFile(file, outputDir);
    }

    private String saveFile(MultipartFile file, String targetDir) throws IOException {
        // Simple file naming pattern to ensure uniqueness
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetLocation = Paths.get(targetDir).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        // We return only the file name, not the whole path, to save in DB
        return fileName;
    }

    public InputStream getFileInputStream(String fileName, boolean isInput) throws IOException {
        Path filePath = Paths.get(isInput ? inputDir : outputDir).resolve(fileName);
        return Files.newInputStream(filePath);
    }

    public Path getFilePath(String fileName, boolean isInput) {
        return Paths.get(isInput ? inputDir : outputDir).resolve(fileName);
    }

    public void deleteInputFile(String fileName) {
        deleteFile(fileName, inputDir);
    }

    public void deleteOutputFile(String fileName) {
        deleteFile(fileName, outputDir);
    }

    private void deleteFile(String fileName, String targetDir) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Path targetLocation = Paths.get(targetDir).resolve(fileName);
            Files.deleteIfExists(targetLocation);
        } catch (IOException e) {
            System.err.println("Could not delete file " + fileName + ": " + e.getMessage());
        }
    }
}
