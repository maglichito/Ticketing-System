package com.example.ticketing_system.data;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String username;
    private String email;

    public User(int id, String username, String email,String name, String lastname) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.name = name;
        this.lastname = lastname;
    }
    public void setName(String name) { this.name = name; }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getName() { return name; }

    public String getLastname() { return lastname; }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


