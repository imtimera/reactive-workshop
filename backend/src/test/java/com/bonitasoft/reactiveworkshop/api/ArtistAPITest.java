package com.bonitasoft.reactiveworkshop.api;

import com.bonitasoft.reactiveworkshop.ReactiveWorkshopApplication;
import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.ArtistAggregate;
import com.bonitasoft.reactiveworkshop.domain.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.service.ArtistService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultPageIdx;
import static com.bonitasoft.reactiveworkshop.util.ApiConstant.defaultSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReactiveWorkshopApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtistAPITest {
    @InjectMocks
    private ArtistAPI artistAPI;

    @Mock
    private ArtistService artistService;


    @Test
    void findById_InvalidArtistId_NotFoundExceptionWillThrown() throws NotFoundException {
        String badId = "my-bad-id";
        given(artistService.getArtistById(badId))
                .willAnswer(invocation -> {
                    throw new NotFoundException("Cannot find artist with id : " + badId);
                });
        Assertions.assertThrows(NotFoundException.class, () -> {
            artistAPI.findById(badId);
        });
    }

    @Test
    void findById_ValidArtistId_WillReturnArtistWithId() throws NotFoundException {
        String id = "my-artist-id";
        Artist expectedArtist = new Artist(id, "uper-artist", "genre1");
        when(artistService.getArtistById(id))
                .thenReturn(expectedArtist);
        final Artist actualArtist = artistAPI.findById(id);

        Assertions.assertEquals(expectedArtist, actualArtist);
    }

    @Test
    void findAll() {
        int pageIdx = Integer.valueOf(defaultPageIdx);
        int size = Integer.valueOf(defaultSize);
        final Artist artist1 = new Artist("id1", "artist1", "genre1");
        final Artist artist2 = new Artist("id2", "artist2", "genre2");
        final Artist artist3 = new Artist("id3", "artist3", "genre1");
        final Artist artist4 = new Artist("id4", "artist4", "genre2");
        List<Artist> expectedArtists = Arrays.asList(artist1, artist2, artist3, artist4);

        Page<Artist> page = Mockito.mock(Page.class);
        when(artistService.getAllArtists(pageIdx, size))
                .thenReturn(page);
        when(page.getContent()).thenReturn(expectedArtists);

        final Page<Artist> actual = artistAPI.findAll(pageIdx, size);
        Assertions.assertEquals(expectedArtists, actual.getContent());
    }

    @Test
    void commentsForArtist_notFoundId_NotFoundExceptionWillBeThrown() throws NotFoundException {
        String badId = "my-bad-id";
        given(artistService.getCommentsForArtist(badId))
                .willAnswer(invocation -> {
                    throw new NotFoundException("Cannot find artist with id : " + badId);
                });
        Assertions.assertThrows(NotFoundException.class, () -> {
            artistAPI.commentsForArtist(badId);
        });
    }

    @Test
    void commentsForArtist_KoExternalApi_RestClientException() throws NotFoundException {
        String goodId = "my-good-id";
        given(artistService.getCommentsForArtist(goodId))
                .willAnswer(invocation -> {
                    throw new RestClientException("External Api is down...");
                });
        Assertions.assertThrows(RestClientException.class, () -> {
            artistAPI.commentsForArtist(goodId);
        });
    }

    @Test
    void commentsForArtist_commentsForArtist() throws NotFoundException {
        String goodId = "my-good-id";
        final Artist artist = new Artist("id1", "artist1", "genre1");
        List<Comment> comments = new ArrayList<>();
        ArtistAggregate artistAggregate = new ArtistAggregate(artist, comments);
        when(artistService.getCommentsForArtist(goodId))
                .thenReturn(artistAggregate);
        final ArtistAggregate actual = artistAPI.commentsForArtist(goodId);

        Assertions.assertEquals(artistAggregate, actual);
    }
}