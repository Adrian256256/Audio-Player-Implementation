package main.programInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import main.database.*;

import java.util.ArrayList;

public class Player extends StatusClass {
    private static final int TIME = 90;
    private static Player instance = null;

    public Player() {
    }

    /**
     * metoda pentru a creea un obiect de tip Player
     *
     * @return obiectul de tip Player
     */
    public static Player getInstance() {
        if (instance == null) {
            synchronized (Player.class) {
                if (instance == null) {
                    instance = new Player();
                }
            }
        }
        return instance;
    }

    /**
     * metoda ce adauga in lista de melodii un nou like dat de utilizator
     * metoda auxiliara pentru metoda "like"
     */
    public void likeAddToLikesBySong(final ArrayList<LikesBySong> likesBySong,
                                     final Command command, final ArrayList<Loaded> loadeds,
                                     final int i, final ArrayList<Status> statuses) {
        //nu uitam sa adaugam si in lista cu melodii si utilizatorii care le au apreciat
        //aceasta va fi folosita ulterior pentru statistici
        boolean okFound = false;
        for (int j = 0; j < likesBySong.size(); j++) {
            boolean okAlreadyLiked = false;
            if (loadeds.get(i).getTypeOfEntity().equals("song")) {
                if (likesBySong.get(j).getSongName().equals(loadeds.get(i).getEntityLoaded())) {
                    okFound = true;
                    for (int k = 0; k < likesBySong.get(j).getUsersThatLiked().size(); k++) {
                        if (likesBySong.get(j).getUsersThatLiked().get(k)
                                .equals(command.getUsername())) {
                            okAlreadyLiked = true;
                            likesBySong.get(j).getUsersThatLiked().remove(k);
                            if (likesBySong.get(j).getUsersThatLiked().size() == 0) {
                                likesBySong.remove(j);
                            }
                            break;
                        }
                    }
                    if (!okAlreadyLiked) {
                        likesBySong.get(j).getUsersThatLiked().add(command.getUsername());
                    }
                    break;
                }
            }
            if (loadeds.get(i).getTypeOfEntity().equals("playlist")) {
                String entity = "";
                for (int p = 0; p < statuses.size(); p++) {
                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                        entity = statuses.get(p).getStats().getName();
                    }
                }
                if (likesBySong.get(j).getSongName().equals(entity)) {
                    okFound = true;
                    for (int k = 0; k < likesBySong.get(j).getUsersThatLiked().size(); k++) {
                        if (likesBySong.get(j).getUsersThatLiked().get(k)
                                .equals(command.getUsername())) {
                            okAlreadyLiked = true;
                            likesBySong.get(j).getUsersThatLiked().remove(k);
                            if (likesBySong.get(j).getUsersThatLiked().size() == 0) {
                                likesBySong.remove(j);
                            }
                            break;
                        }
                    }
                    if (!okAlreadyLiked) {
                        likesBySong.get(j).getUsersThatLiked().add(command.getUsername());
                    }
                    break;
                }
            }
            if (loadeds.get(i).getTypeOfEntity().equals("album")) {
                String entity = "";
                for (int p = 0; p < statuses.size(); p++) {
                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                        entity = statuses.get(p).getStats().getName();
                    }
                }
                if (likesBySong.get(j).getSongName().equals(entity)) {
                    if (likesBySong.get(j).getSongAlbum().equals(statuses.get(i).getCurrentAlbum())
                            || likesBySong.get(j).getSongAlbum().equals("notAlbum")) {
                        okFound = true;
                        for (int k = 0; k < likesBySong.get(j).getUsersThatLiked().size(); k++) {
                            if (likesBySong.get(j).getUsersThatLiked().get(k)
                                    .equals(command.getUsername())) {
                                okAlreadyLiked = true;
                                likesBySong.get(j).getUsersThatLiked().remove(k);
                                if (likesBySong.get(j).getUsersThatLiked().size() == 0) {
                                    likesBySong.remove(j);
                                }
                                break;
                            }
                        }
                        if (!okAlreadyLiked) {
                            likesBySong.get(j).getUsersThatLiked().add(command.getUsername());
                        }
                        break;
                    }
                }
            }
        }
        if (!okFound) {
            LikesBySong newLike = new LikesBySong();
            if (loadeds.get(i).getTypeOfEntity().equals("song")) {
                newLike.setSongName(loadeds.get(i).getEntityLoaded());
                newLike.setSongAlbum("notAlbum");
            }
            if (loadeds.get(i).getTypeOfEntity().equals("album")) {
                String entity = "";
                String album = "";
                for (int p = 0; p < statuses.size(); p++) {
                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                        entity = statuses.get(p).getStats().getName();
                        album = statuses.get(p).getCurrentAlbum();
                    }
                }
                newLike.setSongName(entity);
                newLike.setSongAlbum(album);
            }
            if (loadeds.get(i).getTypeOfEntity().equals("playlist")) {
                String entity = "";
                for (int p = 0; p < statuses.size(); p++) {
                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                        entity = statuses.get(p).getStats().getName();
                    }
                }
                newLike.setSongName(entity);
                newLike.setSongAlbum("notAlbum");
            }
            newLike.getUsersThatLiked().add(command.getUsername());
            likesBySong.add(newLike);
        }
    }

    /**
     * metoda ce adauga primul like pentru un utilizator
     * metoda auxiliara pentru metoda "like"
     */
    public boolean firstLike(final ArrayList<LikesByUsername> likesByUsername,
                          final ArrayList<Loaded> loadeds, final int i, final Command command,
                          final ObjectNode out, final ArrayList<Status> statuses) {
        boolean okDone = false;
        if (loadeds.get(i).getTypeOfEntity().equals("playlist")) {
            String entity = "";
            for (int p = 0; p < statuses.size(); p++) {
                if (statuses.get(p).getUser().equals(command.getUsername())) {
                    entity = statuses.get(p).getStats().getName();
                }
            }
            LikesByUsername newLike = new LikesByUsername();
            newLike.setUsername(command.getUsername());
            newLike.getLikedSongs().add(entity);
            newLike.getLikedSongsAlbum().add("notAlbum");
            likesByUsername.add(newLike);
            okDone = true;
            out.put("message", "Like registered successfully.");
        }
        if (loadeds.get(i).getTypeOfEntity().equals("song")) {
            LikesByUsername newLike = new LikesByUsername();
            newLike.setUsername(command.getUsername());
            newLike.getLikedSongs().add(loadeds.get(i).getEntityLoaded());
            newLike.getLikedSongsAlbum().add("notAlbum");
            likesByUsername.add(newLike);
            okDone = true;
            out.put("message", "Like registered successfully.");
        }
        if (loadeds.get(i).getTypeOfEntity().equals("album")) {
            String entity = "";
            String album = "";
            for (int p = 0; p < statuses.size(); p++) {
                if (statuses.get(p).getUser().equals(command.getUsername())) {
                    entity = statuses.get(p).getStats().getName();
                    album = statuses.get(p).getCurrentAlbum();
                }
            }
            LikesByUsername newLike = new LikesByUsername();
            newLike.setUsername(command.getUsername());
            newLike.getLikedSongs().add(entity);
            //setez numele albumului
            newLike.getLikedSongsAlbum().add(album);
            likesByUsername.add(newLike);
            okDone = true;
            out.put("message", "Like registered successfully.");
        }
        return okDone;
    }
    /**
     * metoda ce adauga entitatea incarcata in acel moment de un utilizator in lista melodiilor
     * apreciate de acesta
     *
     * @param likesByUsername lista cu utilizatori si melodiile la care acestia au dat like
     * @param likesBySong     lista cu melodii si utilizatorii care au apreciat melodiile
     * @param command         comanda actuala
     * @param loadeds         lista cu tot ce este incarcat
     * @param out             "ObjectNode"-ul in care punem output-ul comenzii
     * @param statuses        lista cu statusurile utilizatorilor
     */
    public void like(final ArrayList<LikesByUsername> likesByUsername,
                     final ArrayList<LikesBySong> likesBySong, final Command command,
                     final ArrayList<Loaded> loadeds, final ObjectNode out,
                     final ArrayList<Status> statuses, final UserManagement users) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        for (int i = 0; i < users.getUsers().size(); i++) {
            if (users.getUsers().get(i).getUsername().equals(command.getUsername())) {
                //cautam utilizatorul care a dat search
                if (users.getUsers().get(i).getConnectionStatus().equals("offline")) {
                    //daca acesta este offline, nu se mai face nimic
                    out.put("command", command.getCommand());
                    out.put("user", command.getUsername());
                    out.put("timestamp", command.getTimestamp());
                    out.put("message", command.getUsername() + " is offline.");
                    return;
                }
            }
        }
        boolean okDone = false;
        //verificam daca utilizatorul are ceva incarcat pentru a da like, daca nu afisam eroare
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).isEmpty() || statuses.get(i).getStats().getName().equals("")) {
                    out.put("message", "Please load a source before liking or unliking.");
                    return;
                }
            }
        }
        for (int i = 0; i < loadeds.size(); i++) {
            if (command.getUsername().equals(loadeds.get(i).getUser())) {
                if (!loadeds.get(i).isLoaded()) {
                    out.put("message", "Please load a source before liking or unliking.");
                    return;
                }
                //verificam daca utilizatorul asculta un playlist sau o melodie
                //daca asculta podcast afisam eroare
                if (!loadeds.get(i).getTypeOfEntity().equals("song") && !loadeds.get(i)
                        .getTypeOfEntity().equals("playlist") && !loadeds.get(i).getTypeOfEntity()
                        .equals("album")) {
                    out.put("message", "Loaded source is not a song.");
                    return;
                }
                //initial lista cu utilizatori si melodiile pe care ei le au apreciat este goala
                //daca nu gasim utilizatorul in aceasta lista, il adaugam
                boolean okFound = false;
                for (int j = 0; j < likesByUsername.size(); j++) {
                    boolean okAlreadyLiked = false;
                    if (likesByUsername.get(j).getUsername().equals(command.getUsername())) {
                        okFound = true;
                        for (int k = 0; k < likesByUsername.get(j).getLikedSongs().size(); k++) {
                            if (loadeds.get(i).getTypeOfEntity().equals("playlist")) {
                                String entity = "";
                                for (int p = 0; p < statuses.size(); p++) {
                                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                                        entity = statuses.get(p).getStats().getName();
                                    }
                                }
                                if (likesByUsername.get(j).getLikedSongs().get(k).equals(entity)) {
                                    okAlreadyLiked = true;
                                    likesByUsername.get(j).getLikedSongs().remove(k);
                                    likesByUsername.get(j).getLikedSongsAlbum().remove(k);
                                    okDone = true;
                                    out.put("message", "Unlike registered successfully.");
                                }
                            }
                            if (loadeds.get(i).getTypeOfEntity().equals("album")) {
                                String entity = "";
                                for (int p = 0; p < statuses.size(); p++) {
                                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                                        entity = statuses.get(p).getStats().getName();
                                    }
                                }
                                if (likesByUsername.get(j).getLikedSongs().get(k).equals(entity)) {
                                    okAlreadyLiked = true;
                                    likesByUsername.get(j).getLikedSongs().remove(k);
                                    likesByUsername.get(j).getLikedSongsAlbum().remove(k);
                                    okDone = true;
                                    out.put("message", "Unlike registered successfully.");
                                }
                            }
                            if (loadeds.get(i).getTypeOfEntity().equals("song")) {
                                if (likesByUsername.get(j).getLikedSongs().get(k)
                                        .equals(loadeds.get(i).getEntityLoaded())) {
                                    okAlreadyLiked = true;
                                    likesByUsername.get(j).getLikedSongs().remove(k);
                                    likesByUsername.get(j).getLikedSongsAlbum().remove(k);
                                    okDone = true;
                                    out.put("message", "Unlike registered successfully.");
                                }
                            }
                        }
                        if (!okAlreadyLiked) {
                            if (loadeds.get(i).getTypeOfEntity().equals("playlist")) {
                                okDone = true;
                                String entity = "";
                                for (int p = 0; p < statuses.size(); p++) {
                                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                                        entity = statuses.get(p).getStats().getName();
                                    }
                                }
                                likesByUsername.get(j).getLikedSongs().add(entity);
                                likesByUsername.get(j).getLikedSongsAlbum().add("notAlbum");
                                out.put("message", "Like registered successfully.");
                            }
                            if (loadeds.get(i).getTypeOfEntity().equals("song")) {
                                likesByUsername.get(j).getLikedSongs()
                                        .add(loadeds.get(i).getEntityLoaded());
                                likesByUsername.get(j).getLikedSongsAlbum().add("notAlbum");
                                okDone = true;
                                out.put("message", "Like registered successfully.");
                            }
                            if (loadeds.get(i).getTypeOfEntity().equals("album")) {
                                String entity = "";
                                String album = "";
                                for (int p = 0; p < statuses.size(); p++) {
                                    if (statuses.get(p).getUser().equals(command.getUsername())) {
                                        entity = statuses.get(p).getStats().getName();
                                        album = statuses.get(p).getCurrentAlbum();
                                    }
                                }
                                likesByUsername.get(j).getLikedSongs().add(entity);
                                //setez numele albumului
                                likesByUsername.get(j).getLikedSongsAlbum().add(album);
                                okDone = true;
                                out.put("message", "Like registered successfully.");
                            }
                        }
                    }

                }
                //nu am gasit utilizatorul in lista, il adaugam
                if (!okFound) {
                    okDone = firstLike(likesByUsername, loadeds, i, command, out, statuses);
                }
                likeAddToLikesBySong(likesBySong, command, loadeds, i, statuses);
            }
        }
        if (!okDone) {
            out.put("message", "Please load a source before liking or unliking.");
        }
    }

    /**
     * metoda ce pune pe pauza sau pe play ceea ce asculta utilizatorul in acel moment
     *
     * @param statuses  lista cu statusurile utilizatorilor
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param command   comanda actuala
     */
    public void playPause(final ArrayList<Status> statuses, final ObjectNode out,
                          final Command command) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                //verificam daca avem ceva incarcat
                statuses.get(i).setTimestamp(command.getTimestamp());
                if (statuses.get(i).getStats().getName().equals("")) {
                    out.put("message",
                            "Please load a source before attempting to pause or resume playback.");
                    return;
                }
                //setam playerul pe pauza sau play
                if (statuses.get(i).getStats().isPaused()) {
                    statuses.get(i).getStats().setPaused(false);
                    out.put("message", "Playback resumed successfully.");
                } else if (!statuses.get(i).getStats().isPaused()) {
                    statuses.get(i).getStats().setPaused(true);
                    out.put("message", "Playback paused successfully.");
                }

            }
        }
    }

    /**
     * metoda ce incarca un album selectat anterior
     * metoda auxiliara pentru metoda load
     */
    public void loadAlbum(final ArrayList<Selected> selected, final ArrayList<Status> statuses,
                          final LibraryInput library, final int j, final int k, final Stats stats,
                          final ArrayList<Loaded> loadeds, final int i,
                          final PageManagementHub pages) {
        statuses.get(k).setCurrentAlbum(selected.get(i).getEntity());
        //iteram prin albumele din pages si gasim albumul pe care il ascultam
        for (int l = 0; l < pages.getArtistPages().size(); l++) {
            for (int m = 0; m < pages.getArtistPages().get(l).getAlbums().size(); m++) {
                if (pages.getArtistPages().get(l).getAlbums().get(m).getName()
                        .equals(selected.get(i).getEntity())) {
                    //verific daca prima melodie din album e firstSong din selected
                    if (pages.getArtistPages().get(l).getAlbums().get(m).getSongs().get(0)
                            .getName().equals(selected.get(i).getFirstSong())) {
                        //trec in loadeds acest first song, iterez prin loadeds
                        boolean okLoaded = false;
                        for (int n = 0; n < loadeds.size(); n++) {
                            if (loadeds.get(n).getUser().equals(selected.get(i).getUser())) {
                                okLoaded = true;
                                loadeds.get(n).setLoaded(true);
                                loadeds.get(n).setSongTimestamp(selected.get(i).getTimestamp());
                                loadeds.get(n).setFirstSong(selected.get(i).getFirstSong());
                            }
                        }
                        if (!okLoaded) {
                            //daca nu exista lodeds pentru acest user, il creez
                            Loaded loaded = new Loaded();
                            loaded.setLoaded(true);
                            loaded.setSongTimestamp(selected.get(i).getTimestamp());
                            loaded.setFirstSong(selected.get(i).getFirstSong());
                            loaded.setUser(selected.get(i).getUser());
                            loadeds.add(loaded);
                        }
                        //am gasit albumul
                        // setam prima melodie din abum ca fiind cea ascultata
                        statuses.get(k).getStats().setName(
                                pages.getArtistPages().get(l).getAlbums().get(m).getSongs().get(0)
                                        .getName());
                    }
                }
            }
        }
        stats.setPaused(false);
        stats.setShuffle(false);
        stats.setRepeat("No Repeat");
        Integer time = null;
        ArrayList<SongInput> songs = library.getSongs();
        for (int p = 0; p < songs.size(); p++) {
            if (songs.get(p).getName().equals(statuses.get(k).getStats().getName())) {
                time = songs.get(p).getDuration();
            }
        }
        stats.setRemainedTime(time);
    }

    /**
     * metoda ce incarca o melodie selectata anterior
     * metoda auxiliara pentru metoda load
     */
    public void loadSong(final ArrayList<Selected> selected, final ArrayList<Status> statuses,
                         final LibraryInput library, final int j, final int k, final Stats stats,
                         final ArrayList<Loaded> loadeds, final int i) {
        statuses.get(k).setSongDuration(selected.get(i).getTimestamp());
        loadeds.get(j).setSongTimestamp(selected.get(i).getTimestamp());
        stats.setName(selected.get(i).getEntity());
        stats.setPaused(false);
        stats.setShuffle(false);
        stats.setRepeat("No Repeat");
        Integer time = null;
        ArrayList<SongInput> songs = library.getSongs();
        for (int p = 0; p < songs.size(); p++) {
            if (songs.get(p).getName().equals(selected.get(i).getEntity())) {
                time = songs.get(p).getDuration();
            }
        }
        stats.setRemainedTime(time);
    }

    /**
     * metoda ce incarca un podcast selectata anterior
     * metoda auxiliara pentru metoda load
     */
    public void loadPodcast(final ArrayList<Selected> selected, final ArrayList<Status> statuses,
                            final LibraryInput library, final int i, final int k,
                            final Stats stats) {
        for (int l = 0; l < library.getPodcasts().size(); l++) {
            if (library.getPodcasts().get(l).getName().equals(selected.get(i).getEntity())) {
                statuses.get(i).setCurrentEpisode(selected.get(i).getEntity());
                //am gasit poodcastul in library, avem informatiile despre el
                //cautam sa vedem daca userul s a mai uitat vreodata la acest podcast
                boolean podcastAlreadyWatched = false;
                for (int f = 0; f < statuses.get(k).getPodcastsWatcheds().size(); f++) {
                    if (statuses.get(k).getPodcastsWatcheds().get(f).getPodcastName()
                            .equals(selected.get(i).getEntity())) {
                        //am gasit faptul ca acest user s-a mai uitat in trecut la acest podcast
                        podcastAlreadyWatched = true;
                        stats.setName(
                                statuses.get(k).getPodcastsWatcheds().get(f).getLastEpisode());
                        stats.setRemainedTime(
                                statuses.get(k).getPodcastsWatcheds().get(f).getLastTimestamp());
                        stats.setPaused(false);
                        stats.setShuffle(false);
                        stats.setRepeat("No Repeat");
                    }
                }
                if (!podcastAlreadyWatched) {
                    //userul nu s a mai uitat niciodata la acest podcast
                    //dam load la primul episod din podcast
                    stats.setName(library.getPodcasts().get(l).getEpisodes().get(0).getName());
                    stats.setRemainedTime(
                            library.getPodcasts().get(l).getEpisodes().get(0).getDuration());
                    stats.setPaused(false);
                    stats.setShuffle(false);
                    stats.setRepeat("No Repeat");
                }
            }
        }
    }

    /**
     * metoda ce incarca un playlist selectat anterior
     * metoda auxiliara pentru metoda load
     */
    public void loadPlaylist(final ArrayList<Selected> selected, final ArrayList<Status> statuses,
                             final int i, final Stats stats, final ArrayList<PlayList> playlists) {
        statuses.get(i).setCurrentPlaylist(selected.get(i).getEntity());
        statuses.get(i).setTypeOfListening("playlist");
        for (int l = 0; l < playlists.size(); l++) {
            for (int m = 0; m < playlists.get(l).getPlaylists().size(); m++) {
                if (playlists.get(l).getPlaylists().get(m).getName()
                        .equals(selected.get(i).getEntity())) {
                    //am gasit playlistul
                    stats.setName(playlists.get(l).getPlaylists().get(m).getSongs().get(0));
                    stats.setRemainedTime(playlists.get(l).getPlaylists().get(m)
                                    .getSongTimestamps().get(0));
                    stats.setPaused(false);
                    stats.setShuffle(false);
                    stats.setRepeat("No Repeat");
                }
            }
        }
    }

    /**
     * metoda ce salveaza informatii despre un eventual podcast ascultat de utilizator
     * metoda auxiliara pentru metoda load
     */
    public void savePodcast(final ArrayList<Status> statuses, final Command command) {
        for (int k = 0; k < statuses.size(); k++) {
            //cautam statusul userului care a dat comanda
            if (statuses.get(k).getUser().equals(command.getUsername())) {
                if (statuses.get(k).getTypeOfListening().equals("podcast")) {
                    int time;
                    if (!statuses.get(k).getStats().isPaused()) {
                        time = statuses.get(k).getStats().getRemainedTime() - (
                                command.getTimestamp() - statuses.get(k).getTimestamp());
                    } else {
                        time = statuses.get(k).getStats().getRemainedTime();
                    }
                    boolean okAlreadyHerePodcast = false;
                    for (int i = 0; i < statuses.get(k).getPodcastsWatcheds().size(); i++) {
                        if (statuses.get(k).getPodcastsWatcheds().get(i).getPodcastName()
                                .equals(statuses.get(k).getCurrentEpisode())) {
                            okAlreadyHerePodcast = true;
                            break;
                        }
                    }
                    if (!okAlreadyHerePodcast) {
                        PodcastsWatched newPodcastWatched = new PodcastsWatched();
                        newPodcastWatched.setPodcastName(statuses.get(k).getCurrentEpisode());
                        newPodcastWatched.setLastEpisode(statuses.get(k).getStats().getName());
                        newPodcastWatched.setLastTimestamp(time);
                        statuses.get(k).getPodcastsWatcheds().add(newPodcastWatched);
                    }
                }
            }
        }
    }

    /**
     * metoda ce verifica daca avem ceva loaded
     * metoda auxiliara pentru metoda load
     *
     * @return true pentru succes, false pentru esec
     */
    public boolean loadVerify(final ArrayList<Selected> selected, final Command command,
                          final ObjectNode out) {
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).getUser().equals(command.getUsername())) {
                if (!selected.get(i).isSelected()) {
                    out.put("message", "Please select a source before attempting to load.");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * metoda ce incarca o melodie selectata anterior
     *
     * @param selected  lista care contine entitatile selectate de utilizatori
     * @param command   comanda actuala
     * @param loadeds   lista cu tot ce este incarcat
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param statuses  lista cu statusurile utilizatorilor
     * @param library   clasa care contine tot ce se afla in library
     * @param playlists lista cu playlisturile utilizatorilor
     */
    public void load(final ArrayList<Selected> selected, final Command command,
                     final ArrayList<Loaded> loadeds, final ObjectNode out,
                     final ArrayList<Status> statuses, final LibraryInput library,
                     final ArrayList<PlayList> playlists, final PageManagementHub pages) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        boolean hasBeenLoaded = false;
        if (!loadVerify(selected, command, out)) {
            return;
        }
        //vrem sa salvam datele despre un eventual podcast pe care il asculta userul
        savePodcast(statuses, command);
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).getUser().equals(command.getUsername())) {
                boolean okLoaded = false;
                for (int j = 0; j < loadeds.size(); j++) {
                    if (loadeds.get(j).getUser().equals(command.getUsername())) {
                        loadeds.get(j).setLoaded(true);
                        loadeds.get(j).setEntityLoaded(selected.get(i).getEntity());
                        loadeds.get(j).setTypeOfEntity(selected.get(i).getTypeOfEntity());
                        okLoaded = true;
                        out.put("message", "Playback loaded successfully.");
                        hasBeenLoaded = true;
                        selected.get(i).setSelected(false);
                        for (int k = 0; k < statuses.size(); k++) {
                            if (statuses.get(k).getUser().equals(command.getUsername())) {
                                statuses.get(k).setTimestamp(command.getTimestamp());
                                statuses.get(k)
                                        .setTypeOfListening(selected.get(i).getTypeOfEntity());
                                statuses.get(k).setEmpty(false);
                                Stats stats = statuses.get(i).getStats();
                                if (selected.get(i).getTypeOfEntity().equals("playlist")) {
                                    loadPlaylist(selected, statuses, i, stats, playlists);
                                }
                                if (selected.get(i).getTypeOfEntity().equals("podcast")) {
                                    loadPodcast(selected, statuses, library, i, k, stats);
                                }
                                if (selected.get(i).getTypeOfEntity().equals("song")) {
                                    loadSong(selected, statuses, library, j, k, stats, loadeds, i);
                                }
                                if (selected.get(i).getTypeOfEntity().equals("album")) {
                                    loadAlbum(selected, statuses, library, j, k, stats, loadeds, i,
                                            pages);
                                }
                            }
                        }
                        break;
                    }
                }
                //nu avem informatii despre utilizator cu privire la "load"-uri trecute
                //creem un profil pentru incarcarile utilizatorului in baza de date
                if (!okLoaded) {
                    Loaded load = new Loaded();
                    load.setLoaded(true);
                    load.setUser(command.getUsername());
                    load.setEntityLoaded(selected.get(i).getEntity());
                    load.setTypeOfEntity(selected.get(i).getTypeOfEntity());
                    hasBeenLoaded = true;
                    out.put("message", "Playback loaded successfully.");
                    Status newStatus = new Status();
                    newStatus.setUser(command.getUsername());
                    newStatus.setTimestamp(command.getTimestamp());
                    newStatus.setEmpty(false);
                    newStatus.setTypeOfListening(selected.get(i).getTypeOfEntity());
                    Stats stats = new Stats();
                    stats.setName(selected.get(i).getEntity());
                    if (selected.get(i).getTypeOfEntity().equals("playlist")) {
                        newStatus.setCurrentPlaylist(selected.get(i).getEntity());
                        newStatus.setTypeOfListening("playlist");
                        for (int l = 0; l < playlists.size(); l++) {
                            for (int m = 0; m < playlists.get(l).getPlaylists().size(); m++) {
                                if (playlists.get(l).getPlaylists().get(m).getName()
                                        .equals(selected.get(i).getEntity())) {
                                    //am gasit playlistul
                                    stats.setName(playlists.get(l).getPlaylists().get(m).getSongs()
                                            .get(0));
                                    stats.setRemainedTime(playlists.get(l).getPlaylists().get(m)
                                            .getSongTimestamps().get(0));
                                    stats.setPaused(false);
                                    stats.setShuffle(false);
                                    stats.setRepeat("No Repeat");
                                }
                            }
                        }
                    }
                    if (selected.get(i).getTypeOfEntity().equals("album")) {
                        //cautam albumul in pages
                        newStatus.setCurrentAlbum(selected.get(i).getEntity());
                        //setam loaded.setFirstSong
                        load.setFirstSong(selected.get(i).getFirstSong());
                        for (int l = 0; l < pages.getArtistPages().size(); l++) {
                            for (int m = 0;
                                 m < pages.getArtistPages().get(l).getAlbums().size(); m++) {
                                if (pages.getArtistPages().get(l).getAlbums().get(m).getName()
                                        .equals(selected.get(i).getEntity())) {
                                    //am gasit albumul
                                    // setam prima melodie din abum ca fiind cea ascultata
                                    stats.setName(pages.getArtistPages().get(l).getAlbums().get(m)
                                            .getSongs().get(0).getName());
                                    stats.setRemainedTime(
                                            pages.getArtistPages().get(l).getAlbums().get(m)
                                                    .getSongs().get(0).getDuration());
                                }
                            }
                        }
                    }
                    stats.setPaused(false);
                    stats.setShuffle(false);
                    stats.setRepeat("No Repeat");
                    if (selected.get(i).getTypeOfEntity().equals("podcast")) {
                        newStatus.setCurrentEpisode(selected.get(i).getEntity());
                        //cautam podcastul il library
                        //stim ca userul nu s a mai uitat la nici un podcast, clar
                        for (int k = 0; k < library.getPodcasts().size(); k++) {
                            if (library.getPodcasts().get(k).getName()
                                    .equals(selected.get(i).getEntity())) {
                                stats.setName(library.getPodcasts().get(k).getEpisodes().get(0)
                                        .getName());
                                stats.setRemainedTime(
                                        library.getPodcasts().get(k).getEpisodes().get(0)
                                                .getDuration());
                            }
                        }
                    }
                    if (selected.get(i).getTypeOfEntity().equals("song")) {
                        load.setSongTimestamp(selected.get(i).getTimestamp());
                        newStatus.setSongDuration(selected.get(i).getTimestamp());
                        int time = 0;
                        ArrayList<SongInput> songs = library.getSongs();
                        for (int p = 0; p < songs.size(); p++) {
                            if (songs.get(p).getName().equals(selected.get(i).getEntity())) {
                                time = songs.get(p).getDuration();
                            }
                        }
                        stats.setRemainedTime(time);
                    }
                    newStatus.setStats(stats);
                    statuses.add(newStatus);
                    loadeds.add(load);
                    break;
                }
            }
        }
        if (!hasBeenLoaded) {
            out.put("message", "Please select a source before attempting to load.");
        }
    }

    /**
     * metoda ce porneste sau opreste functia de repeat
     *
     * @param command  comanda actuala
     * @param out      "ObjectNode"-ul in care punem output-ul comenzii
     * @param statuses lista cu statusurile utilizatorilor
     * @param loadeds  lista cu tot ce este incarcat
     */
    public void repeat(final Command command, final ObjectNode out,
                       final ArrayList<Status> statuses, final ArrayList<Loaded> loadeds) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        boolean okFound = false;
        //verificam daca utilizatorul are ceva incarcat, daca nu afisam eroare
        for (int i = 0; i < loadeds.size(); i++) {
            if (loadeds.get(i).getUser().equals(command.getUsername())) {
                okFound = true;
                if (!loadeds.get(i).isLoaded()) {
                    out.put("message", "Please load a source before setting the repeat status.");
                    return;
                }
            }
        }
        if (!okFound) {
            out.put("message", "Please load a source before setting the repeat status.");
            return;
        }
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).getStats().getName().equals("")) {
                    out.put("message", "Please load a source before setting the repeat status.");
                    return;
                }
            }
        }
        //verificam starea repeat-ului curent si modificam in functie de aceasta
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).getTypeOfListening().equals("song")) {
                    if (statuses.get(i).getStats().getRepeat().equals("No Repeat")) {
                        statuses.get(i).getStats().setRepeat("Repeat Once");
                        out.put("message", "Repeat mode changed to repeat once.");
                        break;
                    }
                    if (statuses.get(i).getStats().getRepeat().equals("Repeat Once")) {
                        statuses.get(i).getStats().setRepeat("Repeat Infinite");
                        out.put("message", "Repeat mode changed to repeat infinite.");
                        break;
                    }
                    if (statuses.get(i).getStats().getRepeat().equals("Repeat Infinite")) {
                        statuses.get(i).getStats().setRepeat("No Repeat");
                        out.put("message", "Repeat mode changed to no repeat.");
                        break;
                    }
                }
                if (statuses.get(i).getTypeOfListening().equals("playlist")) {
                    if (statuses.get(i).getStats().getRepeat().equals("No Repeat")) {
                        statuses.get(i).getStats().setRepeat("Repeat All");
                        out.put("message", "Repeat mode changed to repeat all.");
                        break;
                    }
                    if (statuses.get(i).getStats().getRepeat().equals("Repeat All")) {
                        statuses.get(i).getStats().setRepeat("Repeat Current Song");
                        out.put("message", "Repeat mode changed to repeat current song.");
                        break;
                    }
                    if (statuses.get(i).getStats().getRepeat().equals("Repeat Current Song")) {
                        statuses.get(i).getStats().setRepeat("No Repeat");
                        out.put("message", "Repeat mode changed to no repeat.");
                        break;
                    }
                }
            }
        }
    }

    /**
     * metoda ce porneste sau opreste functia de shuffle
     *
     * @param command  comanda actuala
     * @param out      "ObjectNode"-ul in care punem output-ul comenzii
     * @param statuses lista cu statusurile utilizatorilor
     * @param loadeds  lista cu tot ce este incarcat
     */
    public void shuffle(final Command command, final ObjectNode out,
                        final ArrayList<Status> statuses, final ArrayList<Loaded> loadeds) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        boolean okFound = false;
        //verificam daca utilizatorul are ceva incarcat, daca nu afisam eroare
        for (int i = 0; i < loadeds.size(); i++) {
            if (loadeds.get(i).getUser().equals(command.getUsername())) {
                okFound = true;
                if (!loadeds.get(i).isLoaded()) {
                    out.put("message", "Please load a source before using the shuffle function.");
                    return;
                }
            }
        }
        if (!okFound) {
            out.put("message", "Please load a source before using the shuffle function.");
            return;
        }
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).getStats().getName().equals("")) {
                    out.put("message", "Please load a source before using the shuffle function.");
                    return;
                }
            }
        }
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (!statuses.get(i).getTypeOfListening().equals("playlist") && !statuses.get(i)
                        .getTypeOfListening().equals("album")) {
                    out.put("message", "The loaded source is not a playlist or an album.");
                    return;
                }
            }
        }
        //schimbam starea de shuffle in functie de cea precedenta
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).getStats().isShuffle()) {
                    statuses.get(i).getStats().setShuffle(false);
                    out.put("message", "Shuffle function deactivated successfully.");
                } else {
                    statuses.get(i).setSeed(command.getSeed());
                    statuses.get(i).getStats().setShuffle(true);
                    statuses.get(i).setSeed(command.getSeed());
                    out.put("message", "Shuffle function activated successfully.");
                }
            }
        }
    }


    /**
     * @param statuses lista cu statusurile utilizatorilor
     * @param out      "ObjectNode"-ul in care punem output-ul comenzii
     * @param command  comanda actuala
     * @param library  clasa care contine tot ce se afla in library
     */
    public void forward(final ArrayList<Status> statuses, final ObjectNode out,
                        final Command command, final LibraryInput library) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).isEmpty()) {
                    out.put("message", "Please load a source before attempting to forward.");
                    return;
                }
                Stats stats = statuses.get(i).getStats();
                if (statuses.get(i).getTypeOfListening().equals("playlist")) {
                    out.put("message", "The loaded source is not a podcast.");
                }
                if (statuses.get(i).getTypeOfListening().equals("song")) {
                    out.put("message", "The loaded source is not a podcast.");
                }
                if (statuses.get(i).getTypeOfListening().equals("album")) {
                    out.put("message", "The loaded source is not a podcast.");
                }
                if (statuses.get(i).getTypeOfListening().equals("podcast")) {
                    if (!stats.isPaused()) {
                        Integer podcastIndex = null;
                        Integer episodeIndex = null;
                        for (int j = 0; j < library.getPodcasts().size(); j++) {
                            if (library.getPodcasts().get(j).getName()
                                    .equals(statuses.get(i).getCurrentEpisode())) {
                                //am gasit podcastul, urmeaza sa cautam episodul
                                podcastIndex = j;
                                break;
                            }
                        }
                        for (int j = 0; j < library.getPodcasts().get(podcastIndex).getEpisodes()
                                .size(); j++) {
                            if (library.getPodcasts().get(podcastIndex).getEpisodes().get(j)
                                    .getName().equals(statuses.get(i).getStats().getName())) {
                                //am gasit episodul
                                episodeIndex = j;
                                break;
                            }
                        }
                        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                                .getTimestamp();
                        while (x < 0) {
                            episodeIndex++;
                            x += library.getPodcasts().get(podcastIndex).getEpisodes()
                                    .get(episodeIndex).getDuration();
                            statuses.get(i).getStats().setName(
                                    library.getPodcasts().get(podcastIndex).getEpisodes()
                                            .get(episodeIndex).getName());
                        }
                        stats.setRemainedTime(x);
                        if (x - TIME > 0) {
                            stats.setRemainedTime(x - TIME);
                            out.put("message", "Skipped forward successfully.");
                        } else {
                            for (int j = 0; j < library.getPodcasts().size(); j++) {
                                //caut podcastul curent
                                if (statuses.get(i).getCurrentEpisode()
                                        .equals(library.getPodcasts().get(j).getName())) {
                                    for (int k = 0; k < library.getPodcasts().get(j).getEpisodes()
                                            .size(); k++) {
                                        if (library.getPodcasts().get(j).getEpisodes().get(k)
                                                .getName()
                                                .equals(statuses.get(i).getStats().getName())) {
                                            if (k + 1 == library.getPodcasts().get(j).getEpisodes()
                                                    .size()) {
                                                statuses.get(i)
                                                        .setTimestamp(command.getTimestamp());
                                                stats.setName("");
                                                stats.setRemainedTime(0);
                                                stats.setPaused(true);
                                                out.put("message",
                                                        "Skipped forward successfully.");
                                                return;
                                            }
                                            stats.setName(
                                                    library.getPodcasts().get(j).getEpisodes()
                                                            .get(k + 1).getName());
                                            stats.setRemainedTime(
                                                    library.getPodcasts().get(j).getEpisodes()
                                                            .get(k + 1).getDuration());
                                            break;
                                        }
                                    }
                                }
                            }
                            statuses.get(i).setTimestamp(command.getTimestamp());
                            out.put("message", "Skipped forward successfully.");
                        }
                    }
                    statuses.get(i).setTimestamp(command.getTimestamp());
                } else {
                    out.put("message", "The loaded source is not a podcast.");
                }
            }
        }
    }

    /**
     * @param statuses lista cu statusurile utilizatorilor
     * @param out      "ObjectNode"-ul in care punem output-ul comenzii
     * @param command  comanda actuala
     * @param library  clasa care contine tot ce se afla in library
     */
    public void backward(final ArrayList<Status> statuses, final ObjectNode out,
                         final Command command, final LibraryInput library) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).isEmpty()) {
                    out.put("message", "Please load a source before attempting to forward.");
                    return;
                }
                Stats stats = statuses.get(i).getStats();
                if (statuses.get(i).getTypeOfListening().equals("playlist")) {
                    out.put("message", "The loaded source is not a podcast.");
                }
                if (statuses.get(i).getTypeOfListening().equals("song")) {
                    out.put("message", "The loaded source is not a podcast.");
                }
                if (statuses.get(i).getTypeOfListening().equals("podcast")) {
                    if (!stats.isPaused()) {
                        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                                .getTimestamp();
                        if (x < 0) {
                            x = 0;
                            statuses.get(i).getStats().setName("");
                            statuses.get(i).getStats().setPaused(true);
                        }
                        stats.setRemainedTime(x + TIME);
                        out.put("message", "Rewound successfully.");
                    }
                }
                statuses.get(i).setTimestamp(command.getTimestamp());
            }
            return;
        }
    }

    /**
     * @param selected  lista care contine entitatile selectate de utilizatori
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param command   comanda actuala
     * @param playlists clasa care contine tot ce se afla in library
     */
    public void follow(final ArrayList<Selected> selected, final ObjectNode out,
                       final Command command, final ArrayList<PlayList> playlists) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        Selected sel = new Selected();
        boolean isSelected = false;
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).getUser().equals(command.getUsername())) {
                isSelected = true;
                sel = selected.get(i);
                if (!sel.isSelected()) {
                    out.put("message", "Please select a source before following or unfollowing.");
                    return;
                }
                break;
            }
        }
        if (!isSelected) {
            out.put("message", "Please select a source before following or unfollowing.");
            return;
        }
        if (!sel.getTypeOfEntity().equals("playlist")) {
            out.put("message", "The selected source is not a playlist.");
            return;
        }
        for (int i = 0; i < playlists.size(); i++) {
            for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                if (playlists.get(i).getPlaylists().get(j).getName().equals(sel.getEntity())) {
                    if (playlists.get(i).getUsername().equals(command.getUsername())) {
                        out.put("message", "You cannot follow or unfollow your own playlist.");
                        return;
                    }
                    boolean alreadyFollowed = false;
                    for (int k = 0;
                         k < playlists.get(i).getPlaylists().get(j).getFollowers().size(); k++) {
                        if (playlists.get(i).getPlaylists().get(j).getFollowers().get(k)
                                .equals(command.getUsername())) {
                            alreadyFollowed = true;
                            playlists.get(i).getPlaylists().get(j).getFollowers().remove(k);
                            out.put("message", "Playlist unfollowed successfully.");
                            return;
                        }
                    }
                    if (!alreadyFollowed) {
                        playlists.get(i).getPlaylists().get(j).getFollowers()
                                .add(command.getUsername());
                        out.put("message", "Playlist followed successfully.");
                    }
                }
            }
        }
    }

}
