package main.database;

public final class PodcastsWatched {
    private String podcastName;
    private String lastEpisode;
    private int lastTimestamp;

    public String getPodcastName() {
        return podcastName;
    }

    public void setPodcastName(final String podcastName) {
        this.podcastName = podcastName;
    }

    public String getLastEpisode() {
        return lastEpisode;
    }

    public void setLastEpisode(final String lastEpisode) {
        this.lastEpisode = lastEpisode;
    }

    public int getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(final int lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}
