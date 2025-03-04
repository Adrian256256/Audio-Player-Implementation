package main.database;

import fileio.input.SongInput;

import java.util.ArrayList;

public final class Album {
    private String name;
    private ArrayList<SongInput> songs = new ArrayList<>();
    private String description;

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description descrierea albumului
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name numele albumului
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    /**
     *
     * @param songs lista de melodii
     */
    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }
}
