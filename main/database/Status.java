package main.database;

import java.util.ArrayList;

public final class Status {
    private String user;
    private Stats stats;
    private int timestamp;
    private String typeOfListening;
    //podcast, song sau playlist
    private String currentEpisode;
    private String currentPlaylist;
    private String currentAlbum;
    private boolean isEmpty;
    private int songDuration;
    private int seed;

    public String getCurrentAlbum() {
        return currentAlbum;
    }

    public void setCurrentAlbum(final String currentAlbum) {
        this.currentAlbum = currentAlbum;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(final int seed) {
        this.seed = seed;
    }

    public int getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(final int songDuration) {
        this.songDuration = songDuration;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(final boolean empty) {
        this.isEmpty = empty;
    }

    public String getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(final String currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    private ArrayList<PodcastsWatched> podcastsWatcheds = new ArrayList<>();

    public String getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(final String currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public ArrayList<PodcastsWatched> getPodcastsWatcheds() {
        return podcastsWatcheds;
    }

    public void setPodcastsWatcheds(final ArrayList<PodcastsWatched> podcastsWatcheds) {
        this.podcastsWatcheds = podcastsWatcheds;
    }

    public String getTypeOfListening() {
        return typeOfListening;
    }

    public void setTypeOfListening(final String typeOfListening) {
        this.typeOfListening = typeOfListening;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(final Stats stats) {
        this.stats = stats;
    }
}
