package com.bonitasoft.reactiveworkshop.api;

import com.bonitasoft.reactiveworkshop.ReactiveWorkshopApplication;
import com.bonitasoft.reactiveworkshop.service.GenreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultPageIdx;
import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultSize;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReactiveWorkshopApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GenreApiTest {
    @InjectMocks
    private GenreApi genreApi;

    @Mock
    private GenreService genreService;


    @Test
    void findAll() {
        final int pageIdx = Integer.valueOf(defaultPageIdx);
        final int size = Integer.valueOf(defaultSize);

        Page<String> page = Mockito.mock(Page.class);
        List<String> allGenres = Arrays.asList("genre1", "genre2", "genre3");

        when(page.getContent()).thenReturn(allGenres);
        when(genreService.getAllGenres(pageIdx, size)).thenReturn(page);

        final Page<String> actual = genreApi.findAll(pageIdx, size);
        Assertions.assertEquals(allGenres, actual.getContent());
    }

    @Test
    void allGenreStream() {
        final String genre = "genre";
        final String[] commentArray = {"comment:1", "comment:2"};
        final Flux<String> expected = Flux.fromArray(commentArray);

        when(genreService.getCommentsStream(genre)).thenReturn(expected);
        final Flux<String> actual = genreApi.allGenreStream(genre);

        Assertions.assertEquals(expected, actual);

    }
}