package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest {

    @InjectMocks
    private IndexController indexController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetIndexWithRootPathThenReturnIndexView() {
        String result = indexController.getIndex();

        assertThat(result).isEqualTo("index");
    }

    @Test
    void whenGetIndexWithIndexPathThenReturnIndexView() {
        String result = indexController.getIndex();

        assertThat(result).isEqualTo("index");
    }

    @Test
    void whenGetIndexThenAlwaysReturnSameView() {
        String result1 = indexController.getIndex();
        String result2 = indexController.getIndex();

        assertThat(result1).isEqualTo("index");
        assertThat(result2).isEqualTo("index");
        assertThat(result1).isEqualTo(result2);
    }
}