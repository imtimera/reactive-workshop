package com.bonitasoft.reactiveworkshop.api;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.ArtistAggregate;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultPageIdx;
import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultSize;


@RestController
public class ArtistAPI {
    private Logger logger = LoggerFactory.getLogger(ArtistAPI.class);

    private ArtistService artistService;

    public ArtistAPI(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/artist/{id}")
    public Artist findById(@PathVariable String id) throws NotFoundException {
        try {
            return artistService.getArtistById(id);
        } catch (NotFoundException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping("/artists")
    public Page<Artist> findAll(
            @RequestParam(defaultValue = defaultPageIdx) int pageIndex,
            @RequestParam(defaultValue = defaultSize) int size) {
        return artistService.getAllArtists(pageIndex, size);
    }

    @GetMapping("/artist/{artistId}/comments")
    public ArtistAggregate commentsForArtist(@PathVariable String artistId) throws NotFoundException,
            RestClientException {
        try {
            return artistService.getCommentsForArtist(artistId);
        } catch (NotFoundException  | RestClientException
                exception) {
            logger.error(exception.getMessage());
            throw exception;
        }
    }
}
