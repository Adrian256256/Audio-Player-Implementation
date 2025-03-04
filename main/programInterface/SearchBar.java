package main.programInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import main.database.Command;
import main.database.PlayList;
import main.database.Selected;
import main.database.SearchedSongsByUsers;
import main.database.Filter;

import java.util.ArrayList;

public final class SearchBar {
    private static final int MAX = 5;
    private static final int ZERO = 0;
    private static SearchBar instance = null;

    public SearchBar() {
    }

    /**
     * metoda pentru a creea un obiect de tip SearchBar
     *
     * @return obiectul de tip SearchBar
     */
    public static SearchBar getInstance() {
        if (instance == null) {
            synchronized (SearchBar.class) {
                if (instance == null) {
                    instance = new SearchBar();
                }
            }
        }
        return instance;
    }

    /**
     * metoda care se ocupa cu selectia unei pagini de artist sau de host, daca este cazul
     * metoda auxiliara pentru metoda "select"
     * @param command   comanda actuala
     * @param out       output ul comenzii
     * @return
     */
    public void selectPage(final Command command, final ArrayList<SearchedSongsByUsers> searched,
                           final ObjectNode out, final ArrayList<Selected> selecteds,
                           final int i) {
        if (searched.get(i).getSearchType().equals("artist")) {
            if (command.getItemNumber() <= searched.get(i).getArtists().size()) {
                ArrayList artists = searched.get(i).getArtists();
                Object artist = artists.get(command.getItemNumber() - 1);
                String message = "Successfully selected " + artist + "'s page.";
                out.put("message", message);
                boolean okSelect = false;
                for (int j = 0; j < selecteds.size(); j++) {
                    if (selecteds.get(j).getUser().equals(command.getUsername())) {
                        selecteds.get(j).setSelected(true);
                        selecteds.get(j).setEntity((String) artist);
                        selecteds.get(j).setTypeOfEntity("artist");
                        okSelect = true;
                        break;
                    }
                }
                if (!okSelect) {
                    Selected select = new Selected();
                    select.setSelected(true);
                    select.setUser(command.getUsername());
                    select.setEntity((String) artist);
                    select.setTypeOfEntity("artist");
                    selecteds.add(select);
                }
                return;
            } else {
                String message = "The selected ID is too high.";
                out.put("message", message);
            }
        }
        if (searched.get(i).getSearchType().equals("host")) {
            if (command.getItemNumber() <= searched.get(i).getHosts().size()) {
                ArrayList<String> hosts = searched.get(i).getHosts();
                String host = hosts.get(command.getItemNumber() - 1);
                String message = "Successfully selected " + host + "'s page.";
                out.put("message", message);
                boolean okSelect = false;
                for (int j = 0; j < selecteds.size(); j++) {
                    if (selecteds.get(j).getUser().equals(command.getUsername())) {
                        selecteds.get(j).setSelected(true);
                        selecteds.get(j).setEntity(host);
                        selecteds.get(j).setTypeOfEntity("host");
                        okSelect = true;
                        break;
                    }
                }
                if (!okSelect) {
                    Selected select = new Selected();
                    select.setSelected(true);
                    select.setUser(command.getUsername());
                    select.setEntity(host);
                    select.setTypeOfEntity("host");
                    selecteds.add(select);
                }
            } else {
                String message = "The selected ID is too high.";
                out.put("message", message);
            }
        }
    }

