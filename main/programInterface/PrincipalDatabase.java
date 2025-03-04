package main.programInterface;

import main.database.*;

import java.util.ArrayList;

public final class PrincipalDatabase {
    // lista cu tot ce este incarcat de utilizatori
    private ArrayList<Loaded> loaded = new ArrayList<Loaded>();
    // lista cu utilizatorii si melodiile pe care acestia le-au apreciat
    private ArrayList<LikesByUsername> likesByUsernames = new ArrayList<>();
    // lista cu melodiile si utilizatorii care le-au apreciat
    private ArrayList<LikesBySong> likesBySongs = new ArrayList<>();
    // lista cu melodiile cautate de utilizator "searchData"
    private final ArrayList<SearchedSongsByUsers> searchData = new ArrayList<>();
    // lista cu melodiile selectate de utilizator "selected"
    private ArrayList<Selected> selected = new ArrayList<>();
    // lista cu statusurile utilizatorilor "statuses"
    private final ArrayList<Status> statuses = new ArrayList<>();
    // lista cu playlisturile utilizatorilor "playlistsPrincipal"
    private final ArrayList<PlayList> playlistsPrincipal = new ArrayList<>();
    // lista cu utilizatorii "users"
    private final UserManagement users = new UserManagement();
    // lista cu paginile "pages"
    private PageManagementHub pages = new PageManagementHub();

    public ArrayList<PlayList> getPlaylistsPrincipal() {
        return playlistsPrincipal;
    }

    public ArrayList<Selected> getSelected() {
        return selected;
    }

    public void setSelected(final ArrayList<Selected> selected) {
        this.selected = selected;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public UserManagement getUsers() {
        return users;
    }

    public PageManagementHub getPages() {
        return pages;
    }

    public void setPages(final PageManagementHub pages) {
        this.pages = pages;
    }

    public ArrayList<SearchedSongsByUsers> getSearchData() {
        return searchData;
    }

    /**
     * metoda care returneaza lista cu tot ce este incarcat
     *
     * @return
     */
    public ArrayList<Loaded> getLoaded() {
        return loaded;
    }

    /**
     * metoda ce seteaza lista cu tot ce este incarcat
     *
     * @param loaded
     */
    public void setLoaded(final ArrayList<Loaded> loaded) {
        this.loaded = loaded;
    }

    /**
     * metoda care returneaza lista cu utilizatorii si melodiile pe care acestia le-au apreciat
     *
     * @return
     */
    public ArrayList<LikesByUsername> getLikesByUsernames() {
        return likesByUsernames;
    }

    /**
     * metoda care returneaza lista cu melodiile si utilizatorii care le-au apreciat
     *
     * @return
     */
    public ArrayList<LikesBySong> getLikesBySongs() {
        return likesBySongs;
    }

    /**
     * metoda ce seteaza lista cu melodiile si utilizatorii care le-au apreciat
     *
     * @param likesBySongs
     */
    public void setLikesBySongs(final ArrayList<LikesBySong> likesBySongs) {
        this.likesBySongs = likesBySongs;
    }

}
