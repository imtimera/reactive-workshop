package com.bonitasoft.reactiveworkshop.service;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.ArtistAggregate;
import com.bonitasoft.reactiveworkshop.domain.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.bonitasoft.reactiveworkshop.util.ApiConstant.rootUri;

@Service
public class ArtistService {

    private ArtistRepository artistRepository;
    private RestTemplate restTemplate;

    public ArtistService(ArtistRepository artistRepository, RestTemplate restTemplate) {
        this.artistRepository = artistRepository;
        this.restTemplate = restTemplate;
    }

    public Artist getArtistById(String id) throws NotFoundException {
        return artistRepository.findById(id).orElseThrow(()->
                new NotFoundException("Cannot find Artist with id : "+ id));
    }

    public Page<Artist> getAllArtists(int pageIndex,
                                      int size) {
        Pageable pageable = PageRequest.of(pageIndex, size);

        return artistRepository.findAll(pageable);
    }

    public ArtistAggregate getCommentsForArtist(String artistId) throws NotFoundException,
             RestClientException {
        String url = rootUri
                + "/comments/" + artistId + "/last10";
        try{
            Artist artist = getArtistById(artistId);
            ResponseEntity<Comment[]> responseEntity = restTemplate
                    .getForEntity(url, Comment[].class);

            List<Comment> comments = Arrays.asList(responseEntity.getBody());
            return new ArtistAggregate(artist, comments);
        }catch ( RestClientException | NotFoundException exception){
            throw exception;
        }
    }
}
