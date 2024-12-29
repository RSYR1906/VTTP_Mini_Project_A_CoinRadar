package com.nus.iss.sg.Mini_Project_A.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nus.iss.sg.Mini_Project_A.model.CryptoData;
import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;
import com.nus.iss.sg.Mini_Project_A.service.CryptoService;
import com.nus.iss.sg.Mini_Project_A.service.UserService;
import com.nus.iss.sg.Mini_Project_A.service.WatchlistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserPageController {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private WatchlistService watchlistService;

    // Display an error page for invalid routes or exceptions.
    @GetMapping("/error")
    public String showErrorPage() {
        return "error";
    }

    // Render the user dashboard with paginated cryptocurrency data.
    @GetMapping("")
    public String showUserPage(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "60") Integer size,
            Model model,
            HttpSession session) {

        username = (username != null) ? username : (String) session.getAttribute("username");
        model.addAttribute("username", username);

        if (username == null) {
            return "redirect:/login";
        }

        List<CryptoData> cryptos = cryptoService.getCryptos(page, size);
        model.addAttribute("cryptos", cryptos);

        List<WatchlistEntry> watchlist = watchlistService.getUserWatchlist(username);
        cryptos.forEach(crypto -> crypto.setInWatchlist(
                watchlist.stream().anyMatch(entry -> entry.getId().equals(crypto.getId()))));

        int totalCryptos = 300;
        int totalPages = (int) Math.ceil((double) totalCryptos / size);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

        return "userpage";
    }

    // Search for cryptocurrencies based on a query string.
    @GetMapping("/search")
    public String searchCryptos(
            @RequestParam String query,
            Model model,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        List<CryptoData> searchResults = cryptoService.searchCryptos(query);
        model.addAttribute("query", query);
        model.addAttribute("searchResults", searchResults);

        return "search-results";
    }

    // Log out the user by invalidating the session.
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "homepage";
    }
}