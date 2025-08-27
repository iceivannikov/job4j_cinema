package ru.job4j.cinema.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;
import ru.job4j.cinema.service.FileService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public Optional<File> findById(Integer id) {
        return fileRepository.findById(id);
    }
}
