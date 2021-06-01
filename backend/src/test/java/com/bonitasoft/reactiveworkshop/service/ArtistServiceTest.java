package com.bonitasoft.reactiveworkshop.service;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.ArtistAggregate;
import com.bonitasoft.reactiveworkshop.domain.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.util.ApiConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultPageIdx;
import static com.bonitasoft.reactiveworkshop.util.ApiConstant.rootUri;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class ArtistServiceTest {
    @InjectMocks
    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void getArtistById_InvalidId_NotFoundExceptionWillBeThrown() throws NotFoundException {
        final String badId = "mon-bad-id-artist";

        given(artistRepository.findById(badId))
                .willAnswer(invocation -> {
                    throw new NotFoundException("Cannot find artist with id : " + badId);
                });

        Assertions.assertThrows(NotFoundException.class, () -> {
            artistService.getArtistById(badId);
        });
    }

    @Test
    void getArtistById_ValidId_ValidArtistWillBeReturn() throws NotFoundException {
        final String id = "mon-super-id-artist";
        final Artist expectedArtist = new Artist(id, "top-artist", "top-genre");
        when(artistRepository.findById(id))
                .thenReturn(Optional.of(expectedArtist));

        final Artist actualArtist = artistService.getArtistById(id);

        Assertions.assertEquals(expectedArtist, actualArtist);
    }

    @Test
    void getAllArtists() {
        final Artist artist1 = new Artist("id1", "artist1", "genre1");
        final Artist artist2 = new Artist("id2", "artist2", "genre2");
        final Artist artist3 = new Artist("id3", "artist3", "genre1");
        final Artist artist4 = new Artist("id4", "artist4", "genre2");
        List<Artist> expectedAll = Arrays.asList(artist1, artist2, artist3, artist4);

        final int pageIndex = Integer.parseInt(defaultPageIdx);
        final int defaultSize = Integer.parseInt(ApiConstant.defaultSize);
        Pageable pageable = PageRequest.of(pageIndex, defaultSize);
        final PageImpl<Artist> expectedPage = new PageImpl<>(expectedAll);

        when(artistRepository.findAll(pageable))
                .thenReturn(expectedPage);

        final Page<Artist> actualAll = artistService.getAllArtists(pageIndex, defaultSize);

        Assertions.assertEquals(expectedPage, actualAll);
    }

    @Test
    void getCommentsForArtist_invalidArtistId_NotFoundExceptionWillBeThrown() throws NotFoundException {
        final String badId = "mon-bad-id-artist";

        given(artistRepository.findById(badId))
                .willAnswer(invocation -> {
                    throw new NotFoundException("Cannot find artist with id : " + badId);
                });

        Assertions.assertThrows(NotFoundException.class, () -> {
            artistService.getCommentsForArtist(badId);
        });
    }

    @Test
    void getCommentsForArtist_shutdownExternalApi_RestClientExceptionWillBeThrown() throws NotFoundException {
        final String id = "mon-super-id-artist";
        final Artist expectedArtist = new Artist(id, "top-artist", "top-genre");
        final RestClientException restClientException =
                new RestClientException("Internal Server Error");

        when(artistRepository.findById(id))
                .thenReturn(Optional.of(expectedArtist));


        when(restTemplate
                .getForEntity(anyString(), any()))
                .thenThrow(restClientException);

        Assertions.assertThrows(RestClientException.class, () -> {
            artistService.getCommentsForArtist(id);
        });
    }

    @Test
    void getCommentsForArtist_anOKResponseFromExternalApi_thenReturnOK() throws NotFoundException {
        final String id = "mon-super-id-artist";
        final Artist artist = new Artist(id, "top-artist", "top-genre");
        final Comment comment1 = new Comment("artist1", "user1", "comment by user 1");
        final Comment comment2 = new Comment("artist2", "user2", "comment by user 2");
        final Comment comment3 = new Comment("artist3", "user3", "comment by user 3");
        final Comment comment4 = new Comment("artist4", "user4", "comment by user 4");
        final Comment[] comments = new Comment[]{comment1, comment2, comment3, comment4};
        final ResponseEntity<Comment[]> response =  new ResponseEntity<>(comments, HttpStatus.OK);

        final String url = rootUri
                + "/comments/" + id + "/last10";

        final ArtistAggregate expectedComments = new ArtistAggregate(artist, Arrays.asList(comments));
        when(artistRepository.findById(id))
                .thenReturn(Optional.of(artist));

        when(restTemplate
                .getForEntity(url, Comment[].class))
                .thenReturn(response);

        final ArtistAggregate actualComments = artistService.getCommentsForArtist(id);

        Assertions.assertEquals(expectedComments, actualComments);
    }
}