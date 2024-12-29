package com.nus.iss.sg.Mini_Project_A.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nus.iss.sg.Mini_Project_A.model.Article;
import com.nus.iss.sg.Mini_Project_A.service.NewsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/news")
public class CryptoNewsController {

    private static final Logger logger = LoggerFactory.getLogger(CryptoNewsController.class);

    @Autowired
    private NewsService newsService;

    @GetMapping("")
    public String showLatestNews(Model model, HttpSession session) {
        // Check for a valid session
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        if (username == null) {
            logger.warn("Unauthorized access attempt to news page. Redirecting to login.");
            return "redirect:/login";
        }

        // Fetch latest articles
        List<Article> articleList = newsService.getArticles();
        model.addAttribute("articles", articleList);

        if (articleList.isEmpty()) {
            logger.warn("No news articles found to display.");
        }

        logger.info("Displaying latest news for user: {}", username);
        return "news";
    }
}