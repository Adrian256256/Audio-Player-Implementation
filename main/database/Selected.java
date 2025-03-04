package main.database;

public final class Selected {
    private String user;
    private String entity;
    private String typeOfEntity;
    private int timestamp;
    private boolean isSelected;
    private String firstSong;

    public String getFirstSong() {
        return firstSong;
    }

    public void setFirstSong(final String firstSong) {
        this.firstSong = firstSong;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(final boolean selected) {
        this.isSelected = selected;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(final String entity) {
        this.entity = entity;
    }
}
