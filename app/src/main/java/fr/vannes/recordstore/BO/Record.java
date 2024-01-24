package fr.vannes.recordstore.BO;

/**
 * This class represents a record of a music collection.
 * It contains the following information:
 * - id: the unique identifier of the record supplied by MusicBrainz API
 * - title: the title of the record
 * - pictureURL: the URL of the picture of the record
 * - type: the type of the record (album, single, EP, ...)
 * - artist: the artist of the record
 */
public class Record {

    private String id;
    private String title;
    private String pictureURL;
    private String type;

    private Artist artist;


    public Record() {
    }

    public Record(String id, String title, String pictureURL, String type, Artist artist) {
        this.id = id;
        this.title = title;
        this.pictureURL = pictureURL;
        this.type = type;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
