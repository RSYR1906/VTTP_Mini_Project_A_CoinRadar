package com.nus.iss.sg.Mini_Project_A.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;
import com.nus.iss.sg.Mini_Project_A.service.WatchlistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/watchlist")
public class CryptoWatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    // Display the user's watchlist.
    @GetMapping("")
    public String showWatchlist(Model model, HttpSession session) {

        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        if (username == null) {
            return "redirect:/login";
        }

        List<WatchlistEntry> watchlist = watchlistService.getUserWatchlist(username);
        model.addAttribute("watchlist", watchlist);

        if (watchlist == null) {
            watchlist = new ArrayList<>();
        }
        return "watchlist"; // Render the watchlist page
    }

    // Add a cryptocurrency to the user's watchlist.
    @PostMapping("/add")
    public String addToWatchlist(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String symbol,
            @RequestParam BigDecimal currentPrice,
            @RequestParam BigDecimal marketCap,
            @RequestParam BigDecimal priceChangePercentage24h,
            @RequestParam String logoUrl,
            @RequestParam(defaultValue = "") String userNotes, // Default to empty string
            HttpSession session) {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login"; // Redirect to login if session data is missing
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

        return "redirect:/user/watchlist";
    }

    // Remove a cryptocurrency from the user's watchlist.
    @PostMapping("/remove")
    public String removeFromWatchlist(@RequestParam String id, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        List<WatchlistEntry> watchlist = watchlistService.getUserWatchlist(username);
        boolean removed = watchlist.removeIf(entry -> entry.getId().equals(id));

        if (removed) {
            watchlistService.updateUserWatchlist(username, watchlist);
        }

        return "redirect:/user/watchlist";
    }
}