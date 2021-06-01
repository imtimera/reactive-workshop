package com.bonitasoft.reactiveworkshop.repository;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {//extends MongoRepository<Artist, String> {

    List<Artist> findByGenre(String genre);

}
