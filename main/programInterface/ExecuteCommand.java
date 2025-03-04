package main.programInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.LibraryInput;
import main.database.*;

import java.util.ArrayList;

public final class ExecuteCommand extends NavigateClass {
    private static ExecuteCommand instance = null;
    private ArrayList<Command> commands;
    private ObjectMapper objectMapper;

    public ExecuteCommand() {
        commands = new ArrayList<>();
        objectMapper = new ObjectMapper();
    }

    /**
     * metoda pentru a creea un obiect de tip NavigateClass
     *
     * @return obiectul de tip NavigateClass
     */
    public static ExecuteCommand getInstance() {
        if (instance == null) {
            synchronized (ExecuteCommand.class) {
                if (instance == null) {
                    instance = new ExecuteCommand();
                }
            }
        }
        return instance;
    }

    /**
     * metoda care parseaza comanda de tip "search" si apeleaza functiile specifice
     */
    public void parseCommandSearch(final ArrayList<Status> statuses, final Command cmd,
                                   final SearchBar searchBar, final ArrayNode outputs,
                                   final ArrayList searchedIn, final LibraryInput library,
                                   final UserManagement users, final PageManagementHub pages,
                                   final ArrayList<Loaded> loaded,
                                   final PrincipalDatabase principalDatabase) {
        ArrayList<SearchedSongsByUsers> searchData = principalDatabase.getSearchData();
        for (int k = 0; k < statuses.size(); k++) {
            //cautam statusul userului care a dat comanda
            if (statuses.get(k).getUser().equals(cmd.getUsername())) {
                if (statuses.get(k).getTypeOfListening().equals("podcast")) {
                    int time = -1;
                    if (!statuses.get(k).getStats().isPaused()) {
                        time = statuses.get(k).getStats().getRemainedTime() - (cmd.getTimestamp()
                                - statuses.get(k).getTimestamp());
                    } else {
                        time = statuses.get(k).getStats().getRemainedTime();
                    }
                    int okAlreadyHerePodcast = 0;
                    for (int i = 0; i < statuses.get(k).getPodcastsWatcheds().size(); i++) {
                        if (statuses.get(k).getPodcastsWatcheds().get(i).getPodcastName()
                                .equals(statuses.get(k).getCurrentEpisode())) {
                            okAlreadyHerePodcast = 1;
                            break;
                        }
                    }
                    if (okAlreadyHerePodcast == 0) {
                        PodcastsWatched newPodcastWatched = new PodcastsWatched();
                        newPodcastWatched.setPodcastName(statuses.get(k).getCurrentEpisode());
                        newPodcastWatched.setLastEpisode(statuses.get(k).getStats().getName());
                        newPodcastWatched.setLastTimestamp(time);
                        statuses.get(k).getPodcastsWatcheds().add(newPodcastWatched);
                    }
                }
            }
        }
        ArrayList searched = searchedIn;
        ObjectNode out = objectMapper.createObjectNode();
        for (int i = 0; i < users.getUsers().size(); i++) {
            if (users.getUsers().get(i).getUsername().equals(cmd.getUsername())) {
                //cautam utilizatorul care a dat search
                if (users.getUsers().get(i).getConnectionStatus().equals("offline")) {
                    //daca acesta este offline, nu se mai face nimic
                    out.put("command", cmd.getCommand());
                    out.put("user", cmd.getUsername());
                    out.put("timestamp", cmd.getTimestamp());
                    ArrayList<String> empty = new ArrayList<>();
                    out.put("message", cmd.getUsername() + " is offline.");
                    out.putPOJO("results", empty);
                    outputs.add(out);
                    return;
                }
            }
        }
        for (int j = 0; j < statuses.size(); j++) {
            Status currSt = statuses.get(j);
            if (currSt.getUser().equals(cmd.getUsername())) {
                //cautam statusul utilizatorului care a dat search
                //setam faptul ca acesta nu mai are nimic incarcat
                //setam statusul ca fiind gol
                currSt.getStats().setPaused(true);
                currSt.getStats().setRemainedTime(
                        currSt.getStats().getRemainedTime() - (-currSt.getTimestamp()
                                + cmd.getTimestamp()));
                statuses.get(j).setEmpty(true);
                break;
            }
        }
        for (int p = 0; p < principalDatabase.getSelected().size(); p++) {
            if (principalDatabase.getSelected().get(p).getUser().equals(cmd.getUsername())) {
                //cautam selectiile utilizatorului care a dat search
                //setam faptul ca acesta nu mai are nimic selectat
                principalDatabase.getSelected().get(p).setSelected(false);
            }
        }
        int nameExists = -1;
        for (int j = 0; j < searchData.size(); j++) {
            if (searchData.get(j).getUser().equals(cmd.getUsername())) {
                //cautam "search-ul" utilizatorului, in caz ca exista
                //daca nu exista un search pentru acest utilizator inseamna ca
                //acesta nu a mai cautat nimic anterior
                searchData.get(j).setSearched(true);
                nameExists = j;
                break;
            }
        }
        SearchedSongsByUsers newNode = null;
        if (nameExists == -1) {
            //cazul in care acesta nu a mai cautat nimic anterior
            newNode = new SearchedSongsByUsers();
            newNode.setSearched(true);
            newNode.setUser(cmd.getUsername());
            newNode.setSearchType(cmd.getType());
        } else {
            newNode = searchData.get(nameExists);
            searchData.get(nameExists).getSongs().clear();
            searchData.get(nameExists).getPodcasts().clear();
            searchData.get(nameExists).getPlaylists().clear();
            newNode.setSearchType(cmd.getType());
        }
        //in functie de tipul cautarii, accesam functiile specifice acestora
        //cautam melodie/podcast/playlist/artist/album
        if (cmd.getType().equals("song")) {
            newNode.setSearchType("song");
            ArrayList<Integer> timeStamp = new ArrayList<>();
            searched = searchBar.searchSong(cmd, library, out, timeStamp);
            newNode.setSongs(searched);
            newNode.setSongTimestamp(timeStamp);
        }
        if (cmd.getType().equals("playlist")) {
            newNode.setSearchType("playlist");
            searched =
                    searchBar.searchPlaylists(cmd, principalDatabase.getPlaylistsPrincipal(), out);
            newNode.setPlaylists(searched);
        }
        if (cmd.getType().equals("podcast")) {
            newNode.setSearchType("podcast");
            searched = searchBar.searchPodcast(cmd, library, out);
            newNode.setPodcasts(searched);
        }
        if (cmd.getType().equals("artist")) {
            newNode.setSearchType("artist");
            searched = searchBar.searchArtist(cmd, pages, out);
            newNode.setArtists(searched);
        }
        if (cmd.getType().equals("host")) {
            newNode.setSearchType("host");
            searched = searchBar.searchHost(cmd, pages, out);
            newNode.setHosts(searched);
        }
        if (cmd.getType().equals("album")) {
            newNode.setSearchType("album");
            ArrayList<String> frstSng = new ArrayList<>();
            searched = searchBar.searchAlbum(cmd, pages, out, frstSng);
            newNode.setAlbums(searched);
            newNode.setFrstSng(frstSng);
        }
        if (nameExists == -1) {
            searchData.add(newNode);
        }
        outputs.add(out);
        //cautam statusul utilizatorului care a dat search
        //setam faptul ca acesta nu mai are nimic incarcat
        //setam "loaded"-ul specific utilizatorului din baza de date
        //ca fiind gol
        for (int j = 0; j < loaded.size(); j++) {
            if (loaded.get(j).getUser().equals(cmd.getUsername())) {
                loaded.get(j).setLoaded(false);
                break;
            }
        }
    }

