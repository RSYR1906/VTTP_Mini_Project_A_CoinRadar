package com.nus.iss.sg.Mini_Project_A.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class User {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters.")
    private String password;

    private List<String> cryptoWatchlist = new ArrayList<>();
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getCryptoWatchlist() {
        return cryptoWatchlist;
    }

    public void setCryptoWatchlist(List<String> cryptoWatchlist) {
        this.cryptoWatchlist = cryptoWatchlist;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", password=" + password + "]";
    }

}
