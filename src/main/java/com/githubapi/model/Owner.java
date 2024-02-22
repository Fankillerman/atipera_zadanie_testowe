package com.githubapi.model;

public class Owner {
    private String login;

    public Owner() {}

    public Owner(String login) {
        this.login = login;
    }
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
