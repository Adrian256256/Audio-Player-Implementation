package main.database;

import fileio.input.UserInput;

public final class UserDatabase {
    private String username;
    private int age;
    private String city;
    private String type;
    private String connectionStatus;
    private String pageType;
    private String locationName;


    public String getPageType() {
        return pageType;
    }

    public void setPageType(final String pageType) {
        this.pageType = pageType;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(final String locationName) {
        this.locationName = locationName;
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

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(final String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public UserDatabase() {
    }

    public UserDatabase(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * metoda care umple baza de date de useri cu userii din fisierul de input
     * @param userDatabase
     */
    public void fillUserDatabase(final UserInput userDatabase) {
        this.username = userDatabase.getUsername();
        this.age = userDatabase.getAge();
        this.city = userDatabase.getCity();
        this.type = "user";
        this.connectionStatus = "online";
        this.pageType = "home";
    }
}
