package main.database;

import fileio.input.EpisodeInput;

import java.util.ArrayList;

public final class Podcast {
    private String name;
    private ArrayList<EpisodeInput> episodes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }
}
