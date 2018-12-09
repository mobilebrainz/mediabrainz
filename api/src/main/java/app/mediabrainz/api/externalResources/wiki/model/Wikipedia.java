package app.mediabrainz.api.externalResources.wiki.model;

import com.squareup.moshi.Json;


public class Wikipedia {

    @Json(name = "mobileview")
    private Mobileview mobileview;

    public Wikipedia() {
    }

    public Mobileview getMobileview() {
        return mobileview;
    }

    public void setMobileview(Mobileview mobileview) {
        this.mobileview = mobileview;
    }
}
