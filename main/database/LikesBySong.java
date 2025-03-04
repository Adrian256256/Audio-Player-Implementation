package main.database;

import java.util.ArrayList;

public final class LikesBySong {
    private String songName;
    private ArrayList<String> usersThatLiked = new ArrayList<>();
    private String songAlbum;

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(final String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(final String songName) {
        this.songName = songName;
    }

    public ArrayList<String> getUsersThatLiked() {
        return usersThatLiked;
    }

    public void setUsersThatLiked(final ArrayList<String> usersThatLiked) {
        this.usersThatLiked = usersThatLiked;
    }
}
