package main.database;

import fileio.input.SongInput;

import java.util.ArrayList;

public final class ArtistPage implements PageInterface {
    private String name;
    private ArrayList<Album> albums = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Merch> merch = new ArrayList<>();

    public ArrayList<Merch> getMerch() {
        return merch;
    }

    /**
     *
     * @param merch lista de produse
     */
    public void setMerch(final ArrayList<Merch> merch) {
        this.merch = merch;
    }

    /**
     *
     * @return
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     *
     * @param events lista de evenimente
     */
    public void setEvents(final ArrayList<Event> events) {
        this.events = events;
    }

    /**
     *
     * @param songs lista de melodii
     * @param albumName numele albumului
     * @param description descrierea albumului
     */
    public void addAlbum(final ArrayList<SongInput> songs, final String albumName,
                         final String description) {
        Album album = new Album();
        album.setName(albumName);
        album.setSongs(songs);
        album.setDescription(description);
        this.albums.add(album);
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
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     *
     * @param albums lista de albume
     */
    public void setAlbums(final ArrayList<Album> albums) {
        this.albums = albums;
    }
}
