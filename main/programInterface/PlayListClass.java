package main.programInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import main.database.*;

import java.util.ArrayList;

public class PlayListClass {
    private static final int MAX = 5;
    private static final int ZERO = 0;
    private static PlayListClass instance = null;

    public PlayListClass() {
    }

    /**
     * metoda pentru a creea un obiect de tip StatusClass
     *
     * @return obiectul de tip StatusClass
     */
    public static PlayListClass getInstance() {
        if (instance == null) {
            synchronized (PlayListClass.class) {
                if (instance == null) {
                    instance = new PlayListClass();
                }
            }
        }
        return instance;
    }

    /**
     * metoda care creeaza un playlist pentru un utilizator
     *
     * @param command   comanda actuala
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param playlists clasa care contine tot ce se afla in library
     */
    public void createPlaylist(final Command command, final ObjectNode out,
                               final ArrayList<PlayList> playlists) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        boolean okPlaylistCreated = false;
        for (int i = 0; i < playlists.size(); i++) {
            if (command.getUsername().equals(playlists.get(i).getUsername())) {
                for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                    if (playlists.get(i).getPlaylists().get(j).getName()
                            .equals(command.getPlaylistName())) {
                        out.put("message", "A playlist with the same name already exists.");
                        return;
                    }
                }
                okPlaylistCreated = true;
                PlayListsByUser newPlaylist = new PlayListsByUser();
                newPlaylist.setVisibility(true);
                newPlaylist.setName(command.getPlaylistName());
                newPlaylist.setCreationTimestamp(command.getTimestamp());
                playlists.get(i).getPlaylists().add(newPlaylist);
                out.put("message", "Playlist created successfully.");
            }
        }
        if (!okPlaylistCreated) {
            PlayList newPlaylistUser = new PlayList();
            newPlaylistUser.setUsername(command.getUsername());
            PlayListsByUser newPlaylist = new PlayListsByUser();
            newPlaylist.setVisibility(true);
            newPlaylist.setName(command.getPlaylistName());
            newPlaylist.setCreationTimestamp(command.getTimestamp());
            ArrayList<PlayListsByUser> nou = new ArrayList<>();
            newPlaylistUser.setPlaylists(nou);
            newPlaylistUser.getPlaylists().add(newPlaylist);
            playlists.add(newPlaylistUser);
            out.put("message", "Playlist created successfully.");

        }
    }

    /**
     * metoda care adauga melodii sau scoate melodii din playlist-ul unui utilizator
     *
     * @param command   comanda actuala
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param playlists clasa care contine tot ce se afla in library
     * @param loaded    lista cu tot ce este incarcat
     */
    public void addRemoveInPlayList(final Command command, final ObjectNode out,
                                    final ArrayList<PlayList> playlists,
                                    final ArrayList<Loaded> loaded,
                                    final ArrayList<Status> statuses, final LibraryInput library) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        boolean notSong = false;
        for (int i = 0; i < loaded.size(); i++) {
            if (loaded.get(i).getUser().equals(command.getUsername())) {
                if (!loaded.get(i).isLoaded()) {
                    out.put("message",
                            "Please load a source before adding to or removing from the playlist"
                                    + ".");
                    return;
                }
                if (!loaded.get(i).getTypeOfEntity().equals("song") && !loaded.get(i)
                        .getTypeOfEntity().equals("album")) {
                    notSong = true;
                }
                for (int j = 0; j < playlists.size(); j++) {
                    if (playlists.get(j).getUsername().equals(command.getUsername())) {
                        if (playlists.get(j).getPlaylists().size() >= command.getPlaylistId()) {
                            boolean songAlreadyThere = false;
                            for (int k = 0; k < playlists.get(j).getPlaylists()
                                    .get(command.getPlaylistId() - 1).getSongs().size(); k++) {
                                if (playlists.get(j).getPlaylists()
                                        .get(command.getPlaylistId() - 1).getSongs().get(k)
                                        .equals(loaded.get(i).getEntityLoaded())) {
                                    if (!notSong) {
                                        playlists.get(j).getPlaylists()
                                                .get(command.getPlaylistId() - 1).getSongs()
                                                .remove(k);
                                        playlists.get(j).getPlaylists()
                                                .get(command.getPlaylistId() - 1)
                                                .getSongTimestamps().remove(k);
                                        out.put("message", "Successfully removed from playlist.");
                                        return;
                                    }
                                }
                            }
                            if (!songAlreadyThere) {
                                if (!notSong) {
                                    if (loaded.get(i).getTypeOfEntity().equals("song")) {
                                        playlists.get(j).getPlaylists()
                                                .get(command.getPlaylistId() - 1).getSongs()
                                                .add(loaded.get(i).getEntityLoaded());
                                        playlists.get(j).getPlaylists()
                                                .get(command.getPlaylistId() - 1)
                                                .getSongTimestamps()
                                                .add(loaded.get(i).getSongTimestamp());

                                        out.put("message", "Successfully added to playlist.");
                                        return;
                                    } else {
                                        //cautam statusul utilizatorului curent
                                        for (int l = 0; l < statuses.size(); l++) {
                                            if (statuses.get(l).getUser()
                                                    .equals(command.getUsername())) {
                                                playlists.get(j).getPlaylists()
                                                        .get(command.getPlaylistId() - 1)
                                                        .getSongs()
                                                        .add(statuses.get(l).getStats().getName());
                                            }
                                            String song = statuses.get(l).getStats().getName();
                                            //cautam song in library
                                            for (int m = 0; m < library.getSongs().size(); m++) {
                                                if (library.getSongs().get(m).getName()
                                                        .equals(song)) {
                                                    playlists.get(j).getPlaylists()
                                                            .get(command.getPlaylistId() - 1)
                                                            .getSongTimestamps()
                                                            .add(library.getSongs().get(m)
                                                                    .getDuration());
                                                }
                                            }
                                        }
                                        out.put("message", "Successfully added to playlist.");
                                        return;
                                    }
                                }
                            }
                        } else {
                            out.put("message", "The specified playlist does not exist.");
                            return;
                        }
                        if (notSong) {
                            out.put("message", "The loaded source is not a song.");
                            return;
                        }
                    }
                }
                if (playlists.size() == 0) {
                    out.put("message", "The specified playlist does not exist.");
                    return;
                }
            }
        }

        out.put("message", "Please load a source before adding to or removing from the playlist.");
    }

    /**
     * metoda care afiseaza ca output al comenzii playlisturile unui utilizator
     *
     * @param command   comanda actuala
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param playlists clasa care contine tot ce se afla in library
     */
    public void showPlaylists(final Command command, final ObjectNode out,
                              final ArrayList<PlayList> playlists) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode playlistList = objectMapper.createArrayNode();
        for (int i = 0; i < playlists.size(); i++) {
            if (command.getUsername().equals(playlists.get(i).getUsername())) {
                for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                    ObjectNode playlist = objectMapper.createObjectNode();
                    playlist.put("name", playlists.get(i).getPlaylists().get(j).getName());
                    ArrayNode songs = objectMapper.createArrayNode();
                    for (int k = 0;
                         k < playlists.get(i).getPlaylists().get(j).getSongs().size(); k++) {
                        songs.add(playlists.get(i).getPlaylists().get(j).getSongs().get(k));
                    }
                    playlist.set("songs", songs);
                    if (playlists.get(i).getPlaylists().get(j).isVisibility()) {
                        playlist.put("visibility", "public");
                    }
                    if (!playlists.get(i).getPlaylists().get(j).isVisibility()) {
                        playlist.put("visibility", "private");
                    }
                    playlist.put("followers",
                            playlists.get(i).getPlaylists().get(j).getFollowers().size());
                    playlistList.add(playlist);
                }
            }
        }
        out.set("result", playlistList);
    }

    /**
     * metoda care schimba vizibilitatea unui playlist (private/public)
     *
     * @param command   comanda actuala
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param playlists clasa care contine tot ce se afla in library
     */
    public void swichVisibility(final Command command, final ObjectNode out,
                                final ArrayList<PlayList> playlists) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());

        for (int i = 0; i < playlists.size(); i++) {
            if (command.getUsername().equals(playlists.get(i).getUsername())) {
                if (command.getPlaylistId() <= playlists.get(i).getPlaylists().size()) {
                    if (playlists.get(i).getPlaylists().get(command.getPlaylistId() - 1)
                            .isVisibility()) {
                        playlists.get(i).getPlaylists().get(command.getPlaylistId() - 1)
                                .setVisibility(false);
                        out.put("message", "Visibility status updated successfully to private.");
                        return;
                    } else {
                        playlists.get(i).getPlaylists().get(command.getPlaylistId() - 1)
                                .setVisibility(true);
                        out.put("message", "Visibility status updated successfully to public.");
                        return;
                    }
                }
            }
        }
        out.put("message", "The specified playlist ID is too high.");

    }

    /**
     * metoda care afiseaza toate melodiile apreciate de un utilizator
     *
     * @param command         comanda actuala
     * @param out             "ObjectNode"-ul in care punem output-ul comenzii
     * @param likesByUsername lista cu utilizatorii si melodiile la cere fiecare a dat like
     */
    public void showPreferredSongs(final Command command, final ObjectNode out,
                                   final ArrayList<LikesByUsername> likesByUsername) {
        out.put("command", command.getCommand());
        out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode likedSongs = objectMapper.createArrayNode();
        for (int i = 0; i < likesByUsername.size(); i++) {
            if (likesByUsername.get(i).getUsername().equals(command.getUsername())) {
                for (int j = 0; j < likesByUsername.get(i).getLikedSongs().size(); j++) {
                    likedSongs.add(likesByUsername.get(i).getLikedSongs().get(j));
                }
            }
        }
        out.put("result", likedSongs);
    }

    /**
     * metoda care afiseaza top 5 cele mai apreciate playlisturi la output-ul comenzii
     *
     * @param command   comanda actuala
     * @param out       "ObjectNode"-ul in care punem output-ul comenzii
     * @param playlists clasa care contine tot ce se afla in library
     */
    public void getTop5Playlists(final Command command, final ObjectNode out,
                                 final ArrayList<PlayList> playlists) {
        out.put("command", command.getCommand());
        //out.put("user", command.getUsername());
        out.put("timestamp", command.getTimestamp());
        ArrayList<Integer> playlistScore = new ArrayList<>();
        ArrayList<String> playlistsNames = new ArrayList<>();
        ArrayList<Integer> playlistsIndexez = new ArrayList<>();
        ArrayList<Integer> playlistsCreationtime = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode topPlaylists = objectMapper.createArrayNode();
        int cnt = ZERO;
        for (int i = 0; i < playlists.size(); i++) {
            for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                playlistScore.add(playlists.get(i).getPlaylists().get(j).getFollowers().size());
                playlistsNames.add(playlists.get(i).getPlaylists().get(j).getName());
                playlistsIndexez.add(cnt);
                playlistsCreationtime.add(
                        playlists.get(i).getPlaylists().get(j).getCreationTimestamp());
                cnt++;
            }
        }
        for (int i = 0; i < playlistScore.size(); i++) {
            for (int j = i; j < playlistScore.size(); j++) {
                if (playlistScore.get(i) < playlistScore.get(j)) {
                    int aux = playlistScore.get(i);
                    playlistScore.set(i, playlistScore.get(j));
                    playlistScore.set(j, aux);
                    String auxName = playlistsNames.get(i);
                    playlistsNames.set(i, playlistsNames.get(j));
                    playlistsNames.set(j, auxName);
                    int aux2 = playlistsCreationtime.get(i);
                    playlistsCreationtime.set(i, playlistsCreationtime.get(j));
                    playlistsCreationtime.set(j, aux2);
                }
            }
        }

        for (int i = 0; i < playlistScore.size(); i++) {
            for (int j = 0; j < playlistScore.size(); j++) {
                if (playlistScore.get(i) == playlistScore.get(j)) {
                    if (playlistsCreationtime.get(i) < playlistsCreationtime.get(j)) {
                        int aux = playlistScore.get(i);
                        playlistScore.set(i, playlistScore.get(j));
                        playlistScore.set(j, aux);
                        String auxName = playlistsNames.get(i);
                        playlistsNames.set(i, playlistsNames.get(j));
                        playlistsNames.set(j, auxName);
                        int aux2 = playlistsCreationtime.get(i);
                        playlistsCreationtime.set(i, playlistsCreationtime.get(j));
                        playlistsCreationtime.set(j, aux2);
                    }
                }
            }
        }
        for (int i = 0; i < MAX && i < playlistScore.size(); i++) {
            topPlaylists.add(playlistsNames.get(i));
        }
        out.put("result", topPlaylists);
    }

    /**
     * metoda care afiseaza top 5 cele mai apreciate melodii (cu cele mai multe like uri)
     *
     * @param command     comanda actuala
     * @param out         "ObjectNode"-ul in care punem output-ul comenzii
     * @param likesBySong lista cu melodiile si utilizatorii care au dat like fiecarei melodii
     * @param library     clasa care contine tot ce se afla in library
     */
    public void getTop5Songs(final Command command, final ObjectNode out,
                             final ArrayList<LikesBySong> likesBySong,
                             final LibraryInput library) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode topSongs = objectMapper.createArrayNode();
        ArrayList<String> songNames = new ArrayList<>();
        ArrayList<Integer> songLikes = new ArrayList<>();
        ArrayList<String> songAlbums = new ArrayList<>();
        for (int i = 0; i < likesBySong.size(); i++) {
            songNames.add(likesBySong.get(i).getSongName());
            songLikes.add(likesBySong.get(i).getUsersThatLiked().size());
            songAlbums.add(likesBySong.get(i).getSongAlbum());
        }
        for (int i = 0; i < songNames.size(); i++) {
            for (int j = 0; j < songNames.size(); j++) {
                if (songLikes.get(i) > songLikes.get(j)) {
                    int aux = songLikes.get(i);
                    songLikes.set(i, songLikes.get(j));
                    songLikes.set(j, aux);
                    String auxName = songNames.get(i);
                    songNames.set(i, songNames.get(j));
                    songNames.set(j, auxName);
                    String auxAlbumName = songAlbums.get(i);
                    songAlbums.set(i, songAlbums.get(j));
                    songAlbums.set(j, auxAlbumName);
                }
                if (songLikes.get(i) == songLikes.get(j)) {
                    int indexI = ZERO;
                    int indexJ = ZERO;
                    for (int k = 0; k < library.getSongs().size(); k++) {
                        if (library.getSongs().get(k).getName().equals(songNames.get(i))) {
                            if (library.getSongs().get(k).getAlbum().equals(songAlbums.get(i))
                                    || songAlbums.get(i).equals("notAlbum")) {
                                indexI = k;
                            }
                        }
                        if (library.getSongs().get(k).getName().equals(songNames.get(j))) {
                            if (library.getSongs().get(k).getAlbum().equals(songAlbums.get(j))
                                    || songAlbums.get(j).equals("notAlbum")) {
                                indexJ = k;
                            }
                        }
                    }
                    if (indexI < indexJ) {
                        int aux = songLikes.get(i);
                        songLikes.set(i, songLikes.get(j));
                        songLikes.set(j, aux);
                        String auxName = songNames.get(i);
                        songNames.set(i, songNames.get(j));
                        songNames.set(j, auxName);
                        String auxAlbumName = songAlbums.get(i);
                        songAlbums.set(i, songAlbums.get(j));
                        songAlbums.set(j, auxAlbumName);
                    }
                }
            }
        }
        for (int i = 0; i < MAX && i < songNames.size(); i++) {
            topSongs.add(songNames.get(i));
        }
        for (int i = 0; i < MAX - songNames.size(); i++) {
            topSongs.add(library.getSongs().get(i).getName());
        }

        out.put("result", topSongs);
    }
}

