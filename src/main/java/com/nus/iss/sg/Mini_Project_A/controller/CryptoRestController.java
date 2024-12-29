package com.nus.iss.sg.Mini_Project_A.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nus.iss.sg.Mini_Project_A.model.CryptoData;
import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;
import com.nus.iss.sg.Mini_Project_A.service.CryptoService;
import com.nus.iss.sg.Mini_Project_A.service.WatchlistService;

import jakarta.servlet.http.HttpSession;

@RestController
public class CryptoRestController {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private WatchlistService watchlistService;

    @GetMapping("/api/cryptos")
    public List<CryptoData> getCryptoList(Integer page, Integer size) {
        return cryptoService.getCryptos(1, 50);
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

    // Add a cryptocurrency to the user's watchlist.
    @CrossOrigin(origins = "https://coinradar.up.railway.app")
    @PostMapping("/user/watchlist/add")
    public ResponseEntity<String> addToWatchlist(
            @RequestParam String username,
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String symbol,
            @RequestParam BigDecimal currentPrice,
            @RequestParam BigDecimal marketCap,
            @RequestParam BigDecimal priceChangePercentage24h,
            @RequestParam String logoUrl,
            @RequestParam(defaultValue = "") String userNotes, // Default to empty string
            HttpSession session) {

        username = (String) session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid username");
        }
        // Create a new watchlist entry
        WatchlistEntry entry = new WatchlistEntry();
        entry.setId(id);
        entry.setName(name);
        entry.setSymbol(symbol);
        entry.setCurrentPrice(currentPrice);
        entry.setMarketCap(marketCap);
        entry.setPriceChangePercentage24h(priceChangePercentage24h);
        entry.setLogoUrl(logoUrl);
        entry.setUserNotes(userNotes == null || userNotes.trim().isEmpty() ? "" : userNotes);

        watchlistService.addToUserWatchlist(username, entry);

        return ResponseEntity.ok().body("Added to watchlist");
    }

    // Remove a cryptocurrency from the user's watchlist.
    @CrossOrigin(origins = "https://coinradar.up.railway.app")
    @PostMapping("/user/watchlist/remove")
    public ResponseEntity<String> removeFromWatchlist(@RequestParam String id, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid username");
        }

        List<WatchlistEntry> watchlist = watchlistService.getUserWatchlist(username);
        boolean removed = watchlist.removeIf(entry -> entry.getId().equals(id));

        if (removed) {
            watchlistService.updateUserWatchlist(username, watchlist);
        }

        return ResponseEntity.ok().body("Removed from watchlist");
    }
}
