package main.programInterface;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.database.*;

import java.util.ArrayList;

public final class UserManagement {
    private ArrayList<UserDatabase> users = new ArrayList<>();

    public ArrayList<UserDatabase> getUsers() {
        return users;
    }

    /**
     * metoda pentru a schimba statusul online/offline al unui utilizator
     *
     * @param command
     * @param out
     */
    public void switchConnectionStatus(final Command command, final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        out.put("user", command.getUsername());
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                //verific daca este user normal, iar daca nu afisez eroare
                if (!user.getType().equals("user")) {
                    out.put("message", command.getUsername() + " is not a normal user.");
                    return;
                }
                if (user.getConnectionStatus().equals("online")) {
                    user.setConnectionStatus("offline");
                } else {
                    user.setConnectionStatus("online");
                }
                out.put("message", command.getUsername() + " has changed status successfully.");
                return;
            }
        }
        out.put("message", "The username " + command.getUsername() + " doesn't exist.");
    }

    /**
     * metoda pentru a adauga un utilizator
     *
     * @param command
     * @param out
     * @return
     */
    public String addUser(final Command command, final ObjectNode out) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                out.put("user", command.getUsername());
                out.put("message", "The username " + command.getUsername() + " is already taken.");
                return "error";
            }
        }
        UserDatabase newUser = new UserDatabase();
        newUser.setAge(command.getAge());
        newUser.setCity(command.getCity());
        newUser.setUsername(command.getUsername());
        newUser.setType(command.getType());
        newUser.setConnectionStatus("online");
        if (command.getType().equals("user")) {
            newUser.setPageType("home");
        }
        users.add(newUser);
        out.put("user", command.getUsername());
        out.put("message",
                "The username " + command.getUsername() + " has been added successfully.");
        return command.getType();
    }

    /**
     * metoda pentru verificarea daca stergerea unui utilizator este posibila
     * metoda auxiliara pentru metoda "deleteUser"
     */
    public String deleteUserVerify(final Command command, final ObjectNode out,
                                 final ArrayList<SearchedSongsByUsers> searched,
                                 final ArrayList<PlayList> playlists,
                                 final ArrayList<Loaded> loaded) {
        //daca gasim un utilizator care are la search acest artist, atunci
        //nu se poate sterge acesta
        //cautam in searchedsongsbyusers
        for (SearchedSongsByUsers searchedSongsByUsers : searched) {
            //daca searchtype este artist
            if (searchedSongsByUsers.getSearchType().equals("artist")) {
                //iteram prin lista de artisti
                for (int i = 0; i < searchedSongsByUsers.getArtists().size(); i++) {
                    if (searchedSongsByUsers.getArtists().get(i).equals(command.getUsername())) {
                        out.put("user", command.getUsername());
                        out.put("message", command.getUsername() + " can't be deleted.");
                        return "error";
                    }
                }
            }
            //daca searchtype este host
            if (searchedSongsByUsers.getSearchType().equals("host")) {
                //iteram prin lista de hosti
                for (int i = 0; i < searchedSongsByUsers.getHosts().size(); i++) {
                    if (searchedSongsByUsers.getHosts().get(i).equals(command.getUsername())) {
                        out.put("user", command.getUsername());
                        out.put("message", command.getUsername() + " can't be deleted.");
                        return "error";
                    }
                }
            }
        }
        //daca gassim un utilizator care asculta un playlist al acestui artist/host
        //atunci nu se poate sterge acesta
        //luam fiecare playlist al acestui utilizator din playlists
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getUsername().equals(command.getUsername())) {
                //iteram prin fiecare playlist
                for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                    //iteram prin loaded si verificam daca cineva asculta acest playlist
                    for (int k = 0; k < loaded.size(); k++) {
                        if (loaded.get(k).getEntityLoaded()
                                .equals(playlists.get(i).getPlaylists().get(j).getName())
                                && loaded.get(k).isLoaded()) {
                            out.put("user", command.getUsername());
                            out.put("message", command.getUsername() + " can't be deleted.");
                            return "error";
                        }
                    }

                }
            }
        }

        //daca un utilizator se afla pe pagina acestui artist/host
        //nu pot sa il sterg
        //caut in users daca acestia au pagina deschisa
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLocationName() != null) {
                if (users.get(i).getLocationName().equals(command.getUsername())) {
                    if (users.get(i).getPageType().equals("artist") || users.get(i).getPageType()
                            .equals("host")) {
                        out.put("user", command.getUsername());
                        out.put("message", command.getUsername() + " can't be deleted.");
                        return "error";
                    }
                }
            }
        }
        return "notError";
    }
    /**
     * metoda pentru a sterge un utilizator
     */
    public String deleteUser(final Command command, final ObjectNode out,
                             final ArrayList<Status> statuses, final PageManagementHub pages,
                             final ArrayList<SearchedSongsByUsers> searched,
                             final ArrayList<PlayList> playlists, final ArrayList<Loaded> loaded) {
        out.put("command", command.getCommand());
        out.put("timestamp", command.getTimestamp());
        for (UserDatabase user : users) {
            if (user.getUsername().equals(command.getUsername())) {
                if (user.getType().equals("artist")) {
                    //iteram prin toate statusurile si verificam daca vreun utilizator asculta
                    //vreo melodie din albumurile acestui utilizator
                    for (Status status : statuses) {
                        if (status.getTypeOfListening().equals("playlist")
                                || status.getTypeOfListening().equals("song")
                                || status.getTypeOfListening().equals("album")) {
                            //iteram prin paginile utilizatorilor
                            ArrayList<ArtistPage> artistPages = pages.getArtistPages();
                            for (ArtistPage artistPage : artistPages) {
                                if (artistPage.getName().equals(command.getUsername())) {
                                    //daca gasim un utilizator care asculta o melodie din albumul
                                    //artistului pe care vrem sa il stergem, atunci nu se poate
                                    //sterge acesta
                                    //iteream prin melodiile din fiecare album
                                    for (int i = 0; i < artistPage.getAlbums().size(); i++) {
                                        for (int j = 0;
                                             j < artistPage.getAlbums().get(i).getSongs()
                                                     .size(); j++) {
                                            if (artistPage.getAlbums().get(i).getSongs().get(j)
                                                    .getName()
                                                    .equals(status.getStats().getName())) {

                                                out.put("user", command.getUsername());
                                                out.put("message", command.getUsername()
                                                        + " can't be deleted.");
                                                return "error";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (user.getType().equals("host")) {
                    //iteram prin toate statusurile si verificam daca vreun utilizator asculta
                    //vreun episod al unui podcast al acestui utilizator
                    for (Status status : statuses) {
                        if (status.getTypeOfListening().equals("podcast")) {
                            //iteram prin paginile utilizatorilor
                            ArrayList<HostPage> hostPages = pages.getHostPages();
                            for (HostPage hostPage : hostPages) {
                                if (hostPage.getName().equals(command.getUsername())) {
                                    //daca gasim un utilizator care asculta un episod al
                                    //podcastului hostului pe care vrem sa il stergem, atunci
                                    //nu se poate sterge acesta
                                    //iteream prin fiecare episod
                                    for (int i = 0; i < hostPage.getPodcasts().size(); i++) {
                                        for (int j = 0;
                                             j < hostPage.getPodcasts().get(i).getEpisodes()
                                                     .size(); j++) {
                                            if (hostPage.getPodcasts().get(i).getEpisodes().get(j)
                                                    .getName()
                                                    .equals(status.getStats().getName())) {
                                                out.put("user", command.getUsername());
                                                out.put("message", command.getUsername()
                                                        + " can't be deleted.");
                                                return "error";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (deleteUserVerify(command, out, searched, playlists, loaded).equals("error")) {
                    return "error";
                }
                users.remove(user);
                out.put("user", command.getUsername());
                out.put("message", command.getUsername() + " was successfully deleted.");
                //stergem playlisturile userului din playlists
                for (int i = 0; i < playlists.size(); i++) {
                    if (playlists.get(i).getUsername().equals(command.getUsername())) {
                        playlists.remove(i);
                        i--;
                    }
                }
                //stergem unde acesta a dat follow in playlists
                for (int i = 0; i < playlists.size(); i++) {
                    for (int j = 0; j < playlists.get(i).getPlaylists().size(); j++) {
                        for (int k = 0; k < playlists.get(i).getPlaylists().get(j).getFollowers()
                                .size(); k++) {
                            if (playlists.get(i).getPlaylists().get(j).getFollowers().get(k)
                                    .equals(command.getUsername())) {
                                playlists.get(i).getPlaylists().get(j).getFollowers().remove(k);
                                k--;
                            }
                        }
                    }
                }
                //stergem melodiile din likedsongs ale acestui utilizator

                return "success";
            }
        }
        out.put("user", command.getUsername());
        out.put("message", "The username " + command.getUsername() + " doesn't exist.");
        return "error";
    }
}
