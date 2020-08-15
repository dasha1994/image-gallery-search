package com.image.gallery.search.service;

import com.image.gallery.search.domain.dto.PicturesDto;
import com.image.gallery.search.domain.model.Picture;
import com.image.gallery.search.domain.model.PictureDetails;
import com.image.gallery.search.exception.InvalidAuthTokenException;
import com.image.gallery.search.repository.PictureDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PictureDetailsRepository pictureDetailsRepository;

    @Value("${interview.agile.engine.url}")
    private String baseUrl;

    private final static int MAX_ATTEMPTS = 3;
    private final static String IMAGES_PATH = "/images";

    private RestTemplate restTemplate = new RestTemplate();
    private int attempts = 0;
    private String token;

    @PostConstruct
    public void init() throws InvalidAuthTokenException {
        token = authorizationService.renewToken();
        loadPictures();
    }

    @Scheduled(cron = "${reload.period}")
    private void loadPictures() throws InvalidAuthTokenException {
        int page = 1;
        boolean hasMore = true;
        String url = baseUrl + IMAGES_PATH;

        List<Picture> pictures = new ArrayList<>();
        ResponseEntity<PicturesDto> response = null;
        HttpEntity request = composeGetPicsEntity();

        try {
            while (hasMore) {
                response = this.restTemplate.exchange(url + "?page=" + page, HttpMethod.GET, request, PicturesDto.class);

                if (response.getBody() != null) {
                    PicturesDto picturesDto = response.getBody();
                    pictures.addAll(picturesDto.getPictures());
                    hasMore = picturesDto.isHasMore();
                    page++;
                }
            }
        } catch (HttpClientErrorException exception) {
            logger.warn("Current auth token is not valid. Trying to renew");

            attempts++;
            authorizationService.renewToken();
            if (attempts < MAX_ATTEMPTS) loadPictures();
            else {
                logger.error("Unable to authorize");
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            }
        }

        logger.info("All pictures successfully retrieved");

        cachePictureDetails(pictures);
    }

    private void cachePictureDetails(List<Picture> pictures) {
        String url = baseUrl + IMAGES_PATH;
        HttpEntity request = composeGetPicsEntity();
        ResponseEntity<PictureDetails> response = null;

        List<PictureDetails> pictureDetailsList = new ArrayList<>();
        for (Picture picture : pictures) {
            response = this.restTemplate.exchange(url + "/" + picture.getId(), HttpMethod.GET, request, PictureDetails.class);
            if (response.getBody() != null) pictureDetailsList.add(response.getBody());
        }

        pictureDetailsRepository.saveAll(pictureDetailsList);

        logger.info("All picture details successfully cached");
    }

    public List<PictureDetails> getAllBySearchTerm(String searchTerm) {
        return pictureDetailsRepository.findAll().stream()
                .filter(pictureDetails -> pictureDetails.containsSearchTerm(searchTerm))
                .collect(Collectors.toList());
    }

    private HttpEntity composeGetPicsEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity(headers);
    }
}
