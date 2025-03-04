package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.LibraryInput;
import main.database.Command;
import main.database.UserDatabase;
import main.programInterface.ExecuteCommand;
import main.programInterface.GeneralStatistics;
import main.programInterface.PrincipalDatabase;
import main.programInterface.SearchBar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;


/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput  for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */

    public static void action(final String filePathInput, final String filePathOutput)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);
        ArrayNode outputs = objectMapper.createArrayNode();
        ExecuteCommand executeCommand = ExecuteCommand.getInstance();
        ArrayList<Command> commands = objectMapper.readValue(new File("input/" + filePathInput),
                new TypeReference<>() { });
        executeCommand.setCommands(commands);
        SearchBar searchBar = SearchBar.getInstance(); //initializam un searchbar
        PrincipalDatabase principalDatabase = new PrincipalDatabase(); //database separat pe test
        ArrayList<String> searched = null; //stocam datele despre cautari si userii care le-au facut
        GeneralStatistics statistics = GeneralStatistics.getInstance();
        for (int i = 0; i < library.getUsers().size(); i++) {
            UserDatabase user = new UserDatabase();
            user.fillUserDatabase(library.getUsers().get(i));
            principalDatabase.getUsers().getUsers().add(user);
        }
        for (int i = 0; i < ExecuteCommand.getInstance().getCommands().size(); i++) {
            Command command = ExecuteCommand.getInstance().getCommands().get(i);
            //prelucram comanda curenta
            if (command.getCommand().equals("search")) { //verificam ce tip de comanda este
                executeCommand.search(command, library, outputs, searchBar, searched,
                        principalDatabase);
            } //apelam functiile specifice fiecarei comenzi
            if (command.getCommand().equals("select")) {
                executeCommand.select(command, searchBar, outputs, principalDatabase);
            }
            if (command.getCommand().equals("load")) {
                executeCommand.load(command, library, outputs, principalDatabase);
            }
            if (command.getCommand().equals("status")) {
                executeCommand.status(command, library, outputs, principalDatabase);
            }
            if (command.getCommand().equals("playPause")) {
                executeCommand.playPause(command, library, outputs, principalDatabase);
            }
            if (command.getCommand().equals("createPlaylist")) {
                executeCommand.createPlaylist(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("like")) {
                executeCommand.like(command, library, outputs, principalDatabase);
            }
            if (command.getCommand().equals("addRemoveInPlaylist")) {
                executeCommand.addRemoveInPlaylist(command, library, outputs, principalDatabase);
            }
            if (command.getCommand().equals("showPlaylists")) {
                executeCommand.showPlaylists(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("showPreferredSongs")) {
                executeCommand.showPreferredSongs(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("repeat")) {
                executeCommand.repeat(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("shuffle")) {
                executeCommand.shuffle(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("next")) {
                executeCommand.next(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("prev")) {
                executeCommand.prev(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("forward")) {
                executeCommand.forward(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("backward")) {
                executeCommand.backward(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("follow")) {
                executeCommand.follow(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("switchVisibility")) {
                executeCommand.switchVisibility(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("getTop5Playlists")) {
                executeCommand.getTop5Playlists(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("getTop5Songs")) {
                executeCommand.getTop5Songs(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("switchConnectionStatus")) {
                executeCommand.switchConnectionStatus(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("getOnlineUsers")) {
                executeCommand.getOnlineUsers(command, outputs, statistics, principalDatabase);
            }
            if (command.getCommand().equals("addUser")) {
                executeCommand.addUser(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("addAlbum")) {
                executeCommand.addAlbum(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("addPodcast")) {
                executeCommand.addPodcast(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("removePodcast")) {
                executeCommand.removePodcast(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("removeAlbum")) {
                executeCommand.removeAlbum(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("showAlbums")) {
                executeCommand.showAlbums(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("showPodcasts")) {
                executeCommand.showPodcasts(command, outputs, principalDatabase.getPages());
            }
            if (command.getCommand().equals("printCurrentPage")) {
                executeCommand.printCurrentPage(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("changePage")) {
                executeCommand.changePage(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("addEvent")) {
                executeCommand.addEvent(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("removeEvent")) {
                executeCommand.removeEvent(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("addAnnouncement")) {
                executeCommand.addAnnouncement(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("removeAnnouncement")) {
                executeCommand.removeAnnouncement(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("addMerch")) {
                executeCommand.addMerch(command, outputs, principalDatabase);
            }
            if (command.getCommand().equals("getAllUsers")) {
                executeCommand.getAllUsers(command, outputs, statistics, principalDatabase);
            }
            if (command.getCommand().equals("deleteUser")) {
                executeCommand.deleteUser(command, outputs, library, principalDatabase);
            }
            if (command.getCommand().equals("getTop5Albums")) {
                executeCommand.getTop5Albums(command, outputs, statistics, principalDatabase);
            }
            if (command.getCommand().equals("getTop5Artists")) {
                executeCommand.getTop5Artists(command, outputs, statistics, principalDatabase);
            }
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
