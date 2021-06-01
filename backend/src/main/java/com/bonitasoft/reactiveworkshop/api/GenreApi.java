package com.bonitasoft.reactiveworkshop.api;

import com.bonitasoft.reactiveworkshop.service.GenreService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultPageIdx;
import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultSize;

@RestController
public class GenreApi {
    private GenreService genreService;

    public GenreApi(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres")
    public Page<String> findAll(@RequestParam(defaultValue = defaultPageIdx) int pageIndex,
                                @RequestParam(defaultValue = defaultSize) int size) {
        return genreService.getAllGenres(pageIndex, size);
    }

    @GetMapping("/genres/{genre}/comments/stream")
    public Flux<String> allGenreStream(@PathVariable String genre) {
        return genreService.getCommentsStream(genre);
    }

}
