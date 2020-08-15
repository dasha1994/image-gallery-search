package com.image.gallery.search.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="pictures")
public class PictureDetails {
    @Id
    private String id;
    private String author;
    private String camera;
    private String tags;
    @JsonProperty("cropped_picture")
    private String cropped_picture;
    @JsonProperty("full_picture")
    private String full_picture;

    public boolean containsSearchTerm(String searchTerm) {
        return StringUtils.contains(author, searchTerm) ||
                StringUtils.contains(camera, searchTerm) ||
                StringUtils.contains(tags, searchTerm);
    }
}
