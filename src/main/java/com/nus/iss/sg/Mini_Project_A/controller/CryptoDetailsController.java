package com.nus.iss.sg.Mini_Project_A.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nus.iss.sg.Mini_Project_A.model.Article;
import com.nus.iss.sg.Mini_Project_A.service.CryptoService;
import com.nus.iss.sg.Mini_Project_A.service.NewsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CryptoDetailsController {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private NewsService newsService;

    @GetMapping("/user/crypto/details")
    public String getCoinDetails(
            @RequestParam String id,
            @RequestParam(required = false, defaultValue = "BTC") String symbol,
            Model model,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        // Convert the symbol to uppercase for consistency
        symbol = symbol.toUpperCase();
        model.addAttribute("symbol", symbol);

        // Fetch general details of the coin
        var coinDetails = cryptoService.fetchCryptoDetails(id);
        model.addAttribute("details", coinDetails);

        // Fetch market tickers of the coin
        var markets = cryptoService.fetchMarketData(id);
        model.addAttribute("markets", markets);

        // Fetch news articles related to the coin
        List<Article> articles = newsService.getNewsForCoin(symbol);
        model.addAttribute("articles", articles);

        return "coin-details";
    }
}