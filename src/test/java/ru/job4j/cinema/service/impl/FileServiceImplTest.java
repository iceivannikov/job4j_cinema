package ru.job4j.cinema.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FileServiceImplTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFindByExistingIdThenReturnFile() {
        File file = File.builder()
                .id(1)
                .name("test.jpg")
                .path("/test/path/test.jpg")
                .build();

        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        Optional<File> result = fileService.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getName()).isEqualTo("test.jpg");
        assertThat(result.get().getPath()).isEqualTo("/test/path/test.jpg");

        verify(fileRepository).findById(1);
    }

    @Test
    void whenFindByNonExistingIdThenReturnEmpty() {
        when(fileRepository.findById(999)).thenReturn(Optional.empty());

        Optional<File> result = fileService.findById(999);

        assertThat(result).isEmpty();
        verify(fileRepository).findById(999);
    }

    @Test
    void whenGetFileContentWithExistingFileAndValidPathThenReturnContent() {
        File file = File.builder()
                .id(1)
                .name("test.jpg")
                .path("/test/path/test.jpg")
                .build();

        byte[] expectedContent = "test file content".getBytes();

        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        try (MockedStatic<Paths> pathsMock = mockStatic(Paths.class);
             MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get("/test/path/test.jpg")).thenReturn(mockPath);
            filesMock.when(() -> Files.readAllBytes(mockPath)).thenReturn(expectedContent);

            Optional<byte[]> result = fileService.getFileContent(1);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedContent);
        }

        verify(fileRepository).findById(1);
    }

    @Test
    void whenGetFileContentWithNonExistingFileThenReturnEmpty() {
        when(fileRepository.findById(999)).thenReturn(Optional.empty());

        Optional<byte[]> result = fileService.getFileContent(999);

        assertThat(result).isEmpty();
        verify(fileRepository).findById(999);
    }

    @Test
    void whenGetFileContentWithIOExceptionThenReturnEmpty() {
        File file = File.builder()
                .id(1)
                .name("test.jpg")
                .path("/invalid/path/test.jpg")
                .build();

        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        try (MockedStatic<Paths> pathsMock = mockStatic(Paths.class);
             MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get("/invalid/path/test.jpg")).thenReturn(mockPath);
            filesMock.when(() -> Files.readAllBytes(mockPath)).thenThrow(new IOException("File not found"));

            Optional<byte[]> result = fileService.getFileContent(1);

            assertThat(result).isEmpty();
        }

        verify(fileRepository).findById(1);
    }

    @Test
    void whenGetFileContentCallsFindByIdInternally() {
        File file = File.builder()
                .id(1)
                .name("test.jpg")
                .path("/test/path/test.jpg")
                .build();

        byte[] expectedContent = "test content".getBytes();

        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        try (MockedStatic<Paths> pathsMock = mockStatic(Paths.class);
             MockedStatic<Files> filesMock = mockStatic(Files.class)) {

            Path mockPath = mock(Path.class);
            pathsMock.when(() -> Paths.get("/test/path/test.jpg")).thenReturn(mockPath);
            filesMock.when(() -> Files.readAllBytes(mockPath)).thenReturn(expectedContent);

            fileService.getFileContent(1);

            verify(fileRepository).findById(1);
        }
    }
}