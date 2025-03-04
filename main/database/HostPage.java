package main.database;

import java.util.ArrayList;

public final class HostPage implements PageInterface {
    private String name;
    private ArrayList<Podcast> podcasts = new ArrayList<>();
    private ArrayList<Announcement> announcements = new ArrayList<>();

    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(final ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }
}
