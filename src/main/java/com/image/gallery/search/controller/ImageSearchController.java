package com.image.gallery.search.controller;

import com.image.gallery.search.domain.model.PictureDetails;
import com.image.gallery.search.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImageSearchController {

    @Autowired
    private ImageService imageService;

    @GetMapping(value ="/search/{searchTerm}")
    public ResponseEntity<List<PictureDetails>> getAllBySearchTerm(@PathVariable String searchTerm) {
        return new ResponseEntity<>(imageService.getAllBySearchTerm(searchTerm), HttpStatus.OK);
    }
}
