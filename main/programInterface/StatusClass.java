package main.programInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import main.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class StatusClass extends PlayListClass {

    private static StatusClass instance = null;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    public StatusClass() {
    }

    /**
     * metoda pentru a creea un obiect de tip StatusClass
     *
     * @return obiectul de tip StatusClass
     */
    public static StatusClass getInstance() {
        if (instance == null) {
            synchronized (StatusClass.class) {
                if (instance == null) {
                    instance = new StatusClass();
                }
            }
        }
        return instance;
    }

    /**
     * metoda auxiliara pentru metoda "statusPlaylist"
     * caz pentru no repeat
     * @return
     */
    public int statusPlaylistNoRepeat(final ArrayList<Status> statuses, final LibraryInput library,
                                      final ArrayList<PlayList> playlists, final int i,
                                      final int indexPlaylistUsers,
                                      final int indexPlaylistPlaylists,
                                      final int indexPlaylistSongIn, final int durationIn,
                                      final int xIn, final int doneIn, final Stats stats,
                                      final ArrayList<Loaded> loaded) {
        int duration = durationIn;
        int x = xIn;
        int done = doneIn;
        int indexPlaylistSong = indexPlaylistSongIn;
        //avem cazuri separate, in functie de faptul ca utilizatorul poate avea
        if (!statuses.get(i).getStats().isShuffle()) { //modul shuffle inactiv
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                }
            }
            done = 1;
            if (indexPlaylistSong
                    == playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().size() - 1 && x < 0) {
                statuses.get(i).getStats().setName("");
                statuses.get(i).getStats().setPaused(true);
                statuses.get(i).setEmpty(true);
                x = ZERO;
                stats.setRemainedTime(x);
                //setam loaded.isLoaded false
                for (int j = 0; j < loaded.size(); j++) {
                    if (loaded.get(j).getUser().equals(statuses.get(i).getUser())) {
                        loaded.get(j).setLoaded(false);
                    }
                }
            } else {
                done = ONE;
                while (x < 0) {
                    if (indexPlaylistSong == playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size() - 1) {
                        //ne aflam la ultima melodie din playlist
                        if (x < 0) {
                            x = 0;
                            statuses.get(i).getStats().setName("");
                            statuses.get(i).getStats().setPaused(true);
                            statuses.get(i).setEmpty(true);
                            break;
                        }
                    }
                    for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                        if (playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(j)
                                .equals(statuses.get(i).getStats().getName())) {
                            indexPlaylistSong = j;
                        }
                    }
                    indexPlaylistSong++;
                    for (int j = 0; j < library.getSongs().size(); j++) {
                        if (library.getSongs().get(j).getName()
                                .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                        .get(indexPlaylistPlaylists).getSongs()
                                        .get(indexPlaylistSong))) {
                            duration = library.getSongs().get(j).getDuration();
                        }
                    }
                    x += duration;
                    statuses.get(i).getStats().setName(
                            playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong));
                }
                stats.setRemainedTime(x);
            }
        }
        if (statuses.get(i).getStats().isShuffle()) { //caz pentru modul shuffle activ
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                }
            }
            ArrayList<Integer> originalArray = new ArrayList<>();
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                originalArray.add(j);
            }
            //calculam noua ordine in functie de seed-ul shuffle-ului
            long seed = statuses.get(i).getSeed();
            Random random = new Random(seed);
            Collections.shuffle(originalArray, random);
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (originalArray.get(j) == indexPlaylistSong) {
                    if (j + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size() && x < 0) {
                        statuses.get(i).getStats().setName("");
                        statuses.get(i).getStats().setPaused(true);
                        statuses.get(i).setEmpty(true);
                        statuses.get(i).getStats().setShuffle(false);
                        x = 0;
                        stats.setRemainedTime(x);
                        break;
                    } else {
                        if (x < 0) {
                            indexPlaylistSong = originalArray.get(j + 1);
                            break;
                        }
                    }
                }
            }
            done = ONE;
            int endOfPlaylist = ZERO;
            while (x < 0 && endOfPlaylist == 0) {
                for (int j = 0; j < library.getSongs().size(); j++) {
                    if (library.getSongs().get(j).getName()
                            .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong))) {
                        duration = library.getSongs().get(j).getDuration();
                    }
                }
                x += duration;
                statuses.get(i).getStats().setName(playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong));
                for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                    if (originalArray.get(j) == indexPlaylistSong) {
                        if (j + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().size() && x < 0) {
                            statuses.get(i).getStats().setName("");
                            statuses.get(i).getStats().setPaused(true);
                            statuses.get(i).setEmpty(true);
                            statuses.get(i).getStats().setShuffle(false);
                            x = 0;
                            stats.setRemainedTime(x);
                            break;
                        } else {
                            if (x < 0) {
                                indexPlaylistSong = originalArray.get(j + 1);
                                break;
                            }
                        }
                    }
                }
            }
            stats.setRemainedTime(x);
        }
        return done;
    }

    /**
     * metoda auxiliara pentru metoda "statusPlaylist"
     * caz pentru repeat all
     */
    public void statusPlaylistRpAll(final ArrayList<Status> statuses, final LibraryInput library,
                                    final ArrayList<PlayList> playlists, final int i,
                                    final int indexPlaylistUsers, final int indexPlaylistPlaylists,
                                    final int indexPlaylistSongIn, final int durationIn,
                                    final int xIn, final Stats stats) {
        int duration = durationIn;
        int x = xIn;
        int indexPlaylistSong = indexPlaylistSongIn;

        //caz pentru shuffle activ
        if (statuses.get(i).getStats().isShuffle()) {
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                }
            }

            ArrayList<Integer> originalArray = new ArrayList<>();
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                originalArray.add(j);
            }
            //calculam noua ordine a melodiilor
            long seed = statuses.get(i).getSeed();
            Random random = new Random(seed);
            Collections.shuffle(originalArray, random);
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (originalArray.get(j) == indexPlaylistSong) {
                    if (j + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size()) {
                        indexPlaylistSong = originalArray.get(0);
                        break;
                    } else {
                        indexPlaylistSong = originalArray.get(j + 1);
                        break;
                    }
                }
            }
            while (x < 0) {
                for (int j = 0; j < library.getSongs().size(); j++) {
                    if (library.getSongs().get(j).getName()
                            .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong))) {
                        duration = library.getSongs().get(j).getDuration();
                    }
                }
                x += duration;

                statuses.get(i).getStats().setName(playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong));
                for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                    if (originalArray.get(j) == indexPlaylistSong) {
                        if (j + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().size()) {
                            indexPlaylistSong = originalArray.get(0);
                            break;
                        } else {
                            indexPlaylistSong = originalArray.get(j + 1);
                            break;
                        }
                    }
                }
            }
            stats.setRemainedTime(x);
        }

        //caz pentru shuffle inactiv
        if (!statuses.get(i).getStats().isShuffle()) {
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                }
            }
            while (x < 0) {
                if (indexPlaylistSong == playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size() - 1) {
                    indexPlaylistSong = 0;
                } else {
                    indexPlaylistSong++;
                }
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().size() == 0) {
                    return;
                }
                for (int j = 0; j < library.getSongs().size(); j++) {
                    if (library.getSongs().get(j).getName()
                            .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong))) {
                        duration = library.getSongs().get(j).getDuration();
                    }
                }
                x += duration;
                statuses.get(i).getStats().setName(playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong));
            }
            stats.setRemainedTime(x);
        }
    }

    /**
     * metoda auxiliara pentru metoda "statusPlaylist"
     *
     * @param statuses  lista cu statusurile utilizatorilor
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param command   comanda actuala
     * @param library   clasa care contine tot ce se afla in library
     * @param playlists lista cu playlisturile utilizatorilor
     * @param i         indicele care reflecta unde ne aflam in lista de statusuri
     * @param results   output-ul care reflecta statusul utilizatorului, in campul "stats"
     */
    public void statusPlaylist(final ArrayList<Status> statuses, final ObjectNode out,
                               final Command command, final LibraryInput library,
                               final ArrayList<PlayList> playlists, final int i,
                               final ObjectNode results, final ArrayList<Loaded> loaded) {
        Stats stats = statuses.get(i).getStats();
        if (!statuses.get(i).getStats().isPaused()) {
            int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                    .getTimestamp();

            //cautam playlist-ul pe care il asculta utilizatorul
            int indexPlaylistUsers = ZERO;
            int indexPlaylistPlaylists = ZERO;
            if (statuses.get(i).getStats().getName().equals("")) {
                results.put("name", "");
                results.put("remainedTime", 0);
                results.put("repeat", statuses.get(i).getStats().getRepeat());
                results.put("shuffle", statuses.get(i).getStats().isShuffle());
                results.put("paused", statuses.get(i).getStats().isPaused());
                out.set("stats", results);
                return;
            }
            for (int j = 0; j < playlists.size(); j++) {
                //cautam la fiecare utilizator
                for (int z = 0; z < playlists.get(j).getPlaylists().size(); z++) {
                    //luam toate playlisturile acestui utilizator si verificam numele
                    if (playlists.get(j).getPlaylists().get(z).getName()
                            .equals(statuses.get(i).getCurrentPlaylist())) {
                        //am gasit playlistul
                        indexPlaylistUsers = j;
                        indexPlaylistPlaylists = z;
                    }
                }
            }
            int indexPlaylistSong = ZERO;
            //cautam la ce piesa se afla utilizatorul in acel playlist
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                    if (indexPlaylistSong < playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size() - 1) {
                        indexPlaylistSong++;
                        break;
                    }
                }
            }
            int duration = ZERO;
            int done = ZERO;
            //in functie de modul de rulare actual al utilizatorului, simulam derularea entitatii
            //incarcate si actualizam datele in statusul utilizatorului
            if (stats.getRepeat().equals("Repeat Current Song")) {
                for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                    if (playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().get(j)
                            .equals(statuses.get(i).getStats().getName())) {
                        indexPlaylistSong = j;
                    }
                }
                done = ONE;
                int actDuration = 0;
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().size() == 0) {
                    return;
                }
                for (int j = 0; j < library.getSongs().size(); j++) {
                    if (library.getSongs().get(j).getName()
                            .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong))) {
                        actDuration = library.getSongs().get(j).getDuration();
                        break;
                    }
                }

                while (x < 0) {
                    x = actDuration + x;
                }
                stats.setRemainedTime(x);
            }
            if (stats.getRepeat().equals("No Repeat") && done == 0) {
                statusPlaylistNoRepeat(statuses, library, playlists, i, indexPlaylistUsers,
                        indexPlaylistPlaylists, indexPlaylistSong, duration, x, done, stats,
                        loaded);
            }

            if (stats.getRepeat().equals("Repeat All") && done == 0) {
                statusPlaylistRpAll(statuses, library, playlists, i, indexPlaylistUsers,
                        indexPlaylistPlaylists, indexPlaylistSong, duration, x, stats);
            }
        }
    }
    /**
     * metoda auxiliara pentru metoda "status"
     * calculeaza statusul pentru cazul in care utilizatorul asculta un album
     * @return
     */
    public void statusAlbum(final ArrayList<Status> statuses, final Command command, final int i,
                            final PageManagementHub pages) {
        Stats stats = statuses.get(i).getStats();
        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i).getTimestamp();
        if (x < 0) {
            while (x < 0) {
                //cautam albumul prezent in pages
                //albumul prezent este statuses.get(i).getCurrentAlbum()
                for (int j = 0; j < pages.getArtistPages().size(); j++) {
                    for (int z = 0; z < pages.getArtistPages().get(j).getAlbums().size(); z++) {
                        if (pages.getArtistPages().get(j).getAlbums().get(z).getName()
                                .equals(statuses.get(i).getCurrentAlbum())) {
                            //am gasit albumul
                            for (int k = 0;
                                 k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                         .size(); k++) {
                                if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                        .get(k).getName()
                                        .equals(statuses.get(i).getStats().getName())) {
                                    //am gasit melodia
                                    if (k == pages.getArtistPages().get(j).getAlbums().get(z)
                                            .getSongs().size() - 1 && !stats.isShuffle()) {
                                        statuses.get(i).getStats().setName("");
                                        statuses.get(i).getStats().setPaused(true);
                                        statuses.get(i).setEmpty(true);
                                        statuses.get(i).getStats().setShuffle(false);
                                        x = 0;
                                        stats.setRemainedTime(x);
                                        break;
                                    } else {
                                        if (!stats.isShuffle()) {
                                            statuses.get(i).getStats().setName(
                                                    pages.getArtistPages().get(j).getAlbums()
                                                            .get(z).getSongs().get(k + 1)
                                                            .getName());
                                            x += pages.getArtistPages().get(j).getAlbums().get(z)
                                                    .getSongs().get(k + 1).getDuration();
                                            stats.setRemainedTime(x);
                                            break;
                                        }
                                        if (stats.isShuffle()) {
                                            ArrayList<Integer> originalArray = new ArrayList<>();
                                            for (int l = 0;
                                                 l < pages.getArtistPages().get(j).getAlbums()
                                                         .get(z).getSongs().size(); l++) {
                                                originalArray.add(l);
                                            }
                                            long seed = statuses.get(i).getSeed();
                                            Random random = new Random(seed);
                                            Collections.shuffle(originalArray, random);
                                            for (int l = 0;
                                                 l < pages.getArtistPages().get(j).getAlbums()
                                                         .get(z).getSongs().size(); l++) {
                                                if (originalArray.get(l) == k) {
                                                    if (l + 1 == pages.getArtistPages().get(j)
                                                            .getAlbums().get(z).getSongs().size()
                                                            && x < 0) {
                                                        statuses.get(i).getStats().setName("");
                                                        statuses.get(i).getStats().setPaused(true);
                                                        statuses.get(i).setEmpty(true);
                                                        statuses.get(i).getStats()
                                                                .setShuffle(false);
                                                        x = 0;
                                                        stats.setRemainedTime(x);
                                                        break;
                                                    } else {
                                                        if (x < 0) {
                                                            statuses.get(i).getStats().setName(
                                                                    pages.getArtistPages().get(j)
                                                                            .getAlbums().get(z)
                                                                            .getSongs()
                                                                            .get(originalArray.get(
                                                                                    l + 1))
                                                                            .getName());
                                                            x += pages.getArtistPages().get(j)
                                                                    .getAlbums().get(z).getSongs()
                                                                    .get(originalArray.get(l + 1))
                                                                    .getDuration();
                                                            stats.setRemainedTime(x);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            stats.setRemainedTime(x);
        }
    }

    /**
     * metoda ce calculeaza statusul utilizatorului si il afiseaza la output-ul comenzii
     *
     * @param statuses  lista cu statusurile utilizatorilor
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param command   comanda actuala
     * @param library   clasa care contine tot ce se afla in library
     * @param playlists lista cu playlisturile utilizatorilor
     */
    public void status(final ArrayList<Status> statuses, final ObjectNode out,
                       final Command command, final LibraryInput library,
                       final ArrayList<PlayList> playlists, final UserManagement users,
                       final PageManagementHub pages, final ArrayList<Loaded> loaded) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode results = objectMapper.createObjectNode();
        //verificam daca utilizatorul are ceva incarcat
        //daca nu este nimic incarcat afisam statusul ca fiind gol
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).isEmpty()) {
                    results.put("name", "");
                    results.put("remainedTime", 0);
                    results.put("repeat", "No Repeat");
                    results.put("shuffle", statuses.get(i).getStats().isShuffle());
                    results.put("paused", statuses.get(i).getStats().isPaused());
                    out.set("stats", results);
                    return;
                }
                Stats stats = statuses.get(i).getStats();
                //verificam daca utilizatorul este offline, iar daca este lasam statusul asa cum
                //era si actualizam timestamp-ul, adica timpul la care am umblat ultima oara
                //la statusul unui utilizator
                for (int j = 0; j < users.getUsers().size(); j++) {
                    if (users.getUsers().get(j).getUsername().equals(command.getUsername())) {
                        if (users.getUsers().get(j).getConnectionStatus().equals("offline")) {
                            results.put("name", stats.getName());
                            results.put("remainedTime", stats.getRemainedTime());
                            results.put("repeat", stats.getRepeat());
                            results.put("shuffle", stats.isShuffle());
                            results.put("paused", stats.isPaused());
                            out.set("stats", results);
                            statuses.get(j).setTimestamp(command.getTimestamp());
                            return;
                        }
                    }
                }
                if (statuses.get(i).getTypeOfListening().equals("playlist")) {
                    statusPlaylist(statuses, out, command, library, playlists, i, results, loaded);
                }
                //in functie de tipul de entitate pe care utlizatorul il asculta, calculam si
                //afisam statusul curent (secunda unde a ajuns, entitatea la care se afla)
                if (statuses.get(i).getTypeOfListening().equals("song")) {
                    if (!stats.isPaused()) {
                        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                                .getTimestamp();
                        if (x < 0) {
                            int done = ZERO;
                            if (stats.getRepeat().equals("No Repeat")) {
                                done = ONE;
                                x = 0;
                                statuses.get(i).getStats().setName("");
                                statuses.get(i).getStats().setPaused(true);
                                stats.setRemainedTime(x);
                            }
                            if (stats.getRepeat().equals("Repeat Once") && done == 0) {
                                done = ONE;

                                if (statuses.get(i).getSongDuration() + x < 0) {
                                    stats.setRemainedTime(0);
                                    statuses.get(i).getStats().setName("");
                                    statuses.get(i).getStats().setPaused(true);
                                    stats.setRepeat("No Repeat");
                                } else {
                                    stats.setRemainedTime(statuses.get(i).getSongDuration() + x);
                                    stats.setRepeat("No Repeat");
                                }
                            }
                            if (stats.getRepeat().equals("Repeat Infinite") && done == 0) {
                                while (x < -statuses.get(i).getSongDuration()) {
                                    x = statuses.get(i).getSongDuration() + x;
                                }
                                stats.setRemainedTime(statuses.get(i).getSongDuration() + x);
                            }
                        } else {
                            stats.setRemainedTime(x);
                        }
                    }
                }
                if (statuses.get(i).getTypeOfListening().equals("podcast")) {
                    if (!stats.isPaused()) {
                        //cautam in library podcastul si episodul din podcast pe care il asculta
                        // userul
                        int podcastIndex = ZERO;
                        int episodeIndex = ZERO;
                        for (int j = 0; j < library.getPodcasts().size(); j++) {
                            if (library.getPodcasts().get(j).getName()
                                    .equals(statuses.get(i).getCurrentEpisode())) {
                                //am gasit podcastul mare
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
                            if (podcastIndex == library.getPodcasts().size() - 1
                                    && episodeIndex == library.getPodcasts().get(podcastIndex)
                                    .getEpisodes().size()) {
                                //am ajuns la ultimul episod din ultimul podcast
                                x = 0;
                                statuses.get(i).getStats().setName("");
                                statuses.get(i).getStats().setPaused(true);
                                statuses.get(i).setEmpty(true);
                                stats.setRemainedTime(x);
                                break;
                            }
                            x += library.getPodcasts().get(podcastIndex).getEpisodes()
                                    .get(episodeIndex).getDuration();
                            statuses.get(i).getStats().setName(
                                    library.getPodcasts().get(podcastIndex).getEpisodes()
                                            .get(episodeIndex).getName());
                        }
                        stats.setRemainedTime(x);
                    }
                }
                if (statuses.get(i).getTypeOfListening().equals("album") && !stats.isPaused()) {
                    statusAlbum(statuses, command, i, pages);
                }
                statuses.get(i).setTimestamp(command.getTimestamp());
                results.put("name", statuses.get(i).getStats().getName());
                results.put("remainedTime", statuses.get(i).getStats().getRemainedTime());
                results.put("repeat", statuses.get(i).getStats().getRepeat());
                results.put("shuffle", statuses.get(i).getStats().isShuffle());
                results.put("paused", statuses.get(i).getStats().isPaused());
                out.set("stats", results);
            }
        }
    }
}
