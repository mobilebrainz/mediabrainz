package app.mediabrainz.api.coverart;

import com.squareup.moshi.Json;

import java.util.List;


public class ReleaseCoverArt {

    @Json(name = "release")
    private String release;

    @Json(name = "images")
    private List<CoverArtImage> images;

    public ReleaseCoverArt() {
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public List<CoverArtImage> getImages() {
        return images;
    }

    public void setImages(List<CoverArtImage> images) {
        this.images = images;
    }

    public CoverArtImage getFront() {
        if (images == null) {
            return null;
        }
        for (CoverArtImage image : images) {
            if (image.getFront()) {
                return image;
            }
        }
        return null;
    }

    public CoverArtImage.Thumbnails getFrontThumbnails() {
        CoverArtImage front = getFront();
        return front != null ? front.getThumbnails() : null;
    }

    public CoverArtImage getBack() {
        if (images == null) {
            return null;
        }
        for (CoverArtImage image : images) {
            if (image.getBack()) {
                return image;
            }
        }
        return null;
    }
}
