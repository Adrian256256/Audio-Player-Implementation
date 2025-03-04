package main.database;

import java.util.List;

public final class Filter {
    private final String name = null;
    private final String album = null;
    private final List<String> tags = null;
    private final String lyrics = null;
    private final String genre = null;
    private final String releaseYear = null;
    private final String artist = null;
    private final String owner = null;
    // Getter and setter methods
    public Filter() {

    }
    public List<String> getTags() {
        return tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public String getOwner() {
        return owner;
    }

    public String getAlbum() {
        return album;
    }

    public String getName() {
        return name;
    }

}
