package com.nus.iss.sg.Mini_Project_A.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
}