package main.database;

import java.util.ArrayList;

public final class SearchedSongsByUsers {
    private ArrayList songs = new ArrayList<>();
    private ArrayList podcasts = new ArrayList<>();
    private ArrayList playlists = new ArrayList<>();
    private ArrayList<String> artists = new ArrayList<>();
    private ArrayList albums = new ArrayList<>();
    private ArrayList<String> hosts = new ArrayList<>();
    private String user;
    private String searchType;
    private ArrayList<Integer> songTimestamp;
    private ArrayList<String> frstSng = new ArrayList<>();
    private boolean isSearched;

    public ArrayList<String> getFrstSng() {
        return frstSng;
    }

    public void setFrstSng(final ArrayList<String> frstSng) {
        this.frstSng = frstSng;
    }

    public ArrayList<String> getHosts() {
        return hosts;
    }

    public void setHosts(final ArrayList<String> hosts) {
        this.hosts = hosts;
    }

    public ArrayList getAlbums() {
        return albums;
    }

    public void setAlbums(final ArrayList albums) {
        this.albums = albums;
    }

    public ArrayList<String> getArtists() {
        return artists;
    }

    public void setArtists(final ArrayList<String> artists) {
        this.artists = artists;
    }

    public boolean isSearched() {
        return isSearched;
    }

    public void setSearched(final boolean searched) {
        this.isSearched = searched;
    }

    public ArrayList<Integer> getSongTimestamp() {
        return songTimestamp;
    }

    public void setSongTimestamp(final ArrayList<Integer> songTimestamp) {
        this.songTimestamp = songTimestamp;
    }

    public ArrayList getPlaylists() {
        return playlists;
    }

    public void setPlaylists(final ArrayList playlists) {
        this.playlists = playlists;
    }

    public ArrayList getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList songs) {
        this.songs = songs;
    }

    public ArrayList getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final ArrayList podcasts) {
        this.podcasts = podcasts;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(final String searchType) {
        this.searchType = searchType;
    }
}
