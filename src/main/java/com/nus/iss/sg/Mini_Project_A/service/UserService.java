package com.nus.iss.sg.Mini_Project_A.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nus.iss.sg.Mini_Project_A.model.User;
import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;
import com.nus.iss.sg.Mini_Project_A.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Check if a username already exists
    public boolean isUsernameTaken(String username) {
        return userRepository.userExists(username);
    }

    // Register a new user
    public void registerUser(String username, String password) {
        if (isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        User user = new User(username, password);
        userRepository.saveUser(user); // Save the user via UserRepository
    }

    // Authenticate a user by username and password
    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    // Add a crypto entry to the user's watchlist
    public void addCryptoToWatchlist(String username, WatchlistEntry entry) {
        List<WatchlistEntry> watchlist = userRepository.getWatchlist(username);

        // Prevent duplicates in the watchlist
        boolean exists = watchlist.stream()
                .anyMatch(existingEntry -> existingEntry.getId().equals(entry.getId()));

        if (!exists) {
            watchlist.add(entry);
            userRepository.saveWatchlist(username, watchlist);
        }
    }

    // Remove a crypto entry from the user's watchlist
    public void removeCryptoFromWatchlist(String username, String cryptoId) {
        List<WatchlistEntry> watchlist = userRepository.getWatchlist(username);

        // Remove the entry by ID
        watchlist.removeIf(entry -> entry.getId().equals(cryptoId));
        userRepository.saveWatchlist(username, watchlist);
    }

    // Retrieve the user's watchlist
    public List<WatchlistEntry> getCryptoWatchlist(String username) {
        return userRepository.getWatchlist(username);
    }

    // Retrieve all users (for admin or debugging purposes)
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAllUsers().values());
    }
}