package main.programInterface;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import main.database.Command;
import main.database.Loaded;
import main.database.Status;
import main.database.Stats;
import main.database.PlayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * clasa ce contine metodele pentru navigarea prin melodiile utilizatorului
 */
public class NavigateClass extends Player {
    private static NavigateClass instance = null;
    private static final int NOT_INIT = -1;
    public NavigateClass() {
    }

    /**
     * metoda pentru a creea un obiect de tip NavigateClass
     *
     * @return obiectul de tip NavigateClass
     */
    public static NavigateClass getInstance() {
        if (instance == null) {
            synchronized (NavigateClass.class) {
                if (instance == null) {
                    instance = new NavigateClass();
                }
            }
        }
        return instance;
    }
    /**
     * metoda pentru trecerea la melodia din spate, pentru cazul in care ascultam un album
     * caz particular cand avem shuffle pornit
     * metoda auxiliara pentru metoda "prev"
     */
    public void nextAlbumShuffle(final ObjectNode out, final ArrayList<Status> statuses,
                                 final PageManagementHub pages, final int i) {
        //cautam albumul prezent in pages
        //albumul prezent este statuses.get(i).getCurrentAlbum()
        for (int j = 0; j < pages.getArtistPages().size(); j++) {
            for (int z = 0; z < pages.getArtistPages().get(j).getAlbums().size(); z++) {
                if (pages.getArtistPages().get(j).getAlbums().get(z).getName()
                        .equals(statuses.get(i).getCurrentAlbum())) {
                    //am gasit albumul
                    //daca e mai mult de o secunda, trecem la inceputul
                    // melodiei actuale
                    //facem vectorul de shuffle
                    ArrayList<Integer> originalArray = new ArrayList<>();
                    for (int k = 0; k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                            .size(); k++) {
                        originalArray.add(k);
                    }
                    long seed = statuses.get(i).getSeed();
                    Random random = new Random(seed);
                    Collections.shuffle(originalArray, random);
                    //caut la ce melodie din album ma aflu si salvez indicele
                    Integer indexSong = null;
                    for (int k = 0; k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                            .size(); k++) {
                        if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs().get(k)
                                .getName().equals(statuses.get(i).getStats().getName())) {
                            indexSong = k;
                            break;
                        }
                    }
                    //caut melodia urmatoare
                    int indexNextSong;
                    for (int k = 0; k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                            .size(); k++) {
                        if (originalArray.get(k) == indexSong) {
                            if (k + 1 == pages.getArtistPages().get(j).getAlbums().get(z)
                                    .getSongs().size()) {
                                //golesc statusul si afisez eroare
                                statuses.get(i).getStats().setName("");
                                statuses.get(i).getStats().setPaused(true);
                                statuses.get(i).getStats().setShuffle(false);
                                statuses.get(i).getStats().setRemainedTime(0);
                                out.put("message",
                                        "Please load a source before " + "skipping to the next "
                                                + "track.");
                                return;
                            } else {
                                indexNextSong = originalArray.get(k + 1);
                                //adaug in player melodia si afisez mesaj
                                statuses.get(i).getStats().setName(
                                        pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                                .get(indexNextSong).getName());
                                statuses.get(i).getStats().setRemainedTime(
                                        pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                                .get(indexNextSong).getDuration());
                                statuses.get(i).getStats().setPaused(false);
                                out.put("message", "Skipped to next track successfully. "
                                        + "The current track is " + statuses.get(i).getStats()
                                        .getName() + ".");
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * metoda pentru trecerea la melodia urmatoare, in cazul in care ascultam un album
     * metoda auxiliara pentru next
     */
    public void nextAlbum(final ObjectNode out, final ArrayList<Status> statuses,
                          final PageManagementHub pages, final int i) {
        if (statuses.get(i).getStats().isShuffle()) {
            nextAlbumShuffle(out, statuses, pages, i);
        }
        if (!statuses.get(i).getStats().isShuffle()) {
            //cautam albumul prezent in pages
            //albumul prezent este statuses.get(i).getCurrentAlbum()
            for (int j = 0; j < pages.getArtistPages().size(); j++) {
                for (int z = 0; z < pages.getArtistPages().get(j).getAlbums().size(); z++) {
                    if (pages.getArtistPages().get(j).getAlbums().get(z).getName()
                            .equals(statuses.get(i).getCurrentAlbum())) {
                        //am gasit albumul
                        //daca e mai mult de o secunda, trecem la inceputul
                        // melodiei actuale
                        //cautam melodia la care suntem in album
                        Integer duration = NOT_INIT;
                        for (int k = 0;
                             k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                     .size(); k++) {
                            if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs().get(k)
                                    .getName().equals(statuses.get(i).getStats().getName())) {
                                //am gasit melodia
                                //salvam durata acesteia
                                duration =
                                        pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                                .get(k).getDuration();
                            }
                        }
                        if (duration - statuses.get(i).getStats().getRemainedTime() >= 1) {
                            //cautam melodia la care suntem in album
                            for (int k = 0;
                                 k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                         .size(); k++) {
                                if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                        .get(k).getName()
                                        .equals(statuses.get(i).getStats().getName())) {
                                    //am gasit melodia
                                    //trecem la inceputul melodiei
                                    int time = pages.getArtistPages().get(j).getAlbums().get(z)
                                            .getSongs().get(k).getDuration();
                                    statuses.get(i).getStats().setRemainedTime(time);
                                    statuses.get(i).getStats().setPaused(false);
                                    out.put("message", "Skipped to next track successfully. "
                                            + "The current track is " + statuses.get(i).getStats()
                                            .getName() + ".");
                                    return;
                                }
                            }
                        } else {
                            //cautam melodia la care suntem in album
                            for (int k = 0;
                                 k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                         .size(); k++) {
                                if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                        .get(k).getName()
                                        .equals(statuses.get(i).getStats().getName())) {
                                    //am gasit melodia
                                    //trecem la urmatoarea melodie
                                    int indexSong = k;
                                    if (indexSong + 1 == pages.getArtistPages().get(j).getAlbums()
                                            .get(z).getSongs().size()) {
                                        //golesc statusul si afisez eroare
                                        statuses.get(i).getStats().setName("");
                                        statuses.get(i).getStats().setPaused(true);
                                        statuses.get(i).getStats().setRemainedTime(0);
                                        out.put("message", "Please load a source before "
                                                + "skipping to the next " + "track.");
                                        return;
                                    } else {
                                        //adaug in player melodia si afisez mesaj
                                        statuses.get(i).getStats().setName(
                                                pages.getArtistPages().get(j).getAlbums().get(z)
                                                        .getSongs().get(indexSong + 1).getName());
                                        statuses.get(i).getStats().setRemainedTime(
                                                pages.getArtistPages().get(j).getAlbums().get(z)
                                                        .getSongs().get(indexSong + 1)
                                                        .getDuration());
                                        statuses.get(i).getStats().setPaused(false);
                                        out.put("message",
                                                "Skipped to next track " + "successfully. "
                                                        + "The current track is " + statuses.get(i)
                                                        .getStats().getName() + ".");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * metoda pentru trecerea la melodia urmatoare (in functie de caz)
     *
     * @param statuses  lista cu statusurile utilizatorilor
     * @param command   comanda actuala
     * @param library   clasa care contine tot ce se afla in library
     * @param playlists clasa care contine tot ce se afla in library
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     */
    public void next(final ArrayList<Status> statuses, final Command command,
                     final LibraryInput library, final ArrayList<PlayList> playlists,
                     final ObjectNode out, final PageManagementHub pages) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        // iterez prin loadeds si afisez de fiecare daca cand gasesc un loaded
        // cu userul comandei
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).isEmpty() || statuses.get(i).getStats().getName().equals("")) {
                    out.put("message", "Please load a source before skipping to the next track.");
                    return;
                }
                Stats stats = statuses.get(i).getStats();
                if (statuses.get(i).getTypeOfListening().equals("playlist")) {
                    //cautam playlist-ul pe care il asculta utilizatorul
                    int indexPlaylistUsers = NOT_INIT;
                    int indexPlaylistPlaylists = NOT_INIT;
                    if (statuses.get(i).getStats().getRemainedTime() == 0) {
                        out.put("message",
                                "Please load a source before skipping to the next track.");
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
                    int indexPlaylistSong = NOT_INIT;
                    //cautam la ce piesa se afla utilizatorul in acel playlist
                    for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                        if (playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(j)
                                .equals(statuses.get(i).getStats().getName())) {
                            indexPlaylistSong = j;
                            if (indexPlaylistSong
                                    < playlists.get(indexPlaylistUsers).getPlaylists()
                                            .get(indexPlaylistPlaylists).getSongs().size() - 1) {
                                indexPlaylistSong++;
                                break;
                            }
                        }
                    }
                    boolean done = false;
                    if (stats.getRepeat().equals("Repeat Current Song")) {
                        for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                            if (playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs().get(j)
                                    .equals(statuses.get(i).getStats().getName())) {
                                indexPlaylistSong = j;
                            }
                        }
                        done = true;
                        int actDuration = NOT_INIT;
                        for (int j = 0; j < library.getSongs().size(); j++) {
                            if (library.getSongs().get(j).getName()
                                    .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                            .get(indexPlaylistPlaylists).getSongs()
                                            .get(indexPlaylistSong))) {
                                actDuration = library.getSongs().get(j).getDuration();
                                break;
                            }
                        }
                        stats.setRemainedTime(actDuration);
                        stats.setPaused(false);
                        out.put("message",
                                "Skipped to next track successfully. The current track is "
                                        + stats.getName() + ".");
                    }
                    if (stats.getRepeat().equals("No Repeat") && !done) {
                        for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                            if (playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs().get(j)
                                    .equals(statuses.get(i).getStats().getName())) {
                                indexPlaylistSong = j;
                            }
                        }
                        nextPlaylistNoRepeat(out, indexPlaylistSong, statuses, library, playlists,
                                i, indexPlaylistUsers, indexPlaylistPlaylists);
                    }
                    if (stats.getRepeat().equals("Repeat All") && !done) {
                        nextPlaylistRepeatAll(out, statuses, library, playlists,
                                i, indexPlaylistUsers, indexPlaylistPlaylists);
                    }
                }
                if (statuses.get(i).getTypeOfListening().equals("song")) {
                    if (!stats.isPaused()) {
                        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                                .getTimestamp();
                        if (x < 0) {
                            boolean done = false;
                            if (stats.getRepeat().equals("No Repeat")) {
                                done = true;
                                x = 0;
                                statuses.get(i).getStats().setName("");
                                statuses.get(i).getStats().setPaused(true);
                                stats.setRemainedTime(x);
                            }
                            if (stats.getRepeat().equals("Repeat Once") && !done) {
                                done = true;
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
                            if (stats.getRepeat().equals("Repeat Infinite") && !done) {
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
                    nextPodcast(out, statuses, library, i, command);
                }
                if (statuses.get(i).getTypeOfListening().equals("album")) {
                    nextAlbum(out, statuses, pages, i);
                }
                statuses.get(i).setTimestamp(command.getTimestamp());
            }
        }
    }

    /**
     * metoda ce pune pauza sau scoate pauza de pe episodul curent
     * metoda auxiliara pentru next
     */
    public void nextPodcast(final ObjectNode out, final ArrayList<Status> statuses,
                            final LibraryInput library, final int i, final Command command) {

        Stats stats = statuses.get(i).getStats();
        if (!stats.isPaused()) {
            int podcastIndex = NOT_INIT;
            int episodeIndex = NOT_INIT;
            for (int j = 0; j < library.getPodcasts().size(); j++) {
                if (library.getPodcasts().get(j).getName()
                        .equals(statuses.get(i).getCurrentEpisode())) {
                    //am gasit podcastul mare
                    podcastIndex = j;
                    break;
                }
            }
            for (int j = 0;
                 j < library.getPodcasts().get(podcastIndex).getEpisodes().size(); j++) {
                if (library.getPodcasts().get(podcastIndex).getEpisodes().get(j).getName()
                        .equals(statuses.get(i).getStats().getName())) {
                    //am gasit episodul
                    episodeIndex = j;
                    break;
                }
            }
            int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                    .getTimestamp();
            while (x < 0) {
                episodeIndex++;
                x += library.getPodcasts().get(podcastIndex).getEpisodes().get(episodeIndex)
                        .getDuration();
                statuses.get(i).getStats().setName(
                        library.getPodcasts().get(podcastIndex).getEpisodes().get(episodeIndex)
                                .getName());
            }
            stats.setRemainedTime(x);
            episodeIndex++;
            x += library.getPodcasts().get(podcastIndex).getEpisodes().get(episodeIndex)
                    .getDuration();
            statuses.get(i).getStats().setName(
                    library.getPodcasts().get(podcastIndex).getEpisodes().get(episodeIndex)
                            .getName());
            stats.setPaused(false);
            out.put("message",
                    "Skipped to next track successfully. The current track is " + stats.getName()
                            + ".");
            stats.setRemainedTime(x);

        }
    }

    /**
     * metoda ce pune pauza sau scoate pauza de pe melodia curenta
     * metoda auxiliara pentru next
     */
    public void nextPlaylistRepeatAll(final ObjectNode out, final ArrayList<Status> statuses,
                                      final LibraryInput library,
                                      final ArrayList<PlayList> playlists, final int i,
                                      final int indexPlaylistUsers,
                                      final int indexPlaylistPlaylists) {
        int x = statuses.get(i).getStats().getRemainedTime();
        int duration = NOT_INIT;
        Stats stats = statuses.get(i).getStats();
        int indexPlaylistSong;
        if (statuses.get(i).getStats().isShuffle()) {
            ArrayList<Integer> originalArray = new ArrayList<>();
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                originalArray.add(j);
            }
            long seed = statuses.get(i).getSeed();
            Random random = new Random(seed);
            Collections.shuffle(originalArray, random);
            indexPlaylistSong = NOT_INIT;
            //cautam la ce piesa se afla utilizatorul in acel playlist
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                    break;
                }
            }
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
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

            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x = duration;
            stats.setRemainedTime(x);
            stats.setPaused(false);
            out.put("message",
                    "Skipped to next track successfully. The current track is " + stats.getName()
                            + ".");
        }
        if (!statuses.get(i).getStats().isShuffle()) {
            indexPlaylistSong = NOT_INIT;
            //cautam la ce piesa se afla utilizatorul in acel playlist
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                    break;
                }
            }

            if (indexPlaylistSong
                    == playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().size() - 1) {
                statuses.get(i).getStats().setName(playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(0));
                for (int j = 0; j < library.getSongs().size(); j++) {
                    if (library.getSongs().get(j).getName()
                            .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs().get(0))) {
                        duration = library.getSongs().get(j).getDuration();
                    }
                }
                x = duration;
                stats.setPaused(false);
                stats.setRemainedTime(x);
                out.put("message", "Skipped to next track successfully. The current track " + "is "
                        + stats.getName() + ".");
                return;
            }


            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            if (indexPlaylistSong
                    == playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().size() - 1) {
                indexPlaylistSong = 0;
            } else {
                indexPlaylistSong++;
            }

            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            stats.setRemainedTime(duration);
            stats.setPaused(false);
            out.put("message",
                    "Skipped to next track successfully. The current track is " + stats.getName()
                            + ".");
        }
    }

    /**
     * metoda ce pune pauza sau scoate pauza de pe melodia curenta
     * metoda auxiliara pentru next
     *
     * @param out
     * @param indexPlaylistSongIn
     * @param statuses
     * @param library
     * @param playlists
     * @param i
     * @param indexPlaylistUsers
     * @param indexPlaylistPlaylists
     */
    public void nextPlaylistNoRepeat(final ObjectNode out, final int indexPlaylistSongIn,
                                     final ArrayList<Status> statuses, final LibraryInput library,
                                     final ArrayList<PlayList> playlists, final int i,
                                     final int indexPlaylistUsers,
                                     final int indexPlaylistPlaylists) {
        int x = statuses.get(i).getStats().getRemainedTime();
        int duration = NOT_INIT;
        Stats stats = statuses.get(i).getStats();
        int indexPlaylistSong = indexPlaylistSongIn;
        if (!statuses.get(i).getStats().isShuffle()) {
            while (x < 0) {
                if (indexPlaylistSong == playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size()) {
                    //ne aflam la ultima melodie din playlist
                    x = 0;
                    statuses.get(i).getStats().setName("");
                    statuses.get(i).getStats().setPaused(true);
                    statuses.get(i).setEmpty(true);
                    stats.setRemainedTime(0);
                    break;
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
                statuses.get(i).getStats().setName(playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong));
            }
            stats.setRemainedTime(x);
            if (statuses.get(i).isEmpty()) {
                out.put("message", "Please load a source before skipping to the next track.");
                statuses.get(i).getStats().setName("");
                statuses.get(i).getStats().setPaused(true);
                statuses.get(i).setEmpty(true);
                stats.setRemainedTime(0);
                return;
            }
            if (indexPlaylistSong
                    == playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().size() - 1) {
                out.put("message", "Please load a source before skipping to the next track.");
                statuses.get(i).getStats().setName("");
                statuses.get(i).getStats().setPaused(true);
                statuses.get(i).setEmpty(true);
                stats.setRemainedTime(0);
                return;
            }
            indexPlaylistSong++;
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x = duration;
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            stats.setRemainedTime(x);
            stats.setPaused(false);

            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                }
            }
            out.put("message",
                    "Skipped to next track successfully. The current track is " + statuses.get(i)
                            .getStats().getName() + ".");
        }
        if (statuses.get(i).getStats().isShuffle()) {
            //facem vectorul de shuffle
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
            long seed = statuses.get(i).getSeed();
            Random random = new Random(seed);
            Collections.shuffle(originalArray, random);
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (originalArray.get(j) == indexPlaylistSong) {
                    if (j + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size()) {
                        stats.setName("");
                        stats.setRemainedTime(0);
                        stats.setPaused(true);
                        stats.setShuffle(false);
                        out.put("message",
                                "Please load a source before skipping to the next track.");
                        return;
                    } else {
                        indexPlaylistSong = originalArray.get(j + 1);
                        break;
                    }
                }
            }
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x = duration;
            stats.setRemainedTime(x);
            stats.setPaused(false);
            out.put("message",
                    "Skipped to next track successfully. The current track is " + stats.getName()
                            + ".");
        }
    }
    /**
     * metoda pentru trecerea la melodia din spate, pentru cazul in care avem un album
     * metoda auxiliara pentru metoda "prev"
     */
    public void prevAlbum(final ObjectNode out, final ArrayList<Status> statuses,
                          final PageManagementHub pages, final int i) {
        Stats stats = statuses.get(i).getStats();
        if (stats.getRepeat().equals("No Repeat")) {
            //cautam albumul prezent in pages
            //albumul prezent este statuses.get(i).getCurrentAlbum()
            for (int j = 0; j < pages.getArtistPages().size(); j++) {
                for (int z = 0; z < pages.getArtistPages().get(j).getAlbums().size(); z++) {
                    if (pages.getArtistPages().get(j).getAlbums().get(z).getName()
                            .equals(statuses.get(i).getCurrentAlbum())) {
                        //am gasit albumul
                        //daca e mai mult de o secunda, trecem la inceputul melodiei
                        // actuale
                        //cautam melodia la care suntem in album
                        int duration = NOT_INIT;
                        for (int k = 0;
                             k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                     .size(); k++) {
                            if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs().get(k)
                                    .getName().equals(statuses.get(i).getStats().getName())) {
                                //am gasit melodia
                                //salvam durata acesteia
                                duration =
                                        pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                                .get(k).getDuration();
                            }
                        }
                        if (duration - statuses.get(i).getStats().getRemainedTime() >= 1) {
                            //cautam melodia la care suntem in album
                            for (int k = 0;
                                 k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                         .size(); k++) {
                                if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                        .get(k).getName()
                                        .equals(statuses.get(i).getStats().getName())) {
                                    //am gasit melodia
                                    //trecem la inceputul melodiei
                                    int time = pages.getArtistPages().get(j).getAlbums().get(z)
                                            .getSongs().get(k).getDuration();
                                    statuses.get(i).getStats().setRemainedTime(time);
                                    statuses.get(i).getStats().setPaused(false);
                                    out.put("message", "Returned to previous track successfully. "
                                            + "The current track is " + statuses.get(i).getStats()
                                            .getName() + ".");
                                    return;
                                }
                            }
                            statuses.get(i).getStats().setRemainedTime(0);
                            statuses.get(i).getStats().setPaused(false);
                            out.put("message",
                                    "Returned to previous track successfully. The " + "current "
                                            + "track is " + statuses.get(i).getStats().getName()
                                            + ".");
                            return;
                        } else {
                            //trecem la melodia precedenta
                            //cautam melodia la care suntem in album
                            for (int k = 0;
                                 k < pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                         .size(); k++) {
                                if (pages.getArtistPages().get(j).getAlbums().get(z).getSongs()
                                        .get(k).getName()
                                        .equals(statuses.get(i).getStats().getName())) {
                                    //am gasit melodia
                                    //trecem la melodia precedenta
                                    if (k - 1 < 0) {
                                        //golesc statusul si afisez eroare
                                        statuses.get(i).getStats().setName("");
                                        statuses.get(i).getStats().setPaused(true);
                                        statuses.get(i).getStats().setRemainedTime(0);
                                        out.put("message", "Please load a source before "
                                                + "returning to the previous" + " track.");
                                        return;
                                    } else {
                                        statuses.get(i).getStats().setName(
                                                pages.getArtistPages().get(j).getAlbums().get(z)
                                                        .getSongs().get(k - 1).getName());
                                        statuses.get(i).getStats().setRemainedTime(
                                                pages.getArtistPages().get(j).getAlbums().get(z)
                                                        .getSongs().get(k - 1).getDuration());
                                        statuses.get(i).getStats().setPaused(false);
                                        out.put("message",
                                                "Returned to previous track " + "successfully. "
                                                        + "The current track is " + statuses.get(i)
                                                        .getStats().getName() + ".");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * metoda pentru trecerea la melodia din spate (in functie de caz)
     *
     * @param statuses  lista cu statusurile utilizatorilor
     * @param command   comanda actuala
     * @param library   clasa care contine tot ce se afla in library
     * @param playlists clasa care contine tot ce se afla in library
     * @param loadeds   lista cu tot ce este incarcat
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     */
    public void prev(final ArrayList<Status> statuses, final Command command,
                     final LibraryInput library, final ArrayList<PlayList> playlists,
                     final ArrayList<Loaded> loadeds, final ObjectNode out,
                     final PageManagementHub pages) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        for (int i = 0; i < statuses.size(); i++) {
            if (statuses.get(i).getUser().equals(command.getUsername())) {
                if (statuses.get(i).isEmpty() || statuses.get(i).getStats().getName().equals("")) {
                    out.put("message",
                            "Please load a source before returning to the previous track.");
                    return;
                }
                Stats stats = statuses.get(i).getStats();
                if (statuses.get(i).getTypeOfListening().equals("playlist")) {
                    prevPlaylist(statuses, out, command, library, playlists, i, loadeds);
                }
                if (statuses.get(i).getTypeOfListening().equals("song")) {
                    if (!stats.isPaused()) {
                        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                                .getTimestamp();
                        if (x < 0) {
                            boolean done = false;
                            if (stats.getRepeat().equals("No Repeat")) {
                                done = true;
                                x = 0;
                                statuses.get(i).getStats().setName("");
                                statuses.get(i).getStats().setPaused(true);
                                stats.setRemainedTime(x);
                            }
                            if (stats.getRepeat().equals("Repeat Once") && !done) {
                                done = true;
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
                            if (stats.getRepeat().equals("Repeat Infinite") && !done) {
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
                        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                                .getTimestamp();
                        if (x < 0) {
                            x = 0;
                            statuses.get(i).getStats().setName("");
                            statuses.get(i).getStats().setPaused(true);
                        }
                        stats.setRemainedTime(x);
                    }
                }
                if (statuses.get(i).getTypeOfListening().equals("album")) {
                   prevAlbum(out, statuses, pages, i);
                }
                statuses.get(i).setTimestamp(command.getTimestamp());
                statuses.get(i).getStats().setPaused(false);
            }
        }
    }

    /**
     * metoda pentru trecerea la melodia precedenta (in functie de caz)
     * metoda auxiliara pentru metoda prevPlaylistNoRepeat
     */
    public void prevPlaylistNoRepeatShuffle(final ArrayList<Status> statuses, final ObjectNode out,
                                            final LibraryInput library, final Stats stats,
                                            final ArrayList<PlayList> playlists, final int i,
                                            final int indexPlaylistSongIn,
                                            final int indexPlaylistPlaylists, final int durationIn,
                                            final int xIn, final int indexPlaylistUsers) {
        int indexPlaylistSong = indexPlaylistSongIn;
        int duration = durationIn;
        int x = xIn;
        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                    .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                indexPlaylistSong = j;
            }
        }
        while (x < 0) {
            if (indexPlaylistSong >= playlists.get(indexPlaylistUsers).getPlaylists()
                    .get(indexPlaylistPlaylists).getSongs().size()) {
                //ne aflam la ultima melodie din playlist
                if (x < 0) {
                    statuses.get(i).getStats().setName("");
                    statuses.get(i).getStats().setPaused(true);
                    break;
                }
            }
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x += duration;
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            indexPlaylistSong++;
        }
        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                    .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                indexPlaylistSong = j;
            }
        }
        for (int j = 0; j < library.getSongs().size(); j++) {
            if (library.getSongs().get(j).getName()
                    .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                duration = library.getSongs().get(j).getDuration();
            }
        }
        if (duration - stats.getRemainedTime() < 1) {
            if (indexPlaylistSong - 1 >= 0) {
                indexPlaylistSong--;
            }
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x = duration;
            stats.setRemainedTime(x);
            out.put("message",
                    "Returned to previous track successfully. The current " + "track is "
                            + statuses.get(i).getStats().getName() + ".");
        } else {
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                    indexPlaylistSong = j;
                }
            }
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x = duration;
            stats.setRemainedTime(x);
            out.put("message",
                    "Returned to previous track successfully. The current " + "track is "
                            + statuses.get(i).getStats().getName() + ".");
        }
    }

    /**
     * metoda pentru trecerea la melodia precedenta (in functie de caz)
     * metoda auxiliara pentru metoda prevPlaylist
     */
    public void prevPlaylistNoRepeat(final ArrayList<Status> statuses, final ObjectNode out,
                                     final Command command, final LibraryInput library,
                                     final ArrayList<PlayList> playlists, final int i,
                                     final int indexPlaylistSongIn, final int indexPlaylistUsers,
                                     final int indexPlaylistPlaylists, final int durationIn,
                                     final int xIn, final Stats stats) {
        int indexPlaylistSong = indexPlaylistSongIn;
        int duration = durationIn;
        int x = xIn;
        if (!statuses.get(i).getStats().isShuffle()) {
            prevPlaylistNoRepeatShuffle(statuses, out, library, stats, playlists, i,
                    indexPlaylistSong, indexPlaylistPlaylists, duration, x,
                    indexPlaylistUsers);
        }
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
            long seed = statuses.get(i).getSeed();
            Random random = new Random(seed);
            Collections.shuffle(originalArray, random);
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            if (duration - stats.getRemainedTime() > 1) {
                stats.setRemainedTime(duration);
                stats.setName(playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong));
                statuses.get(i).setTimestamp(command.getTimestamp());
                out.put("message",
                        "Returned to previous track successfully. The current " + "track is "
                                + stats.getName() + ".");
                statuses.get(i).getStats().setPaused(false);
                return;
            }
            if (duration - stats.getRemainedTime() <= 1) {
                boolean notOk = false;
                for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                    if (originalArray.get(j) == indexPlaylistSong) {
                        if (j - 1 == -1) {
                            notOk = true;
                        }
                    }
                }
                if (notOk) {
                    statuses.get(i).getStats().setName(
                            playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong));
                    for (int j = 0; j < library.getSongs().size(); j++) {
                        if (library.getSongs().get(j).getName()
                                .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                        .get(indexPlaylistPlaylists).getSongs()
                                        .get(indexPlaylistSong))) {
                            duration = library.getSongs().get(j).getDuration();
                        }
                    }
                    x = duration;
                    stats.setRemainedTime(x);
                } else {
                    for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                        if (originalArray.get(j) == indexPlaylistSong) {
                            if (j - 1 == -1) {
                                indexPlaylistSong = originalArray.get(
                                        playlists.get(indexPlaylistUsers).getPlaylists()
                                                .get(indexPlaylistPlaylists).getSongs().size()
                                                - 1);
                            } else {
                                indexPlaylistSong = originalArray.get(j - 1);
                                break;
                            }
                        }
                    }

                    statuses.get(i).getStats().setName(
                            playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong));
                    for (int j = 0; j < library.getSongs().size(); j++) {
                        if (library.getSongs().get(j).getName()
                                .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                        .get(indexPlaylistPlaylists).getSongs()
                                        .get(indexPlaylistSong))) {
                            duration = library.getSongs().get(j).getDuration();
                        }
                    }
                    x = duration;
                    stats.setRemainedTime(x);
                }
            }
            statuses.get(i).getStats().setPaused(false);
            out.put("message",
                    "Returned to previous track successfully. The current track " + "is "
                            + stats.getName() + ".");
        }
    }

    /**
     * metoda pentru trecerea la melodia precedenta (in functie de caz)
     * metoda auxiliara pentru metoda prevPlaylist
     */
    public void prevPlaylistRepeatAllShuffle(final int indexPlaylistSongIn,
                                             final int indexPlaylistUsers,
                                             final int indexPlaylistPlaylists, final Stats stats,
                                             final int xIn, final ArrayList<PlayList> playlists,
                                             final ArrayList<Status> statuses,
                                             final LibraryInput library, final ObjectNode out,
                                             final int i, final ArrayList<Loaded> loadeds) {
        int x = xIn;
        int duration = NOT_INIT;
        int indexPlaylistSong = indexPlaylistSongIn;
        ArrayList<Integer> originalArray = new ArrayList<>();
        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            originalArray.add(j);
        }
        long seed = statuses.get(i).getSeed();
        Random random = new Random(seed);
        Collections.shuffle(originalArray, random);
        for (int j = 0; j < library.getSongs().size(); j++) {
            if (library.getSongs().get(j).getName()
                    .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                duration = library.getSongs().get(j).getDuration();
            }
        }
        if (duration - statuses.get(i).getStats().getRemainedTime() > 1) {
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            x = duration;
            stats.setPaused(false);
            stats.setRemainedTime(x);
            out.put("message",
                    "Returned to previous track successfully. The current " + "track is "
                            + stats.getName() + ".");
            return;
        }
        if (duration - statuses.get(i).getStats().getRemainedTime() <= 1) {
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (originalArray.get(j) == indexPlaylistSong) {
                    if (j - 1 == -1) {
                        indexPlaylistSong = originalArray.get(
                                playlists.get(indexPlaylistUsers).getPlaylists()
                                        .get(indexPlaylistPlaylists).getSongs().size() - 1);
                    } else {
                        indexPlaylistSong = originalArray.get(j - 1);
                        break;
                    }
                }
            }
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x = duration;
            stats.setPaused(false);
            stats.setRemainedTime(x);
            out.put("message",
                    "Returned to previous track successfully. The current " + "track is "
                            + stats.getName() + ".");
            return;
        }
        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            if (originalArray.get(j) == indexPlaylistSong) {
                if (j - 1 == -1) {
                    indexPlaylistSong = originalArray.get(
                            playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs().size() - 1);
                } else {
                    indexPlaylistSong = originalArray.get(j - 1);
                    break;
                }
            }
        }
        boolean endOfPlaylist = false;
        while (x < 0 && !endOfPlaylist) {
            if (indexPlaylistSong >= playlists.get(indexPlaylistUsers).getPlaylists()
                    .get(indexPlaylistPlaylists).getSongs().size()) {
                //ne aflam la ultima melodie din playlist
                statuses.get(i).getStats().setName("");
                statuses.get(i).getStats().setPaused(true);
                break;
            }
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x += duration;
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            for (int j = 0;
                 j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                         .getSongs().size(); j++) {
                if (originalArray.get(j) == indexPlaylistSong) {
                    if (j + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size()) {
                        endOfPlaylist = true;
                        statuses.get(i).getStats().setName("");
                        statuses.get(i).getStats().setPaused(true);
                        break;
                    } else {
                        indexPlaylistSong = originalArray.get(j + 1);
                        break;
                    }
                }
            }
        }
        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            if (originalArray.get(j) == indexPlaylistSong) {
                if (j + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size()) {
                    statuses.get(i).getStats().setName("");
                    statuses.get(i).getStats().setPaused(true);
                    statuses.get(i).getStats().setShuffle(false);
                    statuses.get(i).setEmpty(true);
                    loadeds.get(i).setLoaded(false);
                    return;
                } else {
                    indexPlaylistSong = originalArray.get(j + 1);
                    break;
                }
            }
        }
        statuses.get(i).getStats().setName(
                playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                        .getSongs().get(indexPlaylistSong));

        x = duration;
        stats.setRemainedTime(x);
        out.put("message", "Returned to previous track successfully. The current track " + "is "
                + stats.getName() + ".");
    }

    /**
     * metoda pentru trecerea la melodia precedenta (in functie de caz)
     * metoda auxiliara pentru metoda prevPlaylist
     */
    public void prevPlaylistRepeatAllNoShuffle(final int indexPlaylistSongIn,
                                               final int indexPlaylistUsers,
                                               final int indexPlaylistPlaylists, final Stats stats,
                                               final int xIn, final ArrayList<PlayList> playlists,
                                               final ArrayList<Status> statuses,
                                               final LibraryInput library, final ObjectNode out,
                                               final int i) {
        int x = xIn;
        int duration = NOT_INIT;
        int indexPlaylistSong = indexPlaylistSongIn;

        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                    .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                indexPlaylistSong = j;
            }
        }
        while (x < 0) {
            if (indexPlaylistSong + 1 == playlists.get(indexPlaylistUsers).getPlaylists()
                    .get(indexPlaylistPlaylists).getSongs().size()) {
                indexPlaylistSong = 0;
            } else {
                indexPlaylistSong++;
            }

            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            x += duration;
            statuses.get(i).getStats().setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));

        }
        for (int j = 0; j < library.getSongs().size(); j++) {
            if (library.getSongs().get(j).getName()
                    .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                duration = library.getSongs().get(j).getDuration();
            }
        }
        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                    .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                indexPlaylistSong = j;
            }
        }
        if (duration - stats.getRemainedTime() > 1) {
            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            stats.setRemainedTime(duration);
            stats.setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            statuses.get(i).getStats().setPaused(false);
            out.put("message",
                    "Returned to previous track successfully. The current " + "track is "
                            + stats.getName() + ".");
        } else if (duration - stats.getRemainedTime() <= 1) {
            if (indexPlaylistSong - 1 == -1) {
                indexPlaylistSong = 0;
            } else {
                indexPlaylistSong--;
            }

            for (int j = 0; j < library.getSongs().size(); j++) {
                if (library.getSongs().get(j).getName()
                        .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong))) {
                    duration = library.getSongs().get(j).getDuration();
                }
            }
            stats.setRemainedTime(duration);
            stats.setName(
                    playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                            .getSongs().get(indexPlaylistSong));
            statuses.get(i).getStats().setPaused(false);
            out.put("message",
                    "Returned to previous track successfully. The current " + "track is "
                            + stats.getName() + ".");
        }
    }

    /**
     * metoda pentru trecerea la melodia precedenta (in functie de caz)
     * metoda auxiliara pentru prev
     */
    public void prevPlaylist(final ArrayList<Status> statuses, final ObjectNode out,
                             final Command command, final LibraryInput library,
                             final ArrayList<PlayList> playlists, final int i,
                             final ArrayList<Loaded> loadeds) {
        Stats stats = statuses.get(i).getStats();
        int x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i).getTimestamp();
        Integer indexPlaylistUsers = null;
        Integer indexPlaylistPlaylists = null;
        if (statuses.get(i).getStats().getName().equals("")) {
            out.put("message", "Please load a source before skipping to the next track.");
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
        int indexPlaylistSong = NOT_INIT;
        //cautam la ce piesa se afla utilizatorul in acel playlist
        for (int j = 0;
             j < playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                     .getSongs().size(); j++) {
            if (playlists.get(indexPlaylistUsers).getPlaylists().get(indexPlaylistPlaylists)
                    .getSongs().get(j).equals(statuses.get(i).getStats().getName())) {
                indexPlaylistSong = j;
            }
        }
        int duration = NOT_INIT;
        boolean done = false;
        if (stats.getRepeat().equals("Repeat Current Song")) {
            if (!stats.isShuffle()) {
                done = true;
                int actDuration = NOT_INIT;
                for (int j = 0; j < library.getSongs().size(); j++) {
                    if (library.getSongs().get(j).getName()
                            .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong))) {
                        actDuration = library.getSongs().get(j).getDuration();
                        break;
                    }
                }
                stats.setName(playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong));
                stats.setRemainedTime(actDuration);
                out.put("message", "Returned to previous track successfully. The current track is "
                        + stats.getName() + ".");
            }
            if (stats.isShuffle()) {
                //creez vectorul de shuffle
                ArrayList<Integer> originalArray = new ArrayList<>();
                for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                    originalArray.add(j);
                }
                long seed = statuses.get(i).getSeed();
                Random random = new Random(seed);
                Collections.shuffle(originalArray, random);
                x = stats.getRemainedTime() - command.getTimestamp() + statuses.get(i)
                        .getTimestamp();
                //iau durata melodiei actuale
                String song = playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong);
                for (int j = 0; j < library.getSongs().size(); j++) {
                    //caut in library melodia cu numele song
                    //salvez durata in duration
                    if (library.getSongs().get(j).getName().equals(song)) {
                        duration = library.getSongs().get(j).getDuration();
                    }
                }
                //caz pentru care a trecut mai putin de o secunda din melodie
                //trec la inceputul melodiei
                if (duration - stats.getRemainedTime() >= 1) {
                    stats.setRemainedTime(duration);
                    stats.setName(playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().get(indexPlaylistSong));
                    statuses.get(i).setTimestamp(command.getTimestamp());
                    out.put("message",
                            "Returned to previous track successfully. The current track is "
                                    + stats.getName() + ".");
                    statuses.get(i).getStats().setPaused(false);
                    return;
                } else {
                    for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                        if (originalArray.get(j) == indexPlaylistSong) {
                            if (j - 1 != -1) {
                                indexPlaylistSong = originalArray.get(j - 1);
                                break;
                            }
                        }
                    }
                    int actDuration;
                    for (int j = 0; j < library.getSongs().size(); j++) {
                        if (library.getSongs().get(j).getName()
                                .equals(playlists.get(indexPlaylistUsers).getPlaylists()
                                        .get(indexPlaylistPlaylists).getSongs()
                                        .get(indexPlaylistSong))) {
                            actDuration = library.getSongs().get(j).getDuration();
                            stats.setName(playlists.get(indexPlaylistUsers).getPlaylists()
                                    .get(indexPlaylistPlaylists).getSongs()
                                    .get(indexPlaylistSong));
                            stats.setRemainedTime(actDuration);
                            out.put("message",
                                    "Returned to previous track successfully. The current track "
                                            + "is "
                                            + stats.getName() + ".");
                            break;
                        }
                    }
                    done = true;
                }
            }
        }
        if (stats.getRepeat().equals("No Repeat") && !done) {
            prevPlaylistNoRepeat(statuses, out, command, library, playlists, i, indexPlaylistSong,
                    indexPlaylistUsers, indexPlaylistPlaylists, duration, x, stats);
        }
        if (stats.getRepeat().equals("Repeat All") && !done) {
            if (statuses.get(i).getStats().isShuffle()) {
                for (int j = 0; j < playlists.get(indexPlaylistUsers).getPlaylists()
                        .get(indexPlaylistPlaylists).getSongs().size(); j++) {
                    if (playlists.get(indexPlaylistUsers).getPlaylists()
                            .get(indexPlaylistPlaylists).getSongs().get(j)
                            .equals(statuses.get(i).getStats().getName())) {
                        indexPlaylistSong = j;
                    }
                }
                prevPlaylistRepeatAllShuffle(indexPlaylistSong, indexPlaylistUsers,
                        indexPlaylistPlaylists, stats, x, playlists, statuses, library, out, i,
                        loadeds);
            }
            if (!statuses.get(i).getStats().isShuffle()) {
                prevPlaylistRepeatAllNoShuffle(indexPlaylistSong, indexPlaylistUsers,
                        indexPlaylistPlaylists, stats, x, playlists, statuses, library, out, i);
            }
        }
    }
}
