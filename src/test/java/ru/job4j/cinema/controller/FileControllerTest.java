package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.job4j.cinema.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetFileWithExistingIdThenReturnFileContent() {
        int fileId = 1;
        byte[] fileContent = "test file content".getBytes();

        when(fileService.getFileContent(fileId)).thenReturn(Optional.of(fileContent));

        ResponseEntity<byte[]> result = fileController.getFile(fileId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(fileContent);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);

        verify(fileService).getFileContent(fileId);
    }

    @Test
    void whenGetFileWithNonExistingIdThenReturnNotFound() {
        int fileId = 999;

        when(fileService.getFileContent(fileId)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> result = fileController.getFile(fileId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNull();

        verify(fileService).getFileContent(fileId);
    }

    @Test
    void whenGetFileWithEmptyContentThenReturnNotFound() {
        int fileId = 2;

        when(fileService.getFileContent(fileId)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> result = fileController.getFile(fileId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(fileService).getFileContent(fileId);
    }

    @Test
    void whenGetFileThenHeadersAreSetCorrectly() {
        int fileId = 1;
        byte[] fileContent = new byte[]{1, 2, 3, 4, 5};

        when(fileService.getFileContent(fileId)).thenReturn(Optional.of(fileContent));

        ResponseEntity<byte[]> result = fileController.getFile(fileId);

        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(fileContent);
    }
}