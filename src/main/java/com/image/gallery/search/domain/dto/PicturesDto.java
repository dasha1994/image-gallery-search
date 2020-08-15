package com.image.gallery.search.domain.dto;

import com.image.gallery.search.domain.model.Picture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PicturesDto {
    private int page;
    private int pageCount;
    private boolean hasMore;
    private List<Picture> pictures;
}