package main.database;

import java.util.ArrayList;

public final class LikesByUsername {
    private String username;
    private ArrayList<String> likedSongs = new ArrayList<>();
    private ArrayList<String> likedSongsAlbum = new ArrayList<>();

    public ArrayList<String> getLikedSongsAlbum() {
        return likedSongsAlbum;
    }

    public void setLikedSongsAlbum(final ArrayList<String> likedSongsAlbum) {
        this.likedSongsAlbum = likedSongsAlbum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public ArrayList<String> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(final ArrayList<String> likedSongs) {
        this.likedSongs = likedSongs;
    }
}
