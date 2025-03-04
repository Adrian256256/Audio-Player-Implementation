package main.database;

import java.util.ArrayList;

public final class PlayListsByUser {
    private boolean visibility;
    private ArrayList<String> songs = new ArrayList<>();
    private ArrayList<Integer> songTimestamps = new ArrayList<>();
    private ArrayList<String> followers = new ArrayList<>();
    private String name;
    private int creationTimestamp;

    public int getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(final int creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public ArrayList<Integer> getSongTimestamps() {
        return songTimestamps;
    }

    public void setSongTimestamps(final ArrayList<Integer> songTimestamps) {
        this.songTimestamps = songTimestamps;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    public ArrayList<String> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<String> songs) {
        this.songs = songs;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(final ArrayList<String> followers) {
        this.followers = followers;
    }
}