    /**
     * metoda care pregateste si apeleaza metoda de status
     * @param command comanda primita
     * @param library biliblioteca de input
     * @param outputs array-ul de output-uri
     * @param principalDatabase baza de date
     */
    public void status(final Command command, final LibraryInput library, final ArrayNode outputs,
                       final PrincipalDatabase principalDatabase) {
        ArrayList<PlayList> playlists = principalDatabase.getPlaylistsPrincipal();
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        super.status(statuses, out, command, library, playlists, users, pages,
                principalDatabase.getLoaded());
        outputs.add(out);
    }

    /**
     * metoda care pregateste si apeleaza metoda de search
     * @param command comanda primita
     * @param library biliblioteca de input
     * @param outputs array-ul de output-uri
     * @param searchBar bara de search
     * @param searched array-ul de cautari
     * @param principalDatabase baza de date
     */
    public void search(final Command command, final LibraryInput library, final ArrayNode outputs,
                       final SearchBar searchBar, final ArrayList searched,
                       final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        ArrayList<Loaded> loaded = principalDatabase.getLoaded();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, loaded);
        this.parseCommandSearch(statuses, command, searchBar, outputs, searched,
                library, users, pages, loaded, principalDatabase);
    }

    /**
     * metoda care pregateste si apeleaza metoda de select
     */
    public void select(final Command command, final SearchBar searchBar, final ArrayNode outputs,
                       final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ArrayList<SearchedSongsByUsers> searchData = principalDatabase.getSearchData();
        ObjectNode out = objectMapper.createObjectNode();
        outputs.add(searchBar.select(command, searchData, out, principalDatabase.getSelected()));
        pages.setUserPageToArtist(command, searchData, users, principalDatabase.getSelected());
        pages.setUserPageToHost(command, searchData, users, principalDatabase.getSelected());
        pages.setUserPageToHome(command, principalDatabase.getSelected(), users);
    }

    /**
     * metoda care pregateste si apeleaza metoda de load
     */
    public void load(final Command command, final LibraryInput library, final ArrayNode outputs,
                     final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        super.load(principalDatabase.getSelected(), command, principalDatabase.getLoaded(), out,
                statuses, library, principalDatabase.getPlaylistsPrincipal(), pages);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de playPause
     */
    public void playPause(final Command command, final LibraryInput library,
                          final ArrayNode outputs, final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        ObjectNode out = objectMapper.createObjectNode();
        super.playPause(statuses, out, command);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de createPlaylist
     */
    public void createPlaylist(final Command command, final ArrayNode outputs,
                               final PrincipalDatabase principalDatabase) {
        ObjectNode out = objectMapper.createObjectNode();
        super.createPlaylist(command, out, principalDatabase.getPlaylistsPrincipal());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de like
     */
    public void like(final Command command, final LibraryInput library, final ArrayNode outputs,
                     final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        super.like(principalDatabase.getLikesByUsernames(), principalDatabase.getLikesBySongs(),
                command, principalDatabase.getLoaded(), out, statuses, users);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de addRemoveInPlaylist
     */
    public void addRemoveInPlaylist(final Command command, final LibraryInput library,
                                    final ArrayNode outputs,
                                    final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        for (int cnt = 0; cnt < principalDatabase.getSelected().size(); cnt++) {
            if (principalDatabase.getSelected().get(cnt).getUser().equals(command.getUsername())) {
                principalDatabase.getSelected().get(cnt).setSelected(false);
            }
        }
        ObjectNode out = objectMapper.createObjectNode();
        super.addRemoveInPlayList(command, out, principalDatabase.getPlaylistsPrincipal(),
                principalDatabase.getLoaded(), statuses, library);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de showPlaylist
     */
    public void showPlaylists(final Command command, final ArrayNode outputs,
                              final PrincipalDatabase principalDatabase) {
        ObjectNode out = objectMapper.createObjectNode();
        super.showPlaylists(command, out, principalDatabase.getPlaylistsPrincipal());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de showPrefferedSongs
     */
    public void showPreferredSongs(final Command command, final ArrayNode outputs,
                                   final PrincipalDatabase principalDatabase) {
        ObjectNode out = objectMapper.createObjectNode();
        super.showPreferredSongs(command, out, principalDatabase.getLikesByUsernames());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de repeat
     */
    public void repeat(final Command command, final ArrayNode outputs, final LibraryInput library,
                       final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        ObjectNode out = objectMapper.createObjectNode();
        super.repeat(command, out, statuses, principalDatabase.getLoaded());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de shuffle
     */
    public void shuffle(final Command command, final ArrayNode outputs, final LibraryInput library,
                        final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        ObjectNode out = objectMapper.createObjectNode();
        super.shuffle(command, out, statuses, principalDatabase.getLoaded());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de next
     */
    public void next(final Command command, final ArrayNode outputs, final LibraryInput library,
                     final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        super.next(statuses, command, library, principalDatabase.getPlaylistsPrincipal(), out,
                pages);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de prev
     */
    public void prev(final Command command, final ArrayNode outputs, final LibraryInput library,
                     final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        super.prev(statuses, command, library, principalDatabase.getPlaylistsPrincipal(),
                principalDatabase.getLoaded(), out, pages);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de forward
     */
    public void forward(final Command command, final ArrayNode outputs, final LibraryInput library,
                        final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        super.forward(statuses, out, command, library);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de backward
     */
    public void backward(final Command command, final ArrayNode outputs,
                         final LibraryInput library, final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        super.backward(statuses, out, command, library);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de follow
     */
    public void follow(final Command command, final ArrayNode outputs,
                       final PrincipalDatabase principalDatabase) {
        ObjectNode out = objectMapper.createObjectNode();
        super.follow(principalDatabase.getSelected(), out, command,
                principalDatabase.getPlaylistsPrincipal());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de swichVisibility
     */
    public void switchVisibility(final Command command, final ArrayNode outputs,
                                 final PrincipalDatabase principalDatabase) {
        ObjectNode out = objectMapper.createObjectNode();
        super.swichVisibility(command, out, principalDatabase.getPlaylistsPrincipal());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de getTop5Playlists
     */
    public void getTop5Playlists(final Command command, final ArrayNode outputs,
                                 final PrincipalDatabase principalDatabase) {
        ObjectNode out = objectMapper.createObjectNode();
        super.getTop5Playlists(command, out, principalDatabase.getPlaylistsPrincipal());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de getTop5Songs
     */
    public void getTop5Songs(final Command command, final ArrayNode outputs,
                             final LibraryInput library,
                             final PrincipalDatabase principalDatabase) {
        ObjectNode out = objectMapper.createObjectNode();
        super.getTop5Songs(command, out, principalDatabase.getLikesBySongs(), library);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de switchConnectionStatus
     */
    public void switchConnectionStatus(final Command command, final ArrayNode outputs,
                                       final LibraryInput library,
                                       final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out2 = objectMapper.createObjectNode();
        super.status(statuses, out2, command, library, principalDatabase.getPlaylistsPrincipal(),
                users, pages, principalDatabase.getLoaded());
        ObjectNode out = objectMapper.createObjectNode();
        users.switchConnectionStatus(command, out);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de getOnlineUsers
     */
    public void getOnlineUsers(final Command command, final ArrayNode outputs,
                               final GeneralStatistics statistics,
                               final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        ObjectNode out = objectMapper.createObjectNode();
        statistics.getOnlineUsers(command, out, users.getUsers());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de addUser
     */
    public void addUser(final Command command, final ArrayNode outputs,
                        final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.createPage(users.addUser(command, out), command);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de addAlbum
     */
    public void addAlbum(final Command command, final ArrayNode outputs,
                         final LibraryInput library, final PrincipalDatabase principalDatabase) {
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.addAlbum(command, out, library);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de addPodcast
     */
    public void addPodcast(final Command command, final ArrayNode outputs,
                           final LibraryInput library, final PrincipalDatabase principalDatabase) {
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.addPodcast(command, out, library);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de removePodcast
     */
    public void removePodcast(final Command command, final ArrayNode outputs,
                              final LibraryInput library,
                              final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        String aux = command.getUsername();
        for (int cnt = 0; cnt < users.getUsers().size(); cnt++) {
            command.setUsername(users.getUsers().get(cnt).getUsername());
            ObjectNode out2 = objectMapper.createObjectNode();
            super.status(statuses, out2, command, library,
                    principalDatabase.getPlaylistsPrincipal(), users, pages,
                    principalDatabase.getLoaded());
        }
        command.setUsername(aux);
        ObjectNode out = objectMapper.createObjectNode();
        pages.removePodcast(command, out, library, statuses);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de removeAlbum
     */
    public void removeAlbum(final Command command, final ArrayNode outputs,
                            final LibraryInput library,
                            final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        String aux = command.getUsername();
        for (int cnt = 0; cnt < users.getUsers().size(); cnt++) {
            command.setUsername(users.getUsers().get(cnt).getUsername());
            ObjectNode out2 = objectMapper.createObjectNode();
            super.status(statuses, out2, command, library,
                    principalDatabase.getPlaylistsPrincipal(), users, pages,
                    principalDatabase.getLoaded());
        }
        command.setUsername(aux);
        ObjectNode out = objectMapper.createObjectNode();
        pages.removeAlbum(command, out, library, statuses,
                principalDatabase.getPlaylistsPrincipal(), users.getUsers());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de showAlbums
     */
    public void showAlbums(final Command command, final ArrayNode outputs,
                           final PrincipalDatabase principalDatabase) {
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.showAlbums(command, out);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de showPodcasts
     */
    public void showPodcasts(final Command command, final ArrayNode outputs,
                             final PageManagementHub pages) {
        ObjectNode out = objectMapper.createObjectNode();
        pages.showPodcasts(command, out);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de printCurrentPage
     */
    public void printCurrentPage(final Command command, final ArrayNode outputs,
                                 final LibraryInput library,
                                 final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.printCurrentPage(command, out, users.getUsers(),
                principalDatabase.getLikesByUsernames(), principalDatabase.getPlaylistsPrincipal(),
                library, principalDatabase.getLikesBySongs());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de changePage
     */
    public void changePage(final Command command, final ArrayNode outputs,
                           final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ArrayList<SearchedSongsByUsers> searchData = principalDatabase.getSearchData();
        ObjectNode out = objectMapper.createObjectNode();
        pages.changePage(command, out, users.getUsers(), searchData);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de addEvent
     */
    public void addEvent(final Command command, final ArrayNode outputs,
                         final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.addEvent(command, out, users.getUsers());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de removeEvent
     */
    public void removeEvent(final Command command, final ArrayNode outputs,
                            final PrincipalDatabase principalDatabase) {
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.removeEvent(command, out);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de addAnnouncement
     */
    public void addAnnouncement(final Command command, final ArrayNode outputs,
                                final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.addAnnouncement(command, out, users.getUsers());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de removeAnnouncement
     */
    public void removeAnnouncement(final Command command, final ArrayNode outputs,
                                   final PrincipalDatabase principalDatabase) {
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.removeAnnouncement(command, out);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de addMerch
     */
    public void addMerch(final Command command, final ArrayNode outputs,
                         final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        pages.addMerch(command, out, users.getUsers());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de getAllUsers
     */
    public void getAllUsers(final Command command, final ArrayNode outputs,
                            final GeneralStatistics statistics,
                            final PrincipalDatabase principalDatabase) {
        UserManagement users = principalDatabase.getUsers();
        ObjectNode out = objectMapper.createObjectNode();
        statistics.getAllUsers(command, out, users.getUsers());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de deleteUser
     */
    public void deleteUser(final Command command, final ArrayNode outputs,
                           final LibraryInput library, final PrincipalDatabase principalDatabase) {
        ArrayList<Status> statuses = principalDatabase.getStatuses();
        UserManagement users = principalDatabase.getUsers();
        PageManagementHub pages = principalDatabase.getPages();
        ArrayList<SearchedSongsByUsers> searchData = principalDatabase.getSearchData();
        String aux = command.getUsername();
        for (int cnt = 0; cnt < users.getUsers().size(); cnt++) {
            command.setUsername(users.getUsers().get(cnt).getUsername());
            ObjectNode out2 = objectMapper.createObjectNode();
            super.status(statuses, out2, command, library,
                    principalDatabase.getPlaylistsPrincipal(), users, pages,
                    principalDatabase.getLoaded());
        }
        command.setUsername(aux);
        ObjectNode out = objectMapper.createObjectNode();
        pages.deletePage(users.deleteUser(command, out, statuses, pages, searchData,
                        principalDatabase.getPlaylistsPrincipal(),
                        principalDatabase.getLoaded()), command,
                library, principalDatabase.getLikesBySongs(),
                principalDatabase.getLikesByUsernames());
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de getTop5Albums
     */
    public void getTop5Albums(final Command command, final ArrayNode outputs,
                              final GeneralStatistics statistics,
                              final PrincipalDatabase principalDatabase) {
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        statistics.getTop5Albums(command, out, principalDatabase.getLikesBySongs(), pages);
        outputs.add(out);
    }
    /**
     * metoda care pregateste si apeleaza metoda de getTop5Artists
     */
    public void getTop5Artists(final Command command, final ArrayNode outputs,
                               final GeneralStatistics statistics,
                               final PrincipalDatabase principalDatabase) {
        PageManagementHub pages = principalDatabase.getPages();
        ObjectNode out = objectMapper.createObjectNode();
        statistics.getTop5Artists(command, out, principalDatabase.getLikesBySongs(), pages);
        outputs.add(out);
    }

    public void setCommands(final ArrayList<Command> commands) {
        this.commands = commands;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
