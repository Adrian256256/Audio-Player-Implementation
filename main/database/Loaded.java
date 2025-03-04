package main.database;

public final class Loaded {
    private String user;
    private String entityLoaded;
    private String typeOfEntity;
    private boolean isLoaded;
    private int songTimestamp;
    private String firstSong;

    public String getFirstSong() {
        return firstSong;
    }

    public void setFirstSong(final String firstSong) {
        this.firstSong = firstSong;
    }

    public int getSongTimestamp() {
        return songTimestamp;
    }

    public void setSongTimestamp(final int songTimestamp) {
        this.songTimestamp = songTimestamp;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(final boolean loaded) {
        this.isLoaded = loaded;
    }

    public String getTypeOfEntity() {
        return typeOfEntity;
    }

    public void setTypeOfEntity(final String typeOfEntity) {
        this.typeOfEntity = typeOfEntity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getEntityLoaded() {
        return entityLoaded;
    }

    public void setEntityLoaded(final String entityLoaded) {
        this.entityLoaded = entityLoaded;
    }
}
