package com.example.ticketing_system.data;

public class API {
    public String login = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/login.php"; //login api
    public String signup = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/signup.php"; //signup api
    public String pass_change = "https:///ticketingsystemfscg.000webhostapp.com/ticketing_api/change_password.php"; //change password
    public String prof_change = "https:///ticketingsystemfscg.000webhostapp.com/ticketing_api/change_profile.php"; //change profile

    public String getLogin() { return login; }
    public String getSignup() { return signup; }
    public String getPass_change() {return pass_change; }
    public String getProf_change() {return prof_change; }
}


