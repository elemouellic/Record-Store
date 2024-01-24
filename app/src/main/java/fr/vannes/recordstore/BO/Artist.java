package fr.vannes.recordstore.BO;


/**
 * This class represents an artist of a music collection.
 * It contains the following information:
 * - id: the unique identifier of the artist supplied by MusicBrainz API
 * - name: the name of the artist
 */
public class Artist {

    private String id;
    private String name;

    public Artist() {
    }

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
