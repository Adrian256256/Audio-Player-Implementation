package main.programInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import main.database.*;

import java.util.ArrayList;
import java.util.Collections;

public final class PageManagementHub {
    private static final int LEN_3 = 3;
    private static final int LEN_4 = 4;
    private static final int LEN_31 = 31;
    private static final int LEN_12 = 12;
    private static final int LEN_5 = 5;
    private static final int LEN_2 = 2;
    private static final int LEN_1 = 1;
    private final ArrayList<ArtistPage> artistPages = new ArrayList<>();
    private final ArrayList<HostPage> hostPages = new ArrayList<>();

    public ArrayList<HostPage> getHostPages() {
        return hostPages;
    }

    public ArrayList<ArtistPage> getArtistPages() {
        return artistPages;
    }

    /**
     * Metoda care adauga un merch nou in pagina unui artist
     *
     * @param command comanda primita
     * @param out     mesajul de output
     * @param users   lista de utilizatori
     */
    public void addMerch(final Command command, final ObjectNode out,
                         final ArrayList<UserDatabase> users) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (ArtistPage artistPage : artistPages) {
            if (artistPage.getName().equals(command.getUsername())) {
                //daca mai exista un merch cu acelasi nume afisam eroare si nu adaugam
                for (int i = 0; i < artistPage.getMerch().size(); i++) {
                    if (artistPage.getMerch().get(i).getName().equals(command.getName())) {
                        out.put("message",
                                command.getUsername() + " has merchandise with the same name.");
                        return;
                    }
                }
                //daca pretul este negativ afisam eroare
                if (command.getPrice() < 0) {
                    out.put("message", "Price for merchandise can not be negative.");
                    return;
                }
                Merch merch = new Merch();
                merch.setName(command.getName());
                merch.setDescription(command.getDescription());
                merch.setPrice(command.getPrice());
                artistPage.getMerch().add(merch);
                out.put("message",
                        command.getUsername() + " has added new merchandise successfully.");
                return;
            }
        }
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                out.put("message", command.getUsername() + " is not an artist.");
                return;
            }
        }
        out.put("message", "The username " + command.getUsername() + " doesn't exist.");
    }

    /**
     * Metoda care sterge un anunt din pagina unui host
     */
    public void removeAnnouncement(final Command command, final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        for (HostPage hostPage : hostPages) {
            if (hostPage.getName().equals(command.getUsername())) {
                //verific daca exista anuntul
                for (int i = 0; i < hostPage.getAnnouncements().size(); i++) {
                    if (hostPage.getAnnouncements().get(i).getName().equals(command.getName())) {
                        hostPage.getAnnouncements().remove(i);
                        out.put("message", command.getUsername()
                                + " has successfully deleted the announcement.");
                        return;
                    }
                }
                out.put("message",
                        command.getUsername() + " has no announcement with the given name.");
                return;
            }
        }
        out.put("message", command.getUsername() + " is not a host.");
    }

    /**
     * Metoda care adauga un anunt nou in pagina unui host
     */
    public void addAnnouncement(final Command command, final ObjectNode out,
                                final ArrayList<UserDatabase> users) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (HostPage hostPage : hostPages) {
            if (hostPage.getName().equals(command.getUsername())) {
                //daca mai exista un anunt cu acelasi nume afisam eroare si nu adaugam
                for (int i = 0; i < hostPage.getAnnouncements().size(); i++) {
                    if (hostPage.getAnnouncements().get(i).getName().equals(command.getName())) {
                        out.put("message", command.getUsername()
                                + " has another announcement with the same name.");
                        return;
                    }
                }
                Announcement announcement = new Announcement();
                announcement.setName(command.getName());
                announcement.setDescription(command.getDescription());
                hostPage.getAnnouncements().add(announcement);
                out.put("message",
                        command.getUsername() + " has successfully added new announcement.");
                return;
            }
        }
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                out.put("message", command.getUsername() + " is not a host.");
                return;
            }
        }
        out.put("message", "The username " + command.getUsername() + " doesn't exist.");
    }

    /**
     * Metoda care sterge un event din pagina unui artist
     */
    public void removeEvent(final Command command, final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        for (ArtistPage artistPage : artistPages) {
            if (artistPage.getName().equals(command.getUsername())) {
                //verific daca exista evenimentul
                for (int i = 0; i < artistPage.getEvents().size(); i++) {
                    if (artistPage.getEvents().get(i).getName().equals(command.getName())) {
                        artistPage.getEvents().remove(i);
                        out.put("message",
                                command.getUsername() + " deleted the event successfully.");
                        return;
                    }
                }
                out.put("message", command.getUsername() + " has no event with the given name.");
                return;
            }
        }
        out.put("message", command.getUsername() + " is not an artist.");
    }

    /**
     * Metoda care adauga un event nou in pagina unui artist
     */
    public void addEvent(final Command command, final ObjectNode out,
                         final ArrayList<UserDatabase> users) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (ArtistPage artistPage : artistPages) {
            if (artistPage.getName().equals(command.getUsername())) {
                //verific daca command.getDate() este in formatul corect dd-mm-yyyy
                //verificare generata cu GitHub Copilot
                String[] date = command.getDate().split("-");
                if (date.length != LEN_3) {
                    out.put("message",
                            "Event for " + command.getUsername() + " does not have a valid date.");
                    return;
                }
                if (date[0].length() != LEN_2 || date[1].length() != LEN_2
                        || date[2].length() != LEN_4) {
                    out.put("message",
                            "Event for " + command.getUsername() + " does not have a valid date.");
                    return;
                }
                if (Integer.parseInt(date[0]) < LEN_1 || Integer.parseInt(date[0]) > LEN_31) {
                    out.put("message",
                            "Event for " + command.getUsername() + " does not have a valid date.");
                    return;
                }
                if (Integer.parseInt(date[1]) < LEN_1 || Integer.parseInt(date[1]) > LEN_12) {
                    out.put("message",
                            "Event for " + command.getUsername() + " does not have a valid date.");
                    return;
                }
                Event event = new Event();
                event.setName(command.getName());
                event.setDate(command.getDate());
                event.setDescription(command.getDescription());
                artistPage.getEvents().add(event);
                out.put("message", command.getUsername() + " has added new event successfully.");
                return;
            }
        }
        //verific daca utilizatorul nu exista sau nu este artist
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                out.put("message", command.getUsername() + " is not an artist.");
                return;
            }
        }
        out.put("message", "The username " + command.getUsername() + " doesn't exist.");
    }

    /**
     * Metoda care adauga o pagina noua in baza de date
     */
    public void createPage(final String type, final Command command) {
        if (type.equals("artist")) {
            ArtistPage artistPage = new ArtistPage();
            artistPage.setName(command.getUsername());
            artistPages.add(artistPage);
        }
        if (type.equals("host")) {
            HostPage hostPage = new HostPage();
            hostPage.setName(command.getUsername());
            hostPages.add(hostPage);
        }
    }

    /**
     * Metoda care sterge o pagina din baza de date
     */
    public void deletePage(final String go, final Command command, final LibraryInput library,
                           final ArrayList<LikesBySong> likesBySongs,
                           final ArrayList<LikesByUsername> likesByUsernames) {
        if (go.equals("success")) {
            //intai stergem toate melodiile din library care sunt in albumele utilizatorului
            for (ArtistPage artistPage : artistPages) {
                if (artistPage.getName().equals(command.getUsername())) {
                    for (int i = 0; i < artistPage.getAlbums().size(); i++) {
                        for (int j = 0; j < artistPage.getAlbums().get(i).getSongs().size(); j++) {
                            for (int k = 0; k < library.getSongs().size(); k++) {
                                if (library.getSongs().get(k).getName()
                                        .equals(artistPage.getAlbums().get(i).getSongs().get(j)
                                                .getName())) {
                                    library.getSongs().remove(k);
                                }
                            }
                        }
                    }
                }
            }
            //stergem melodiile din likesBySongs si likesByUsernames care apartin userului
            for (ArtistPage artistPage : artistPages) {
                if (artistPage.getName().equals(command.getUsername())) {
                    for (int i = 0; i < artistPage.getAlbums().size(); i++) {
                        for (int j = 0; j < artistPage.getAlbums().get(i).getSongs().size(); j++) {
                            //cautam si stergem melodia din likesBySongs si likesByUsernames
                            String melody =
                                    artistPage.getAlbums().get(i).getSongs().get(j).getName();
                            for (int k = 0; k < likesBySongs.size(); k++) {
                                if (likesBySongs.get(k).getSongName().equals(melody)) {
                                    if (likesBySongs.get(k).getSongAlbum()
                                            .equals(artistPage.getAlbums().get(i).getName())) {
                                        likesBySongs.remove(k);
                                    }
                                }
                                for (int l = 0; l < likesByUsernames.size(); l++) {
                                    for (int m = 0;
                                         m < likesByUsernames.get(l).getLikedSongs().size(); m++) {
                                        if (likesByUsernames.get(l).getLikedSongs().get(m)
                                                .equals(melody)) {
                                            likesByUsernames.get(l).getLikedSongs().remove(m);
                                            likesByUsernames.get(l).getLikedSongsAlbum().remove(m);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //stergem like urile userului din likesbyusernames si likesbysongs
            for (int i = 0; i < likesByUsernames.size(); i++) {
                if (likesByUsernames.get(i).getUsername().equals(command.getUsername())) {
                    likesByUsernames.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < likesBySongs.size(); i++) {
                for (int j = 0; j < likesBySongs.get(i).getUsersThatLiked().size(); j++) {
                    if (likesBySongs.get(i).getUsersThatLiked().get(j)
                            .equals(command.getUsername())) {
                        likesBySongs.get(i).getUsersThatLiked().remove(j);
                        j--;
                    }
                }
            }
            for (int i = 0; i < artistPages.size(); i++) {
                if (artistPages.get(i).getName().equals(command.getUsername())) {
                    artistPages.remove(i);
                    return;
                }
            }
        }
    }

    /**
     * Metoda care sterge un album din pagina unui artist
     */
    public void removeAlbum(final Command command, final ObjectNode out,
                            final LibraryInput library, final ArrayList<Status> statuses,
                            final ArrayList<PlayList> playlists,
                            final ArrayList<UserDatabase> users) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        for (ArtistPage artistPage : artistPages) {
            if (artistPage.getName().equals(command.getUsername())) {
                //verific daca exista albumul
                for (int i = 0; i < artistPage.getAlbums().size(); i++) {
                    if (artistPage.getAlbums().get(i).getName().equals(command.getName())) {
                        //verific daca cineva asculta o melodie din album
                        //caut in statuses.getStats daca exista ceva cu numele unei melodii din
                        // album
                        //fac asta pt fiecare melodie din album
                        for (int j = 0; j < artistPage.getAlbums().get(i).getSongs().size(); j++) {
                            for (int k = 0; k < statuses.size(); k++) {
                                if (statuses.get(k).getStats().getName()
                                        .equals(artistPage.getAlbums().get(i).getSongs().get(j)
                                                .getName())) {
                                    out.put("message",
                                            command.getUsername() + " can't delete this album.");
                                    return;
                                }
                            }
                        }
                        //verific daca o melodie din album este continuta in vreun playlist
                        // existent
                        //caut in playlists daca exista ceva cu numele unei melodii din album
                        //fac asta pt fiecare melodie din album
                        for (int j = 0; j < artistPage.getAlbums().get(i).getSongs().size(); j++) {
                            for (int k = 0; k < playlists.size(); k++) {
                                for (int l = 0; l < playlists.get(k).getPlaylists().size(); l++) {
                                    for (int m = 0;
                                         m < playlists.get(k).getPlaylists().get(l).getSongs()
                                                 .size(); m++) {
                                        if (playlists.get(k).getPlaylists().get(l).getSongs()
                                                .get(m)
                                                .equals(artistPage.getAlbums().get(i).getSongs()
                                                        .get(j).getName())) {
                                            out.put("message", command.getUsername()
                                                    + " can't delete this album.");
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        //sterg albumul din library
                        for (int j = 0; j < library.getSongs().size(); j++) {
                            for (int k = 0;
                                 k < artistPage.getAlbums().get(i).getSongs().size(); k++) {
                                if (library.getSongs().get(j).getName()
                                        .equals(artistPage.getAlbums().get(i).getSongs().get(k)
                                                .getName())) {
                                    library.getSongs().remove(j);
                                }
                            }
                        }
                        //stergem toate melodiile din playlisturile utilizatorilor care apartin
                        // albumului
                        for (int j = 0; j < playlists.size(); j++) {
                            for (int k = 0; k < playlists.get(j).getPlaylists().size(); k++) {
                                for (int l = 0;
                                     l < playlists.get(j).getPlaylists().get(k).getSongs()
                                             .size(); l++) {
                                    for (int m = 0; m < artistPage.getAlbums().get(i).getSongs()
                                            .size(); m++) {
                                        if (playlists.get(j).getPlaylists().get(k).getSongs()
                                                .get(l)
                                                .equals(artistPage.getAlbums().get(i).getSongs()
                                                        .get(m).getName())) {
                                            //dam remove la melodia din playlist si ajustam
                                            // statusul
                                            playlists.get(j).getPlaylists().get(k).getSongs()
                                                    .remove(l);
                                        }
                                    }
                                }
                            }
                        }
                        artistPage.getAlbums().remove(i);
                        out.put("message",
                                command.getUsername() + " deleted the album successfully.");
                        return;
                    }
                }
                out.put("message",
                        command.getUsername() + " doesn't have an album with the given name.");
                return;
            }
        }
        //verific daca exista userul, caut in users
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                out.put("message", command.getUsername() + " is not an artist.");
                return;
            }
        }

        //nu exista acest user
        out.put("message", "The username " + command.getUsername() + " doesn't exist.");
    }

    /**
     * Metoda care sterge un podcast din pagina unui host
     */
    public void removePodcast(final Command command, final ObjectNode out,
                              final LibraryInput library, final ArrayList<Status> statuses) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        for (HostPage hostPage : hostPages) {
            if (hostPage.getName().equals(command.getUsername())) {
                //verific daca exista podcastul
                for (int i = 0; i < hostPage.getPodcasts().size(); i++) {
                    if (hostPage.getPodcasts().get(i).getName().equals(command.getName())) {
                        //verific daca cineva asculta un episod din podcast
                        //caut in statuses.getStats daca exista ceva cu numele unui episod din
                        // podcast
                        //fac asta pt fiecare episod din podcast
                        for (int j = 0;
                             j < hostPage.getPodcasts().get(i).getEpisodes().size(); j++) {
                            for (int k = 0; k < statuses.size(); k++) {
                                if (statuses.get(k).getStats().getName()
                                        .equals(hostPage.getPodcasts().get(i).getEpisodes().get(j)
                                                .getName())) {
                                    out.put("message",
                                            command.getUsername() + " can't delete this podcast.");
                                    return;
                                }
                            }
                        }


                        //sterg podcastul din library
                        for (int j = 0; j < library.getPodcasts().size(); j++) {
                            if (library.getPodcasts().get(j).getName().equals(command.getName())) {
                                library.getPodcasts().remove(j);
                            }
                        }
                        hostPage.getPodcasts().remove(i);
                        out.put("message",
                                command.getUsername() + " deleted the podcast successfully.");
                        return;
                    }
                }
                out.put("message",
                        command.getUsername() + " doesn't have a podcast with the given name.");
                return;
            }
        }
        out.put("message", command.getUsername() + " is not a host.");
    }

    /**
     * Metoda care adauga un podcast in pagina unui host
     */
    public void addPodcast(final Command command, final ObjectNode out,
                           final LibraryInput library) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (HostPage hostPage : hostPages) {
            if (hostPage.getName().equals(command.getUsername())) {
                //verific daca mai exista un podcast cu acelasi nume
                for (int i = 0; i < hostPage.getPodcasts().size(); i++) {
                    if (hostPage.getPodcasts().get(i).getName().equals(command.getName())) {
                        out.put("message", command.getUsername()
                                + " has another podcast with the same name.");
                        return;
                    }
                }
                //verific daca un episod se repeta de 2 ori
                for (int i = 0; i < command.getEpisodes().size(); i++) {
                    for (int j = i + 1; j < command.getEpisodes().size(); j++) {
                        if (command.getEpisodes().get(i).getName()
                                .equals(command.getEpisodes().get(j).getName())) {
                            out.put("message", command.getUsername()
                                    + " has the same episode at least twice in this podcast.");
                            return;
                        }
                    }
                }
                Podcast podcast = new Podcast();
                podcast.setName(command.getName());
                podcast.setEpisodes(command.getEpisodes());
                hostPage.getPodcasts().add(podcast);
                //adaug podcastul in Podcasts din library
                PodcastInput podcastInput = new PodcastInput();
                podcastInput.setName(command.getName());
                podcastInput.setOwner(command.getUsername());
                ArrayList<EpisodeInput> episodes = new ArrayList<>();
                for (int i = 0; i < command.getEpisodes().size(); i++) {
                    episodes.add(command.getEpisodes().get(i));
                }
                podcastInput.setEpisodes(episodes);
                library.getPodcasts().add(podcastInput);

                out.put("message", command.getUsername() + " has added new podcast successfully.");
                return;
            }
        }
        out.put("message", command.getUsername() + " is not a host.");
    }

    /**
     * Metoda care adauga un album in pagina unui artist
     */
    public void addAlbum(final Command command, final ObjectNode out, final LibraryInput library) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (ArtistPage artistPage : artistPages) {
            if (artistPage.getName().equals(command.getUsername())) {
                //verific daca mai exista un album cu acelasi nume
                for (int i = 0; i < artistPage.getAlbums().size(); i++) {
                    if (artistPage.getAlbums().get(i).getName().equals(command.getName())) {
                        out.put("message",
                                command.getUsername() + " has another album with the same name.");
                        return;
                    }
                }
                //verific daca o melodie se repeta de 2 ori
                for (int i = 0; i < command.getSongs().size(); i++) {
                    for (int j = i + 1; j < command.getSongs().size(); j++) {
                        if (command.getSongs().get(i).getName()
                                .equals(command.getSongs().get(j).getName())) {
                            out.put("message", command.getUsername()
                                    + " has the same song at least twice in this album.");
                            return;
                        }
                    }
                }
                artistPage.addAlbum(command.getSongs(), command.getName(),
                        command.getDescription());
                //adaug melodiile in library
                for (int i = 0; i < command.getSongs().size(); i++) {
                    library.getSongs().add(command.getSongs().get(i));
                }
                out.put("message", command.getUsername() + " has added new album successfully.");
                return;
            }
        }
        out.put("message", command.getUsername() + " is not an artist.");
    }

    /**
     * Metoda care afiseaza podcasturile unui host
     */
    public void showPodcasts(final Command command, final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (HostPage hostPage : hostPages) {
            if (hostPage.getName().equals(command.getUsername())) {
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode results = objectMapper.createArrayNode();
                for (int i = 0; i < hostPage.getPodcasts().size(); i++) {
                    ObjectNode podcast = objectMapper.createObjectNode();
                    podcast.put("name", hostPage.getPodcasts().get(i).getName());
                    ArrayList<String> episodesNames = new ArrayList<>();
                    for (int j = 0; j < hostPage.getPodcasts().get(i).getEpisodes().size(); j++) {
                        episodesNames.add(
                                hostPage.getPodcasts().get(i).getEpisodes().get(j).getName());
                    }
                    podcast.putPOJO("episodes", episodesNames);
                    results.add(podcast);
                }
                out.put("result", results);
                return;
            }
        }
        out.put("message", command.getUsername() + " is not a host.");
    }

    /**
     * Metoda care afiseaza albumele unui artist
     */
    public void showAlbums(final Command command, final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (ArtistPage artistPage : artistPages) {
            if (artistPage.getName().equals(command.getUsername())) {
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode results = objectMapper.createArrayNode();
                for (int i = 0; i < artistPage.getAlbums().size(); i++) {
                    ObjectNode album = objectMapper.createObjectNode();
                    album.put("name", artistPage.getAlbums().get(i).getName());
                    ArrayNode songs = objectMapper.createArrayNode();
                    for (int j = 0; j < artistPage.getAlbums().get(i).getSongs().size(); j++) {
                        songs.add(artistPage.getAlbums().get(i).getSongs().get(j).getName());
                    }
                    album.putPOJO("songs", songs);
                    results.add(album);
                }
                out.put("result", results);
                return;
            }
        }
        out.put("message", command.getUsername() + " is not an artist.");
    }

    /**
     * Metoda care seteaza un user ca fiind pe "home", in cazul in care nu mai are un artist/host
     * selectat
     */
    public void setUserPageToHome(final Command command, final ArrayList<Selected> selected,
                                  final UserManagement users) {
        //daca utilizatorul are isSelected pe false, ii setez pagina de utilizator la home
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).getUser().equals(command.getUsername())) {
                if (!selected.get(i).isSelected()) {
                    for (int j = 0; j < users.getUsers().size(); j++) {
                        if (users.getUsers().get(j).getUsername().equals(command.getUsername())) {
                            if (users.getUsers().get(j).getPageType().equals("host")
                                    || users.getUsers().get(j).getPageType().equals("artist")) {
                                users.getUsers().get(j).setPageType("home");
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Metoda care seteaza un user ca fiind pe "host", in cazul in care are un host selectat
     */
    public void setUserPageToHost(final Command command,
                                  final ArrayList<SearchedSongsByUsers> searchData,
                                  final UserManagement users, final ArrayList<Selected> selected) {
        //daca utilizatorul are searchType "host", ii setez pagina de utilizator la host
        for (int i = 0; i < searchData.size(); i++) {
            if (searchData.get(i).getUser().equals(command.getUsername())) {
                if (searchData.get(i).getSearchType().equals("host")) {
                    for (int j = 0; j < users.getUsers().size(); j++) {
                        if (users.getUsers().get(j).getUsername().equals(command.getUsername())) {
                            users.getUsers().get(j).setPageType("host");
                            //caut numele entitatii selectate si o adaug in informatiile
                            // despre utilizator
                            for (int k = 0; k < selected.size(); k++) {
                                if (selected.get(k).getUser().equals(command.getUsername())) {
                                    users.getUsers().get(j)
                                            .setLocationName(selected.get(k).getEntity());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Metoda care seteaza un user ca fiind pe "artist", in cazul in care are un artist selectat
     */
    public void setUserPageToArtist(final Command command,
                                    final ArrayList<SearchedSongsByUsers> searchData,
                                    final UserManagement users,
                                    final ArrayList<Selected> selected) {
        //daca utilizatorul are searchType "artist", ii setez pagina de utilizator la artist
        for (int i = 0; i < searchData.size(); i++) {
            if (searchData.get(i).getUser().equals(command.getUsername())) {
                if (searchData.get(i).getSearchType().equals("artist")) {
                    for (int j = 0; j < users.getUsers().size(); j++) {
                        if (users.getUsers().get(j).getUsername().equals(command.getUsername())) {
                            users.getUsers().get(j).setPageType("artist");
                            //caut numele entitatii selectate si o adaug in informatiile
                            // despre utilizator
                            for (int k = 0; k < selected.size(); k++) {
                                if (selected.get(k).getUser().equals(command.getUsername())) {
                                    users.getUsers().get(j)
                                            .setLocationName(selected.get(k).getEntity());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Metoda care schimba pe ce pagina se afla un utilizator
     */
    public void changePage(final Command command, final ObjectNode out,
                           final ArrayList<UserDatabase> users,
                           final ArrayList<SearchedSongsByUsers> searched) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        //daca utilizatorul este pe getPageType "home" ii setez pagina de utilizator la
        // "likedContent"
        //si invers
        //verific daca utilizatorul este "user"
        //daca este "host" sau "artist" nu ii pot schimba pagina
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                //stergem ce avea userul la search
                for (int i = 0; i < searched.size(); i++) {
                    if (searched.get(i).getUser().equals(command.getUsername())) {
                        searched.get(i).setSearchType("none");
                    }
                }
                if (command.getNextPage().equals("Home")) {
                    user.setPageType("home");
                    out.put("message", command.getUsername() + " accessed Home successfully.");
                    return;
                }
                if (command.getNextPage().equals("LikedContent")) {
                    user.setPageType("likedContent");
                    out.put("message",
                            command.getUsername() + " accessed LikedContent successfully.");
                    return;
                }
                if (user.getPageType().equals("artist")) {
                    user.setPageType("home");
                    out.put("message", command.getUsername() + " accessed Home successfully.");
                    return;
                }
                if (user.getPageType().equals("host")) {
                    user.setPageType("home");
                    out.put("message", command.getUsername() + " accessed Home successfully.");
                    return;
                }

            }
        }
    }

    /**
     * Metoda care afiseaza pagina unui host
     */
    public boolean printHost(final Command command, final ObjectNode out,
                          final ArrayList<UserDatabase> users) {
        //caut utilizatorul un users
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(command.getUsername())) {
                String host = users.get(i).getLocationName();
                //caut pagina hostului si o printez
                for (int j = 0; j < hostPages.size(); j++) {
                    if (hostPages.get(j).getName().equals(host)) {
                        String message = "";
                        //printez podcasturile
                        //format podcasturi [podcast1:\n\t[episode1 - description1 ,
                        // episode2 - description2, ...], ...]
                        message = "Podcasts:\n\t[";
                        for (int k = 0; k < hostPages.get(j).getPodcasts().size(); k++) {
                            message = message + hostPages.get(j).getPodcasts().get(k).getName()
                                    + ":\n\t[";
                            for (int l = 0; l < hostPages.get(j).getPodcasts().get(k).getEpisodes()
                                    .size(); l++) {
                                message = message + hostPages.get(j).getPodcasts().get(k)
                                        .getEpisodes().get(l).getName() + " - " + hostPages.get(j)
                                        .getPodcasts().get(k).getEpisodes().get(l)
                                        .getDescription();
                                if (l != hostPages.get(j).getPodcasts().get(k).getEpisodes().size()
                                        - 1) {
                                    message = message + ", ";
                                }
                            }
                            message = message + "]\n";
                            if (k != hostPages.get(j).getPodcasts().size() - 1) {
                                message = message + ", ";
                            }
                        }
                        //printez anunturile
                        //format anunturi [anunt1 - anunt1descriprion, ...]
                        message = message + "]\n\n";
                        message = message + "Announcements:\n\t[";
                        for (int k = 0; k < hostPages.get(j).getAnnouncements().size(); k++) {
                            message =
                                    message + hostPages.get(j).getAnnouncements().get(k).getName()
                                            + ":\n\t" + hostPages.get(j).getAnnouncements().get(k)
                                            .getDescription();
                            if (k != hostPages.get(j).getAnnouncements().size() - 1) {
                                message = message + ", ";
                            }
                        }
                        message = message + "\n]";
                        out.put("message", message);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Metoda care afiseaza pagina unui artist
     */
    public boolean printArtist(final Command command, final ObjectNode out,
                          final ArrayList<UserDatabase> users) {
        //caut utilizatorul un users
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(command.getUsername())) {
                String artist = users.get(i).getLocationName();
                //caut pagina artistului si o printez
                for (int j = 0; j < artistPages.size(); j++) {
                    if (artistPages.get(j).getName().equals(artist)) {
                        String message = "";
                        //printez albumele asemanator cazului de mai sus
                        message = "Albums:\n\t[";
                        for (int k = 0; k < artistPages.get(j).getAlbums().size(); k++) {
                            message = message + artistPages.get(j).getAlbums().get(k).getName();
                            if (k != artistPages.get(j).getAlbums().size() - 1) {
                                message = message + ", ";
                            }
                        }
                        //printez merchurile
                        //format merch-uri [merch1 -
                        // merch1Price:\n\tmerch1Descriprion, ...]
                        message = message + "]\n\n";
                        message = message + "Merch:\n\t[";
                        for (int k = 0; k < artistPages.get(j).getMerch().size(); k++) {
                            message = message + artistPages.get(j).getMerch().get(k).getName()
                                    + " - " + artistPages.get(j).getMerch().get(k).getPrice()
                                    + ":\n\t" + artistPages.get(j).getMerch().get(k)
                                    .getDescription();
                            if (k != artistPages.get(j).getMerch().size() - 1) {
                                message = message + ", ";
                            }
                        }
                        //printez evenimentele
                        //format evenimente [event1 -
                        // event1Date:\n\tevent1descriprion, ...]
                        message = message + "]\n\n";
                        message = message + "Events:\n\t[";
                        for (int k = 0; k < artistPages.get(j).getEvents().size(); k++) {
                            message = message + artistPages.get(j).getEvents().get(k).getName()
                                    + " - " + artistPages.get(j).getEvents().get(k).getDate()
                                    + ":\n\t" + artistPages.get(j).getEvents().get(k)
                                    .getDescription();
                            if (k != artistPages.get(j).getEvents().size() - 1) {
                                message = message + ", ";
                            }
                        }
                        message = message + "]";
                        out.put("message", message);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Metoda care afiseaza pagina pe care se afla un utilizator
     */
    public void printCurrentPage(final Command command, final ObjectNode out,
                                 final ArrayList<UserDatabase> users,
                                 final ArrayList<LikesByUsername> likesByUsername,
                                 final ArrayList<PlayList> playlists, final LibraryInput library,
                                 final ArrayList<LikesBySong> likesBySongs) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                //daca utilizatorul este offline afisam eroare
                if (user.getConnectionStatus().equals("offline")) {
                    out.put("message", command.getUsername() + " is offline.");
                    return;
                }
                if (user.getPageType().equals("likedContent")) {
                    String message;
                    ArrayList<String> likedSongs = new ArrayList<>();
                    ArrayList<String> likedSongsAlbum = new ArrayList<>();
                    //cautam melodiile apreciate
                    for (int i = 0; i < likesByUsername.size(); i++) {
                        if (likesByUsername.get(i).getUsername().equals(command.getUsername())) {
                            for (int j = 0;
                                 j < likesByUsername.get(i).getLikedSongs().size(); j++) {
                                likedSongs.add(likesByUsername.get(i).getLikedSongs().get(j));
                                likedSongsAlbum.add(
                                        likesByUsername.get(i).getLikedSongsAlbum().get(j));
                            }
                        }
                    }
                    message = "Liked songs:\n\t[";
                    for (int i = 0; i < likedSongs.size(); i++) {
                        message = message + likedSongs.get(i);
                        //caut artistul melodiei in library
                        for (int j = 0; j < library.getSongs().size(); j++) {
                            if (library.getSongs().get(j).getName().equals(likedSongs.get(i))) {
                                if (library.getSongs().get(j).getAlbum()
                                        .equals(likedSongsAlbum.get(i)) || likedSongsAlbum.get(i)
                                        .equals("notAlbum")) {
                                    message = message + " - " + library.getSongs().get(j)
                                            .getArtist();
                                }
                            }
                        }
                        if (i != likedSongs.size() - 1) {
                            message = message + ", ";
                        }
                    }
                    message = message + "]" + "\n\n";
                    message = message + "Followed playlists:\n\t[";
                    //cautam playlisturile la care utilizatorul a dat follow
                    for (int i = 0; i < playlists.size(); i++) {
                        for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                            for (int k = 0;
                                 k < playlists.get(i).getPlaylists().get(j).getFollowers()
                                         .size(); k++) {
                                if (playlists.get(i).getPlaylists().get(j).getFollowers().get(k)
                                        .equals(command.getUsername())) {
                                    message = message + playlists.get(i).getPlaylists().get(j)
                                            .getName();
                                    message = message + " - " + playlists.get(i).getUsername();
                                }
                            }
                        }
                    }
                    out.put("message", message + "]");
                    return;
                }
                if (user.getPageType().equals("home")) {
                    String message;
                    ArrayList<String> likedSongs = new ArrayList<>();
                    //cautam melodiile apreciate
                    for (int i = 0; i < likesByUsername.size(); i++) {
                        if (likesByUsername.get(i).getUsername().equals(command.getUsername())) {
                            for (int j = 0;
                                 j < likesByUsername.get(i).getLikedSongs().size(); j++) {
                                likedSongs.add(likesByUsername.get(i).getLikedSongs().get(j));
                            }
                        }
                    }
                    message = "Liked songs:\n\t[";
                    //sortez likedSongs in ordinea like-urilor totale
                    //caut in likesBySong
                    ArrayList<Integer> nrOfLikes = new ArrayList<>();
                    ArrayList<Integer> index = new ArrayList<>();
                    for (int i = 0; i < likedSongs.size(); i++) {
                        for (int j = 0; j < likesBySongs.size(); j++) {
                            if (likesBySongs.get(j).getSongName().equals(likedSongs.get(i))) {
                                nrOfLikes.add(likesBySongs.get(j).getUsersThatLiked().size());
                                index.add(i);
                            }
                        }
                    }
                    //sortez likedSongs in ordinea like-urilor totale
                    for (int i = 0; i < likedSongs.size(); i++) {
                        for (int j = i + 1; j < likedSongs.size(); j++) {
                            if (nrOfLikes.get(i) < nrOfLikes.get(j)) {
                                Collections.swap(nrOfLikes, i, j);
                                Collections.swap(likedSongs, i, j);
                                Collections.swap(index, i, j);
                            }
                            //daca nrOfLikes este egal ordonez in functie de index
                            if (nrOfLikes.get(i).equals(nrOfLikes.get(j))) {
                                if (index.get(i) > index.get(j)) {
                                    Collections.swap(nrOfLikes, i, j);
                                    Collections.swap(likedSongs, i, j);
                                    Collections.swap(index, i, j);
                                }
                            }
                        }
                    }
                    for (int i = 0; i < likedSongs.size() && i < LEN_5; i++) {
                        message = message + likedSongs.get(i);
                        if (i != likedSongs.size() - 1 && i < LEN_4) {
                            message = message + ", ";
                        }
                    }
                    message = message + "]" + "\n\n";
                    message = message + "Followed playlists:\n\t[";
                    //cautam playlisturile la care utilizatorul a dat follow
                    for (int i = 0; i < playlists.size(); i++) {
                        for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                            for (int k = 0;
                                 k < playlists.get(i).getPlaylists().get(j).getFollowers()
                                         .size(); k++) {
                                if (playlists.get(i).getPlaylists().get(j).getFollowers().get(k)
                                        .equals(command.getUsername())) {
                                    message = message + playlists.get(i).getPlaylists().get(j)
                                            .getName();
                                }
                            }
                        }
                    }
                    out.put("message", message + "]");
                    return;
                }
                if (user.getPageType().equals("artist")) {
                    if (printArtist(command, out, users)) {
                        return;
                    }
                }
                if (user.getPageType().equals("host")) {
                    if (printHost(command, out, users)) {
                        return;
                    }
                }
            }
        }
        out.put("message", "The username " + command.getUsername() + " doesn't exist.");
    }
}
