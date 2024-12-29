package com.nus.iss.sg.Mini_Project_A.repo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nus.iss.sg.Mini_Project_A.model.User;
import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;

import jakarta.annotation.PostConstruct;

@Repository
public class UserRepository {

    private static final String USER_KEY = "user:";
    private static final String WATCHLIST_SUFFIX = ":watchlist";

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, User> hashOperations;

    public UserRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    // Save a user to Redis
    public void saveUser(User user) {
        hashOperations.put(USER_KEY, user.getUsername(), user);
    }

    // Retrieve a user by username
    public User findUserByUsername(String username) {
        User user = hashOperations.get(USER_KEY, username);
        return user;
    }

    // Retrieve all users
    public Map<String, User> findAllUsers() {
        return hashOperations.entries(USER_KEY);
    }

    // Check if a user exists
    public boolean userExists(String username) {
        return hashOperations.hasKey(USER_KEY, username);
    }

    // Delete a user by username
    public void deleteUser(String username) {
        hashOperations.delete(USER_KEY, username);
    }

    // Retrieve the watchlist for a specific user
    public List<WatchlistEntry> getWatchlist(String username) {
        String userWatchlistKey = USER_KEY + username + WATCHLIST_SUFFIX;
        List<Object> entries = redisTemplate.opsForList().range(userWatchlistKey, 0, -1);

        if (entries == null || entries.isEmpty()) {
            return new ArrayList<>();
        }

        return entries.stream()
                .map(obj -> {
                    if (obj instanceof WatchlistEntry) {
                        return (WatchlistEntry) obj;
                    } else if (obj instanceof LinkedHashMap) {
                        // Deserialize JSON to WatchlistEntry
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.convertValue(obj, WatchlistEntry.class);
                    }
                    throw new IllegalArgumentException("Invalid data type retrieved from Redis");
                })
                .collect(Collectors.toList());
    }

    // Save the updated watchlist for a user
    public void saveWatchlist(String username, List<WatchlistEntry> watchlist) {
        String userWatchlistKey = USER_KEY + username + WATCHLIST_SUFFIX;

        // Clear existing data in Redis
        redisTemplate.delete(userWatchlistKey);

        // Save the new watchlist
        for (WatchlistEntry entry : watchlist) {
            redisTemplate.opsForList().rightPush(userWatchlistKey, entry);
        }
    }
}