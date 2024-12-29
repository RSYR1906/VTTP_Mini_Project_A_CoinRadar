package com.nus.iss.sg.Mini_Project_A.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nus.iss.sg.Mini_Project_A.model.CryptoData;
import com.nus.iss.sg.Mini_Project_A.model.User;
import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;
import com.nus.iss.sg.Mini_Project_A.service.CryptoService;
import com.nus.iss.sg.Mini_Project_A.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class HomePageController {

    private static final Logger logger = LoggerFactory.getLogger(HomePageController.class);

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private UserService userService;

    // Display the login page.

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    // Display the signup page.
    @GetMapping("/signup")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // Display the homepage with paginated cryptocurrency data.
    @GetMapping("/")
    public String showHomePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            Model model) {

        List<CryptoData> cryptos = cryptoService.getCryptos(page, size);
        model.addAttribute("cryptos", cryptos);

        // Assume 100 total cryptos for this example
        int totalCryptos = 100;
        int totalPages = (int) Math.ceil((double) totalCryptos / size);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

        return "homepage";
    }

    // Handle user login.
    @PostMapping("/login")
    public String handleLogin(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            Model model,
            HttpSession session) {

        if (result.hasErrors()) {
            return "login";
        }

        boolean isAuthenticated = userService.authenticateUser(user.getUsername(), user.getPassword());

        if (isAuthenticated) {
            // Add username to session
            session.setAttribute("username", user.getUsername());
            logger.info("Username '{}' added to session.", user.getUsername());

            List<WatchlistEntry> watchlist = userService.getCryptoWatchlist(user.getUsername());
            model.addAttribute("watchlist", watchlist);

            return "redirect:/user?username=" + user.getUsername() + "&page=" + page + "&size=" + size;
        } else {
            logger.warn("Failed login attempt for username: {}", user.getUsername());
            model.addAttribute("showWrongPassMessage", true);
            return "login";
        }
    }

    // Handle user signup.
    @PostMapping("/signup")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "signup";
        }

        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("duplicateUsernameError", "Username is already taken.");
            return "signup";
        }

        userService.registerUser(user.getUsername(), user.getPassword());
        return "redirect:/login";
    }
}