package main.programInterface;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.Command;
import main.database.LikesBySong;
import main.database.UserDatabase;

import java.util.ArrayList;

public final class GeneralStatistics {
    private static final int MAX = 5;
    private static GeneralStatistics instance = null;

    public GeneralStatistics() {

    }

    /**
     * metoda pentru a creea un obiect de tip GeneralStatistics
     *
     * @return obiectul de tip GeneralStatistics
     */
    public static GeneralStatistics getInstance() {
        if (instance == null) {
            synchronized (GeneralStatistics.class) {
                if (instance == null) {
                    instance = new GeneralStatistics();
                }
            }
        }
        return instance;
    }

    /**
     * metoda pentru a afisa top 5 cei mai ascultati artisti
     * @param command comanda primita
     * @param out obiectul de tip ObjectNode in care se va pune output-ul
     * @param likesBySongs lista cu melodiile apreciate
     * @param pages lista cu paginile
     */
    public void getTop5Artists(final Command command, final ObjectNode out,
                               final ArrayList<LikesBySong> likesBySongs,
                               final PageManagementHub pages) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        ArrayList<String> top5Artists = new ArrayList<>();
        ArrayList<String> artists = new ArrayList<>(); //lista cu numele artistilor
        ArrayList<Integer> likesOfArtists =
                new ArrayList<>(); //lista cu numarul de likeuri ale artistilor
        //iterez prin toate artistii din pages si numar likeurile melodiilor din fiecare album
        //melodiile apreciate sunt in lista de likesBySongs
        //iau fiecare artistpage si iterez prin toate albumele
        for (int i = 0; i < pages.getArtistPages().size(); i++) {
            int likes = 0;
            for (int j = 0; j < pages.getArtistPages().get(i).getAlbums().size(); j++) {
                for (int k = 0;
                     k < pages.getArtistPages().get(i).getAlbums().get(j).getSongs().size(); k++) {
                    //acum caut in likes by song
                    for (int l = 0; l < likesBySongs.size(); l++) {
                        if (likesBySongs.get(l).getSongName()
                                .equals(pages.getArtistPages().get(i).getAlbums().get(j).getSongs()
                                        .get(k).getName())) {
                            if (likesBySongs.get(l).getSongAlbum()
                                    .equals(pages.getArtistPages().get(i).getAlbums().get(j)
                                            .getName()) || likesBySongs.get(l).getSongAlbum()
                                    .equals("notAlbum")) {
                                likes += likesBySongs.get(l).getUsersThatLiked().size();
                            }
                        }
                    }
                }
            }
            artists.add(pages.getArtistPages().get(i).getName());
            likesOfArtists.add(likes);
        }
        //ordonez crescator dupa numarul de likeuri
        for (int i = 0; i < likesOfArtists.size() - 1; i++) {
            for (int j = i + 1; j < likesOfArtists.size(); j++) {
                if (likesOfArtists.get(i) < likesOfArtists.get(j)) {
                    int aux = likesOfArtists.get(i);
                    likesOfArtists.set(i, likesOfArtists.get(j));
                    likesOfArtists.set(j, aux);
                    String aux2 = artists.get(i);
                    artists.set(i, artists.get(j));
                    artists.set(j, aux2);
                }
                if (likesOfArtists.get(i) == likesOfArtists.get(j)) {
                    //ordonare lexicografica generata cu Github Copilot
                    if (artists.get(i).compareTo(artists.get(j)) > 0) {
                        String aux2 = artists.get(i);
                        artists.set(i, artists.get(j));
                        artists.set(j, aux2);
                        int aux = likesOfArtists.get(i);
                        likesOfArtists.set(i, likesOfArtists.get(j));
                        likesOfArtists.set(j, aux);

                    }
                }
            }
        }
        //adaug in top5Artists primele 5 artisti
        for (int i = 0; i < MAX && i < artists.size(); i++) {
            top5Artists.add(artists.get(i));
        }
        out.putPOJO("result", top5Artists);
    }

    /**
     * metoda pentru a afisa top 5 cele mai ascultate genuri
     * @param command comanda primita
     * @param out obiectul de tip ObjectNode in care se va pune output-ul
     * @param likesBySongs lista cu melodiile apreciate
     * @param pages lista cu paginile
     */
    public void getTop5Albums(final Command command, final ObjectNode out,
                              final ArrayList<LikesBySong> likesBySongs,
                              final PageManagementHub pages) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        ArrayList<String> top5Albums = new ArrayList<>();
        ArrayList<String> albums = new ArrayList<>(); //lista cu numele alnumelor
        ArrayList<Integer> likesOfAlbums =
                new ArrayList<>(); //lista cu numarul de likeuri ale albumelor
        //iterez prin toate albumele din pages si numar likeurile melodiilor din fiecare album
        //melodiile apreciate sunt in lista de likesBySongs
        //iau fiecare artistpage si iterez prin toate albumele
        for (int i = 0; i < pages.getArtistPages().size(); i++) {
            for (int j = 0; j < pages.getArtistPages().get(i).getAlbums().size(); j++) {
                int likes = 0;
                for (int k = 0;
                     k < pages.getArtistPages().get(i).getAlbums().get(j).getSongs().size(); k++) {
                    //acum caut in likes by song
                    for (int l = 0; l < likesBySongs.size(); l++) {
                        if (likesBySongs.get(l).getSongName()
                                .equals(pages.getArtistPages().get(i).getAlbums().get(j).getSongs()
                                        .get(k).getName())) {
                            if (likesBySongs.get(l).getSongAlbum()
                                    .equals(pages.getArtistPages().get(i).getAlbums().get(j)
                                            .getName()) || likesBySongs.get(l).getSongAlbum()
                                    .equals("notAlbum")) {
                                likes += likesBySongs.get(l).getUsersThatLiked().size();
                            }
                        }
                    }
                }
                albums.add(pages.getArtistPages().get(i).getAlbums().get(j).getName());
                likesOfAlbums.add(likes);
            }
        }
        //ordonez crescator dupa numarul de likeuri
        for (int i = 0; i < likesOfAlbums.size() - 1; i++) {
            for (int j = i + 1; j < likesOfAlbums.size(); j++) {
                if (likesOfAlbums.get(i) < likesOfAlbums.get(j)) {
                    int aux = likesOfAlbums.get(i);
                    likesOfAlbums.set(i, likesOfAlbums.get(j));
                    likesOfAlbums.set(j, aux);
                    String aux2 = albums.get(i);
                    albums.set(i, albums.get(j));
                    albums.set(j, aux2);
                }
                if (likesOfAlbums.get(i) == likesOfAlbums.get(j)) {
                    //ordonare lexicografica generata cu Github Copilot
                    if (albums.get(i).compareTo(albums.get(j)) > 0) {
                        String aux2 = albums.get(i);
                        albums.set(i, albums.get(j));
                        albums.set(j, aux2);
                        int aux = likesOfAlbums.get(i);
                        likesOfAlbums.set(i, likesOfAlbums.get(j));
                        likesOfAlbums.set(j, aux);

                    }
                }
            }
        }
        //adaug in top5Albums primele 5 albume
        for (int i = 0; i < MAX && i < albums.size(); i++) {
            top5Albums.add(albums.get(i));
        }
        out.putPOJO("result", top5Albums);
    }

    /**
     * metoda pentru a afisa utilizatorii online
     *
     * @param command
     * @param out
     */
    public void getOnlineUsers(final Command command, final ObjectNode out,
                               final ArrayList<UserDatabase> users) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        ArrayList<String> onlineUsers = new ArrayList<>();
        for (UserDatabase user : users) {
            if (user.getConnectionStatus().equals("online")) {
                if (user.getType().equals("user")) {
                    onlineUsers.add(user.getUsername());
                }
            }
        }
        out.putPOJO("result", onlineUsers);
    }

    /**
     * metoda pentru a afisa toti utilizatorii
     * @param command comanda primita
     * @param out obiectul de tip ObjectNode in care se va pune output-ul
     * @param users lista cu utilizatori
     */
    public void getAllUsers(final Command command, final ObjectNode out,
                            final ArrayList<UserDatabase> users) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        ArrayList<String> allUsers = new ArrayList<>();
        //printam in ordinea user, artist, host
        for (UserDatabase user : users) {
            if (user.getType().equals("user")) {
                allUsers.add(user.getUsername());
            }
        }
        for (UserDatabase user : users) {
            if (user.getType().equals("artist")) {
                allUsers.add(user.getUsername());
            }
        }
        for (UserDatabase user : users) {
            if (user.getType().equals("host")) {
                allUsers.add(user.getUsername());
            }
        }
        out.putPOJO("result", allUsers);
    }
}
