package ru.job4j.cinema.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;
import ru.job4j.cinema.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public Optional<File> findById(Integer id) {
        return fileRepository.findById(id);
    }

    @Override
    public Optional<byte[]> getFileContent(Integer id) {
        Optional<File> fileOptional = findById(id);
        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }

        File file = fileOptional.get();
        try {
            Path filePath = Paths.get(file.getPath());
            byte[] fileBytes = Files.readAllBytes(filePath);
            return Optional.of(fileBytes);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
