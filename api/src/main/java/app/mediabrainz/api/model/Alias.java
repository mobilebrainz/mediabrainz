package app.mediabrainz.api.model;

import com.squareup.moshi.Json;


public class Alias {

    @Json(name = "name")
    private String name = "";

    @Json(name = "sort-name")
    private String sortName = "";

    @Json(name = "locale")
    private String locale = "";

    @Json(name = "type")
    private String type = "";

    @Json(name = "type-id")
    private String typeId = "";

    @Json(name = "primary")
    private Boolean primary;

    @Json(name = "begin-date")
    private String beginDate = "";

    @Json(name = "end-date")
    private String endDate = "";

    public Alias() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
