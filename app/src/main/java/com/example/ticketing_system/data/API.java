package com.example.ticketing_system.data;

public class API {

    //-------------------------------------------------------RAPID API-------------------------------------------------------//

    private String get_all_teams = "https://api-football-v1.p.rapidapi.com/v2/teams/league/3456";
    private String get_all_fixtures = "https://api-football-v1.p.rapidapi.com/v2/fixtures/league/3456";

    //----------------------------------------------------------API----------------------------------------------------------//

    public String login = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/login.php"; //login api
    public String signup = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/signup.php"; //signup api
    public String pass_change = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/change_password.php"; //change password
    public String prof_change = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/change_profile.php"; //change profile
    public String fixtures = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/fixtures.php"; //get all fixtures
    public String buy_ticket = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/buy_ticket.php"; //buy ticket
    public String my_tickets = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/my_tickets.php"; //get my tickets
    public String available_tickets = "https://ticketingsystemfscg.000webhostapp.com/ticketing_api/available_tickets.php"; //get my tickets

    public String getLogin() {return login; }
    public String getSignup() {return signup; }
    public String getPass_change() {return pass_change; }
    public String getProf_change() {return prof_change; }
    public String getFixtures() {return fixtures; }
    public String getBuy_ticket() {return buy_ticket; }
    public String getMy_tickets() {return my_tickets; }
    public String getAvailable_tickets() {return available_tickets; }

}


