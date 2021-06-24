package com.example.ticketing_system.data;

public class Fixture {

    private String home;
    private String away;
    private String date;
    private String time;
    private String home_logo;
    private String away_logo;

    public boolean isLoaded = false;

    public Fixture(String home, String away, String date, String time, String home_logo, String away_logo) {
        this.home = home;
        this.away = away;
        this.date = date;
        this.time = time;
        this.home_logo = home_logo;
        this.away_logo = away_logo;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHome_logo(String home_logo) {
        this.home_logo = home_logo;
    }

    public void setAway_logo(String away_logo) {
        this.away_logo = away_logo;
    }

    public String getHome() {
        return home;
    }

    public String getAway() {
        return away;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getHome_logo() {
        return home_logo;
    }

    public String getAway_logo() {
        return away_logo;
    }

}
