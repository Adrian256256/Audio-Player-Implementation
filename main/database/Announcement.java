package main.database;

public final class Announcement {
    private String name;
    private String description;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name numele anuntului
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description descrierea anuntului
     */
    public void setDescription(final String description) {
        this.description = description;
    }
}
