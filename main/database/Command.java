package main.database;

import fileio.input.EpisodeInput;
import fileio.input.SongInput;

import java.util.ArrayList;

public final class Command {
    private final String command = null;
    private String username;
    private int timestamp;
    private String type;
    private Filter filters;
    private Integer itemNumber;
    private final String playlistName = null;
    private final Integer playlistId = null;
    private int seed;
    private int age;
    private String city;
    private String name;
    private String description;
    private String releaseYear;
    private String date;
    private int price;
    private ArrayList<EpisodeInput> episodes = new ArrayList<>();
    private String nextPage;
    private ArrayList<SongInput> songs = new ArrayList<>();
    public void setUsername(final String username) {
        this.username = username;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }

    public Command() {
    }

    public int getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public int getSeed() {
        return seed;
    }


    public int getPlaylistId() {
        return playlistId;
    }


    public String getPlaylistName() {
        return playlistName;
    }


    public String getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }


    public int getTimestamp() {
        return timestamp;
    }


    public String getType() {
        return type;
    }


    public Filter getFilters() {
        return filters;
    }


    public Integer getItemNumber() {
        return itemNumber;
    }

}
