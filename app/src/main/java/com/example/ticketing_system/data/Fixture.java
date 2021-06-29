package com.example.ticketing_system.data;

public class Fixture {

    private String id;
    private String home;
    private String away;
    private String date;
    private String time;
    private String home_logo;
    private String away_logo;
    private String stadium;
    private String created_at;
    private String ticket_id;

    public boolean isLoaded = false;

    public Fixture(String id,String home, String away, String date, String time, String home_logo, String away_logo,String stadium) {
        this.home = home;
        this.away = away;
        this.date = date;
        this.time = time;
        this.home_logo = home_logo;
        this.away_logo = away_logo;
        this.stadium = stadium;
        this.id = id;
    }

    public Fixture(String home, String away, String home_logo, String away_logo, String created_at, String ticket_id,String time) {
        this.home = home;
        this.away = away;
        this.home_logo = home_logo;
        this.away_logo = away_logo;
        this.created_at = created_at;
        this.ticket_id = ticket_id;
        this.time = time;
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

    public void setStadium(String stadium) {
        this.stadium = stadium;
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

    public String getStadium() {
        return stadium;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }
}
