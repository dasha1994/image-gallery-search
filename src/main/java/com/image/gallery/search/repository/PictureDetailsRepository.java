package com.image.gallery.search.repository;

import com.image.gallery.search.domain.model.PictureDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureDetailsRepository extends JpaRepository<PictureDetails, String> {

}