    /**
     * metoda care selecteaza o entitate cautata anterior
     *
     * @param command   comanda actuala
     * @param searched  lista cu cautarile utilizatorilor
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param selecteds lista cu selectiile utilizatorilor
     * @return
     */
    public ObjectNode select(final Command command, final ArrayList<SearchedSongsByUsers> searched,
                             final ObjectNode out, final ArrayList<Selected> selecteds) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        boolean ok = false;
        for (int i = 0; i < searched.size(); i++) {
            if (searched.get(i).getUser().equals(command.getUsername())) {
                if (!searched.get(i).isSearched()) {
                    out.put("message", "Please conduct a search before making a selection.");
                    return out;
                }
                searched.get(i).setSearched(false);
                ok = true;
                if (searched.get(i).getSearchType().equals("playlist")) {
                    if (command.getItemNumber() <= searched.get(i).getPlaylists().size()) {
                        ArrayList playlists = searched.get(i).getPlaylists();
                        Object playlist = playlists.get(command.getItemNumber() - 1);
                        String message = "Successfully selected " + playlist + ".";
                        out.put("message", message);
                        boolean okSelect = false;
                        for (int j = 0; j < selecteds.size(); j++) {
                            if (selecteds.get(j).getUser().equals(command.getUsername())) {
                                selecteds.get(j).setSelected(true);
                                selecteds.get(j).setEntity((String) playlist);
                                selecteds.get(j).setTypeOfEntity("playlist");
                                okSelect = true;
                                break;
                            }
                        }
                        if (!okSelect) {
                            Selected select = new Selected();
                            select.setSelected(true);
                            select.setUser(command.getUsername());
                            select.setEntity((String) playlist);
                            select.setTypeOfEntity("playlist");
                            selecteds.add(select);
                        }
                        break;
                    } else {
                        String message = "The selected ID is too high.";
                        out.put("message", message);
                    }
                }
                if (searched.get(i).getSearchType().equals("podcast")) {
                    if (command.getItemNumber() <= searched.get(i).getPodcasts().size()) {
                        ArrayList podcasts = searched.get(i).getPodcasts();
                        Object podcast = podcasts.get(command.getItemNumber() - 1);
                        String message = "Successfully selected " + podcast + ".";
                        out.put("message", message);
                        boolean okSelect = false;
                        for (int j = 0; j < selecteds.size(); j++) {
                            if (selecteds.get(j).getUser().equals(command.getUsername())) {
                                selecteds.get(j).setSelected(true);
                                selecteds.get(j).setEntity((String) podcast);
                                selecteds.get(j).setTypeOfEntity("podcast");
                                okSelect = true;
                                break;
                            }
                        }
                        if (!okSelect) {
                            Selected select = new Selected();
                            select.setSelected(true);
                            select.setUser(command.getUsername());
                            select.setEntity((String) podcast);
                            select.setTypeOfEntity("podcast");
                            selecteds.add(select);
                        }
                        break;
                    } else {
                        String message = "The selected ID is too high.";
                        out.put("message", message);
                    }
                }
                if (searched.get(i).getSearchType().equals("song")) {
                    if (command.getItemNumber() <= searched.get(i).getSongs().size()) {
                        ArrayList songs = searched.get(i).getSongs();
                        ArrayList<Integer> timestamps = searched.get(i).getSongTimestamp();
                        Object song = songs.get(command.getItemNumber() - 1);
                        int timestamp = timestamps.get(command.getItemNumber() - 1);
                        String message = "Successfully selected " + song + ".";
                        out.put("message", message);
                        boolean okSelect = false;
                        for (int j = 0; j < selecteds.size(); j++) {
                            if (selecteds.get(j).getUser().equals(command.getUsername())) {
                                selecteds.get(j).setSelected(true);
                                selecteds.get(j).setEntity((String) song);
                                selecteds.get(j).setTypeOfEntity("song");
                                selecteds.get(j).setTimestamp(timestamp);
                                okSelect = true;
                                break;
                            }
                        }
                        if (!okSelect) {
                            Selected select = new Selected();
                            select.setSelected(true);
                            select.setUser(command.getUsername());
                            select.setEntity((String) song);
                            select.setTypeOfEntity("song");
                            select.setTimestamp(timestamp);
                            selecteds.add(select);
                        }
                        break;
                    } else {
                        String message = "The selected ID is too high.";
                        out.put("message", message);
                    }
                }
                selectPage(command, searched, out, selecteds, i);
                if (searched.get(i).getSearchType().equals("album")) {
                    if (command.getItemNumber() <= searched.get(i).getAlbums().size()) {
                        ArrayList albums = searched.get(i).getAlbums();
                        ArrayList<String> firstSongs = searched.get(i).getFrstSng();
                        String firstSong = firstSongs.get(command.getItemNumber() - 1);
                        Object album = albums.get(command.getItemNumber() - 1);
                        String message = "Successfully selected " + album + ".";
                        out.put("message", message);
                        boolean okSelect = false;
                        for (int j = 0; j < selecteds.size(); j++) {
                            if (selecteds.get(j).getUser().equals(command.getUsername())) {
                                selecteds.get(j).setSelected(true);
                                selecteds.get(j).setEntity((String) album);
                                selecteds.get(j).setTypeOfEntity("album");
                                selecteds.get(j).setFirstSong(firstSong);
                                okSelect = true;
                                break;
                            }
                        }
                        if (!okSelect) {
                            Selected select = new Selected();
                            select.setSelected(true);
                            select.setUser(command.getUsername());
                            select.setEntity((String) album);
                            select.setTypeOfEntity("album");
                            selecteds.add(select);
                        }
                        break;
                    } else {
                        String message = "The selected ID is too high.";
                        out.put("message", message);
                    }
                }
            }
        }
        if (!ok) {
            String message = "Please conduct a search before making a selection.";
            out.put("message", message);
        }
        return out;
    }

    /**
     * metoda auxiliara ce cauta melodii
     *
     * @param verifyIn         acest parametru verifica daca s-a mai gasit vreun rezultat in cadrul
     *                         acestei cautari
     * @param filter           filtrele cautarii
     * @param searched         lista cu cautarile utilizatorilor
     * @param songs            lista cu melodii
     * @param resultsCounterIn contorul pentru numarul rezultatelor gasite
     * @return
     */
    public ArrayList searchByName(final boolean verifyIn, final Filter filter,
                                           final ArrayList<SongInput> searched,
                                           final ArrayList<SongInput> songs,
                                           final int resultsCounterIn) {
        int resultsCounter = resultsCounterIn;
        boolean verify = verifyIn;
        if (!verify) {
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getName().startsWith(filter.getName())) {
                    searched.add(songs.get(i));
                    resultsCounter++;
                }
            }
        } else {
            for (int i = 0; i < searched.size(); i++) {
                if (!searched.get(i).getName().startsWith(filter.getName())) {
                    searched.remove(i);
                    resultsCounter--;
                    i--;
                }
            }
        }

        verify = true;
        ArrayList res = new ArrayList<>();
        res.add(resultsCounter);
        res.add(verify);
        return res;
    }

    /**
     * metoda auxiliara ce cauta melodii
     *
     * @param verifyIn         acest parametru verifica daca s-a mai gasit vreun rezultat in cadrul
     *                         acestei cautari
     * @param filter           filtrele cautarii
     * @param searched         lista cu cautarile utilizatorilor
     * @param songs            lista cu melodii
     * @param resultsCounterIn contorul pentru numarul rezultatelor gasite
     * @return
     */
    public ArrayList searchByAlbum(final boolean verifyIn, final Filter filter,
                                            final ArrayList<SongInput> searched,
                                            final ArrayList<SongInput> songs,
                                            final int resultsCounterIn) {
        int resultsCounter = resultsCounterIn;
        boolean verify = verifyIn;
        if (!verify) {
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getAlbum().equals(filter.getAlbum())) {
                    searched.add(songs.get(i));
                    resultsCounter++;
                }
            }
        } else {
            for (int i = 0; i < songs.size(); i++) {
                if (!searched.get(i).getAlbum().equals(filter.getAlbum())) {
                    searched.remove(i);
                    resultsCounter--;
                    i--;
                }
            }
        }
        verify = true;
        ArrayList res = new ArrayList<>();
        res.add(resultsCounter);
        res.add(verify);
        return res;
    }

    /**
     * metoda auxiliara ce cauta melodii
     *
     * @param verifyIn         acest parametru verifica daca s-a mai gasit vreun rezultat in cadrul
     *                         acestei cautari
     * @param filter           filtrele cautarii
     * @param searched         lista cu cautarile utilizatorilor
     * @param songs            lista cu melodii
     * @param resultsCounterIn contorul pentru numarul rezultatelor gasite
     * @return
     */
    public ArrayList searchByTags(final boolean verifyIn, final Filter filter,
                                           final ArrayList<SongInput> searched,
                                           final ArrayList<SongInput> songs,
                                           final int resultsCounterIn) {
        int resultsCounter = resultsCounterIn;
        boolean verify = verifyIn;
        if (!verify) {
            for (int i = 0; i < songs.size(); i++) {
                int ok = 0;
                //pt fiecare melodie
                for (int j = 0; j < filter.getTags().size(); j++) {
                    //pt fiecare tag din filtre
                    for (int k = 0; k < songs.get(i).getTags().size(); k++) {
                        //pt fiecare tag al melodiei
                        if (filter.getTags().get(j).equals(songs.get(i).getTags().get(k))) {
                            ok++;
                        }
                    }
                }
                if (ok == filter.getTags().size()) {
                    searched.add(songs.get(i));
                    resultsCounter++;
                }
            }
        } else {
            for (int i = 0; i < searched.size(); i++) {
                int ok = 0;
                //pt fiecare melodie
                for (int j = 0; j < filter.getTags().size(); j++) {
                    //pt fiecare tag din filtre
                    for (int k = 0; k < searched.get(i).getTags().size(); k++) {
                        //pt fiecare tag al melodiei
                        if (filter.getTags().get(j).equals(searched.get(i).getTags().get(k))) {
                            ok++;
                        }
                    }
                }
                if (ok != filter.getTags().size()) {
                    searched.remove(i);
                    resultsCounter--;
                    i--;
                }
            }
        }
        verify = true;
        ArrayList res = new ArrayList<>();
        res.add(resultsCounter);
        res.add(verify);
        return res;
    }

    /**
     * metoda auxiliara ce cauta melodii
     *
     * @param verifyIn         acest parametru verifica daca s-a mai gasit vreun rezultat in cadrul
     *                         acestei cautari
     * @param filter           filtrele cautarii
     * @param searched         lista cu cautarile utilizatorilor
     * @param songs            lista cu melodii
     * @param resultsCounterIn contorul pentru numarul rezultatelor gasite
     * @return
     */
    public ArrayList searchByLyrics(final boolean verifyIn, final Filter filter,
                                             final ArrayList<SongInput> searched,
                                             final ArrayList<SongInput> songs,
                                             final int resultsCounterIn) {
        int resultsCounter = resultsCounterIn;
        boolean verify = verifyIn;
        if (!verify) {
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getLyrics().toLowerCase()
                        .contains(filter.getLyrics().toLowerCase())) {
                    searched.add(songs.get(i));
                    resultsCounter++;
                }
            }
        } else {
            for (int i = 0; i < searched.size(); i++) {
                if (!searched.get(i).getLyrics().toLowerCase()
                        .contains(filter.getLyrics().toLowerCase())) {
                    searched.remove(i);
                    resultsCounter--;
                    i--;
                }
            }
        }
        verify = true;
        ArrayList res = new ArrayList<>();
        res.add(resultsCounter);
        res.add(verify);
        return res;
    }

    /**
     * metoda auxiliara ce cauta melodii
     *
     * @param verifyIn         acest parametru verifica daca s-a mai gasit vreun rezultat in cadrul
     *                         acestei cautari
     * @param filter           filtrele cautarii
     * @param searched         lista cu cautarile utilizatorilor
     * @param songs            lista cu melodii
     * @param resultsCounterIn contorul pentru numarul rezultatelor gasite
     * @return
     */
    public ArrayList searchByGenre(final boolean verifyIn, final Filter filter,
                                            final ArrayList<SongInput> searched,
                                            final ArrayList<SongInput> songs,
                                            final int resultsCounterIn) {
        int resultsCounter = resultsCounterIn;
        boolean verify = verifyIn;
        if (!verify) {
            for (int i = 0; i < songs.size(); i++) {
                String filterGenre =
                        filter.getGenre().substring(0, 1).toUpperCase() + filter.getGenre()
                                .substring(1);
                if (songs.get(i).getGenre().equals(filterGenre)) {
                    searched.add(songs.get(i));
                    resultsCounter++;
                }
            }
        } else {
            for (int i = 0; i < searched.size(); i++) {
                String filterGenre =
                        filter.getGenre().substring(0, 1).toUpperCase() + filter.getGenre()
                                .substring(1);
                if (!searched.get(i).getGenre().equals(filterGenre)) {
                    searched.remove(i);
                    resultsCounter--;
                    i--;
                }
            }
        }
        verify = true;
        ArrayList res = new ArrayList<>();
        res.add(resultsCounter);
        res.add(verify);
        return res;
    }

    /**
     * metoda auxiliara ce cauta melodii
     *
     * @param verifyIn         acest parametru verifica daca s-a mai gasit vreun rezultat in cadrul
     *                         acestei cautari
     * @param filter           filtrele cautarii
     * @param searched         lista cu cautarile utilizatorilor
     * @param songs            lista cu melodii
     * @param resultsCounterIn contorul pentru numarul rezultatelor gasite
     * @return
     */
    public ArrayList searchByReleaseYear(final boolean verifyIn, final Filter filter,
                                                  final ArrayList<SongInput> searched,
                                                  final ArrayList<SongInput> songs,
                                                  final int resultsCounterIn) {
        int resultsCounter = resultsCounterIn;
        boolean verify = verifyIn;
        String subString = filter.getReleaseYear().substring(1);
        int year = Integer.parseInt(subString);
        if (!verify) {
            if (filter.getReleaseYear().charAt(0) == '>') {
                for (int i = 0; i < songs.size(); i++) {
                    if (songs.get(i).getReleaseYear() >= year) {
                        searched.add(songs.get(i));
                        resultsCounter++;
                    }
                }
            }
            if (filter.getReleaseYear().charAt(0) == '<') {
                for (int i = 0; i < songs.size(); i++) {
                    if (songs.get(i).getReleaseYear() <= year) {
                        searched.add(songs.get(i));
                        resultsCounter++;
                    }
                }
            }
            verify = true;
        } else {
            for (int i = 0; i < searched.size(); i++) {
                if (filter.getReleaseYear().charAt(0) == '>') {
                    if (searched.get(i).getReleaseYear() <= year) {
                        searched.remove(i);
                        resultsCounter--;
                        i--;
                    }
                } else if (filter.getReleaseYear().charAt(0) == '<') {
                    if (searched.get(i).getReleaseYear() >= year) {
                        searched.remove(i);
                        resultsCounter--;
                        i--;
                    }
                }
            }
        }
        ArrayList res = new ArrayList<>();
        res.add(resultsCounter);
        res.add(verify);
        return res;
    }

    /**
     * metoda auxiliara ce cauta melodii
     *
     * @param verifyIn         acest parametru verifica daca s-a mai gasit vreun rezultat in cadrul
     *                         acestei cautari
     * @param filter           filtrele cautarii
     * @param searched         lista cu cautarile utilizatorilor
     * @param songs            lista cu melodii
     * @param resultsCounterIn contorul pentru numarul rezultatelor gasite
     * @return
     */
    public ArrayList searchByArtist(final boolean verifyIn, final Filter filter,
                                             final ArrayList<SongInput> searched,
                                             final ArrayList<SongInput> songs,
                                             final int resultsCounterIn) {
        int resultsCounter = resultsCounterIn;
        boolean verify = verifyIn;
        if (!verify) {
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getArtist().equals(filter.getArtist())) {
                    searched.add(songs.get(i));
                    resultsCounter++;
                }
            }
        } else {
            for (int i = 0; i < searched.size(); i++) {
                if (!searched.get(i).getArtist().equals(filter.getArtist())) {
                    searched.remove(i);
                    resultsCounter--;
                    i--;
                }
            }
        }
        ArrayList res = new ArrayList<>();
        res.add(resultsCounter);
        res.add(verify);
        return res;
    }

    /**
     * metoda care cauta in biblioteca melodii, apeleaza functiile auxiliare in functie de
     * ce parametri se primesc la filtru
     *
     * @param command   comanda actuala
     * @param out       output ul comenzii
     * @return
     */
    public ArrayList searchHost(final Command command, final PageManagementHub pages,
                                final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode results = objectMapper.createArrayNode();
        int count = ZERO;
        ArrayList<String> hosts = new ArrayList<>();
        for (int i = 0; i < pages.getHostPages().size(); i++) {
            if (pages.getHostPages().get(i).getName().startsWith(command.getFilters().getName())) {
                hosts.add(pages.getHostPages().get(i).getName());
                count++;
            }
        }
        count = Math.min(count, MAX);
        out.put("message", "Search returned " + count + " results");
        for (int i = 0; i < hosts.size() && i < count; i++) {
            results.add(hosts.get(i));
        }
        out.set("results", results);
        return hosts;
    }

    /**
     * metoda care cauta in pages un artist in functie de filtre
     * @return
     */
    public ArrayList searchArtist(final Command command, final PageManagementHub pages,
                                  final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode results = objectMapper.createArrayNode();
        //cautam artistii dupa nume
        int count = ZERO;
        ArrayList<String> artists = new ArrayList<>();
        for (int i = 0; i < pages.getArtistPages().size(); i++) {
            if (pages.getArtistPages().get(i).getName()
                    .startsWith(command.getFilters().getName())) {
                artists.add(pages.getArtistPages().get(i).getName());
                count++;
            }
        }
        count = Math.min(count, MAX);
        out.put("message", "Search returned " + count + " results");
        for (int i = 0; i < artists.size() && i < count; i++) {
            results.add(artists.get(i));
        }
        out.set("results", results);
        return artists;
    }
    /**
     * metoda care cauta in pages un podcast in functie de filtre
     * @return
     */
    public ArrayList searchAlbum(final Command command, final PageManagementHub pages,
                                 final ObjectNode out, final ArrayList<String> frstSng) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode results = objectMapper.createArrayNode();
        int count = ZERO;
        ArrayList<String> albumsSearched = new ArrayList<>();
        boolean verify = false;
        if (command.getFilters().getName() != null) {
            //iteram prin paginile artistilor
            for (int i = 0; i < pages.getArtistPages().size(); i++) {
                //iteram prin albumele fiecarui artist
                for (int j = 0; j < pages.getArtistPages().get(i).getAlbums().size(); j++) {
                    if (pages.getArtistPages().get(i).getAlbums().get(j).getName()
                            .startsWith(command.getFilters().getName())) {
                        albumsSearched.add(
                                pages.getArtistPages().get(i).getAlbums().get(j).getName());
                        frstSng.add(
                                pages.getArtistPages().get(i).getAlbums().get(j).getSongs().get(0)
                                        .getName());
                        count++;
                    }
                }
            }
            verify = true;
        }
        if (command.getFilters().getOwner() != null) {
            if (!verify) {
                //iteram prin paginile artistilor
                //adaugam in albumsSearched totate albumele apartinand artistului "getOwner"
                for (int i = 0; i < pages.getArtistPages().size(); i++) {
                    if (pages.getArtistPages().get(i).getName()
                            .equals(command.getFilters().getOwner())) {
                        for (int j = 0;
                             j < pages.getArtistPages().get(i).getAlbums().size(); j++) {
                            albumsSearched.add(
                                    pages.getArtistPages().get(i).getAlbums().get(j).getName());
                            frstSng.add(pages.getArtistPages().get(i).getAlbums().get(j).getSongs()
                                    .get(0).getName());
                            count++;
                        }
                    }
                }
                verify = true;
            } else {
                //iteram prin albumsSearched si stergem toate albumele care nu apartin artistului
                //"getOwner"
                for (int i = 0; i < albumsSearched.size(); i++) {
                    boolean ok = false;
                    for (int j = 0; j < pages.getArtistPages().size(); j++) {
                        if (pages.getArtistPages().get(j).getName()
                                .equals(command.getFilters().getOwner())) {
                            for (int k = 0;
                                 k < pages.getArtistPages().get(j).getAlbums().size(); k++) {
                                if (albumsSearched.get(i)
                                        .equals(pages.getArtistPages().get(j).getAlbums().get(k)
                                                .getName())) {
                                    ok = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!ok) {
                        albumsSearched.remove(i);
                        frstSng.remove(i);
                        count--;
                        i--;
                    }
                }

            }
        }
        if (command.getDescription() != null) {
            if (!verify) {
                //iteram prin paginile artistilor
                //adaugam in albumsSearched totate albumele care au descrierea "getDescription"
                for (int i = 0; i < pages.getArtistPages().size(); i++) {
                    for (int j = 0; j < pages.getArtistPages().get(i).getAlbums().size(); j++) {
                        if (pages.getArtistPages().get(i).getAlbums().get(j).getDescription()
                                .equals(command.getDescription())) {
                            albumsSearched.add(
                                    pages.getArtistPages().get(i).getAlbums().get(j).getName());
                            frstSng.add(pages.getArtistPages().get(i).getAlbums().get(j).getSongs()
                                    .get(0).getName());
                            count++;
                        }
                    }
                }
            } else {
                //iteram prin albumsSearched si stergem toate albumele care nu au descrierea
                //"getDescription"
                for (int i = 0; i < albumsSearched.size(); i++) {
                    boolean ok = false;
                    for (int j = 0; j < pages.getArtistPages().size(); j++) {
                        for (int k = 0;
                             k < pages.getArtistPages().get(j).getAlbums().size(); k++) {
                            if (albumsSearched.get(i)
                                    .equals(pages.getArtistPages().get(j).getAlbums().get(k)
                                            .getName())) {
                                if (pages.getArtistPages().get(j).getAlbums().get(k)
                                        .getDescription().equals(command.getDescription())) {
                                    ok = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!ok) {
                        albumsSearched.remove(i);
                        frstSng.remove(i);
                        count--;
                        i--;
                    }
                }
            }
        }

        count = Math.min(count, MAX);
        out.put("message", "Search returned " + count + " results");
        for (int i = 0; i < albumsSearched.size() && i < count; i++) {
            results.add(albumsSearched.get(i));
        }
        out.set("results", results);
        return albumsSearched;
    }

    /**
     * metoda care cauta o melodie in functie de filtre
     */
    public ArrayList<PodcastInput> searchSong(final Command command, final LibraryInput library,
                                              final ObjectNode out,
                                              final ArrayList<Integer> timestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        Filter filter = command.getFilters();
        ArrayList<SongInput> songs = library.getSongs();
        //creem output-ul
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ArrayNode results = objectMapper.createArrayNode();
        int resCnt = ZERO;
        //contor pentru numarul rezultatelor in urma cautarii
        boolean verify = false;
        //verificator in legatura cu faptul ca am mai intrat in vreun if
        ArrayList<SongInput> searched = new ArrayList<>();
        if (filter.getName() != null) {
            ArrayList res = searchByName(verify, filter, searched, songs, resCnt);
            resCnt = (int) res.get(0);
            verify = (boolean) res.get(1);
        }
        if (filter.getAlbum() != null) {
            ArrayList res = searchByAlbum(verify, filter, searched, songs, resCnt);
            resCnt = (int) res.get(0);
            verify = (boolean) res.get(1);
        }
        if (filter.getTags() != null) {
            ArrayList res = searchByTags(verify, filter, searched, songs, resCnt);
            resCnt = (int) res.get(0);
            verify = (boolean) res.get(1);
        }
        if (filter.getLyrics() != null) {
            ArrayList res = searchByLyrics(verify, filter, searched, songs, resCnt);
            resCnt = (int) res.get(0);
            verify = (boolean) res.get(1);
        }
        if (filter.getGenre() != null) {
            ArrayList res = searchByGenre(verify, filter, searched, songs, resCnt);
            resCnt = (int) res.get(0);
            verify = (boolean) res.get(1);
        }
        if (filter.getReleaseYear() != null) {
            ArrayList res = searchByReleaseYear(verify, filter, searched, songs, resCnt);
            resCnt = (int) res.get(0);
            verify = (boolean) res.get(1);
        }
        if (filter.getArtist() != null) {
            ArrayList res = searchByArtist(verify, filter, searched, songs, resCnt);
            resCnt = (int) res.get(0);
            verify = (boolean) res.get(1);
        }
        resCnt = Math.min(resCnt, MAX);
        out.put("message", "Search returned " + resCnt + " results");
        for (int i = 0; i < searched.size() && i < resCnt; i++) {
            results.add(searched.get(i).getName());
        }
        out.set("results", results);
        ArrayList returnArr = new ArrayList<>();
        for (int i = 0; i < searched.size() && i < resCnt; i++) {
            returnArr.add(searched.get(i).getName());
            timestamp.add(searched.get(i).getDuration());
        }
        return returnArr;
    }

    /**
     * metoda care cauta in biblioteca playlisturi in functie de parametrii primiti ca filtre
     *
     * @param command   comanda actuala
     * @param playlists lista cu playlisturile utilizatorilor
     * @param out       output ul comenzii
     * @return
     */
    public ArrayList searchPlaylists(final Command command, final ArrayList<PlayList> playlists,
                                     final ObjectNode out) {

        Filter filter = command.getFilters();
        //creem output-ul
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode results = objectMapper.createArrayNode();
        int resultsCounter = 0;
        //verificator in legatura cu faptul ca am mai intrat in
        //vreun if
        ArrayList<String> searched = new ArrayList<>();

        if (filter.getName() != null) {
            for (int i = 0; i < playlists.size(); i++) {
                for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                    if (playlists.get(i).getPlaylists().get(j).getName()
                            .startsWith(filter.getName())) {
                        if (playlists.get(i).getPlaylists().get(j).isVisibility() || playlists.get(
                                i).getUsername().equals(command.getUsername())) {
                            searched.add(playlists.get(i).getPlaylists().get(j).getName());
                            resultsCounter++;
                        }
                    }
                }
            }
        }

        if (filter.getOwner() != null) {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).getUsername().equals(filter.getOwner())) {
                    for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                        if (playlists.get(i).getPlaylists().get(j).isVisibility() || playlists.get(
                                i).getUsername().equals(command.getUsername())) {
                            searched.add(playlists.get(i).getPlaylists().get(j).getName());
                            resultsCounter++;
                        }
                    }
                }
            }
        }
        out.put("message", "Search returned " + resultsCounter + " results");
        for (int i = 0; i < searched.size(); i++) {
            results.add(searched.get(i));
        }
        out.set("results", results);
        ArrayList returnArr = new ArrayList<>();
        for (int i = 0; i < searched.size() && i < MAX; i++) {
            returnArr.add(searched.get(i));
        }
        return returnArr;
    }

    /**
     * functie care cauta podcasturile in biblioteca in functie de parametrii primiti ca filtre
     *
     * @param command comanda actuala
     * @param library continutul bibliotecii
     * @param out     output ul comenzii
     * @return
     */
    public ArrayList searchPodcast(final Command command, final LibraryInput library,
                                   final ObjectNode out) {
        Filter filter = command.getFilters();
        ArrayList<PodcastInput> podcasts = library.getPodcasts();
        //creem output-ul
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode results = objectMapper.createArrayNode();
        int resultsCounter = 0;
        int verify = 0;
        //verificator in legatura cu faptul ca am mai intrat in
        //vreun if
        ArrayList<PodcastInput> searched = new ArrayList<>();

        if (filter.getName() != null) {
            if (verify == 0) {
                for (int i = 0; i < podcasts.size(); i++) {
                    if (podcasts.get(i).getName().startsWith(filter.getName())) {
                        searched.add(podcasts.get(i));
                        resultsCounter++;


                    }
                }
            } else {
                for (int i = 0; i < searched.size(); i++) {
                    if (!searched.get(i).getName().startsWith(filter.getName())) {
                        searched.remove(i);
                        resultsCounter--;
                        i--;
                    }
                }
            }

            verify = 1;
        }

        if (filter.getOwner() != null) {
            if (verify == 0) {
                for (int i = 0; i < podcasts.size(); i++) {
                    if (podcasts.get(i).getOwner().equals(filter.getOwner())) {
                        searched.add(podcasts.get(i));
                        resultsCounter++;


                    }
                }
            } else {
                for (int i = 0; i < searched.size(); i++) {
                    if (!searched.get(i).getOwner().equals(filter.getOwner())) {
                        searched.remove(i);
                        resultsCounter--;
                        i--;
                    }
                }
            }

            verify = 1;
        }

        resultsCounter = Math.min(resultsCounter, MAX);
        out.put("message", "Search returned " + resultsCounter + " results");
        for (int i = 0; i < searched.size() && i < resultsCounter; i++) {
            results.add(searched.get(i).getName());
        }
        out.set("results", results);
        //return out;
        ArrayList returnArr = new ArrayList<>();
        for (int i = 0; i < searched.size() && i < resultsCounter; i++) {
            returnArr.add(searched.get(i).getName());
        }
        return returnArr;
    }

}
