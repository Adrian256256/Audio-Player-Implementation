package main.database;

import java.util.ArrayList;

public final class PlayList {
    private String username;
    private ArrayList<PlayListsByUser> playlists;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public ArrayList<PlayListsByUser> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(final ArrayList<PlayListsByUser> playlists) {
        this.playlists = playlists;
    }
}
