package app.mediabrainz.api.model;

import com.squareup.moshi.Json;

import java.util.List;

import app.mediabrainz.api.model.relations.Relation;


public abstract class BaseLookupEntity {

    @Json(name = "id")
    protected String id;

    //inc=...-rels
    @Json(name = "relations")
    protected List<Relation> relations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseLookupEntity that = (BaseLookupEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
