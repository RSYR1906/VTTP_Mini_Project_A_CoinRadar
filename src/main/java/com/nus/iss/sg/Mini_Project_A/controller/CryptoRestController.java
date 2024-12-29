package com.nus.iss.sg.Mini_Project_A.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nus.iss.sg.Mini_Project_A.model.CryptoData;
import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;
import com.nus.iss.sg.Mini_Project_A.service.CryptoService;
import com.nus.iss.sg.Mini_Project_A.service.UserService;
import com.nus.iss.sg.Mini_Project_A.service.WatchlistService;

import jakarta.servlet.http.HttpSession;

@RestController
public class CryptoRestController {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private UserService userService;

    @GetMapping("/api/cryptos")
    public List<CryptoData> getCryptoList(Integer page, Integer size) {
        return cryptoService.getCryptos(1, 50);
    }

    // Retrieve the user's watchlist
    @GetMapping("/watchlist")
    public ResponseEntity<List<WatchlistEntry>> getWatchlist(@RequestParam String username) {
        List<WatchlistEntry> watchlist = userService.getCryptoWatchlist(username);
        return ResponseEntity.ok(watchlist);
    }

    @PostMapping("/user/watchlist/update-note")
    public ResponseEntity<String> updateNote(
            @RequestParam String id,
            @RequestParam String note,
            HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        boolean updated = watchlistService.updateNoteForCrypto(username, id, note);

        if (updated) {
            return ResponseEntity.ok("Note updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update note");
        }
    }

    // Add a crypto to the user's watchlist
    @PostMapping("/watchlist/add")
    public ResponseEntity<String> addToWatchlist(@RequestParam String username,
            @RequestBody WatchlistEntry entry) {
        try {
            userService.addCryptoToWatchlist(username, entry);
            return ResponseEntity.ok("Crypto added to watchlist successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Remove a crypto from the user's watchlist
    @PostMapping("/watchlist/remove")
    public ResponseEntity<String> removeFromWatchlist(@RequestParam String username,
            @RequestParam String cryptoId) {
        try {
            userService.removeCryptoFromWatchlist(username, cryptoId);
            return ResponseEntity.ok("Crypto removed from watchlist successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
