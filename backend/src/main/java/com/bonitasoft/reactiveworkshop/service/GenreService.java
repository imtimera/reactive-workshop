package com.bonitasoft.reactiveworkshop.service;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.bonitasoft.reactiveworkshop.util.ApiConstant.rootUri;

@Service
public class GenreService {

    private ArtistRepository artistRepository;
    private WebClient webClient;

    public GenreService(ArtistRepository artistRepository, WebClient.Builder webClientBuilder) {
        this.artistRepository = artistRepository;
        this.webClient = webClientBuilder.baseUrl(rootUri).build();
    }

    public Page<String> getAllGenres(int pageIndex, int size) {
        Pageable pageable = PageRequest.of(pageIndex, size);
        return new PageImpl<>(artistRepository.findAll(pageable).stream()
                .map(Artist::getGenre)
                .filter(g -> !g.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList()));
    }

    public Flux<String> getCommentsStream(String genre) {
        final List<Artist> artistsByGenre = getArtistsIds(genre);

        return Flux.fromIterable(artistsByGenre)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(this::callExternalApi)
                .ordered(Comparator.naturalOrder());
    }

    private Flux<String> callExternalApi(Artist artist) {
        String artistId = artist.getId();
        final Flux<String> commentsFlux = this.webClient.get()
                .uri("/comments/{artistId}/stream", artistId)
                .exchangeToFlux(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToFlux(String.class);
                    }
                    return Flux.error(new IllegalStateException("Body has not been written yet"));
                });

        //.bodyToFlux(String.class);

        return commentsFlux;
    }

    private List<Artist> getArtistsIds(String genre) {
        return artistRepository.findByGenre(genre);
    }
}
