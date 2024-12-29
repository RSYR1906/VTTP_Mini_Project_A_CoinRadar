package com.nus.iss.sg.Mini_Project_A.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nus.iss.sg.Mini_Project_A.constants.Url;
import com.nus.iss.sg.Mini_Project_A.model.Article;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private RedisTemplate<String, Object> template;

    RestTemplate restTemplate = new RestTemplate();

    public List<Article> getArticles() {

        String articleData = restTemplate.getForObject(Url.newsUrl, String.class); // get entire Json Data as a String
        JsonReader jReader = Json.createReader(new StringReader(articleData)); // wraps the String for reading
        JsonObject jObject = jReader.readObject(); // convert the JSON data into a JsonObject

        JsonArray jArray = jObject.getJsonArray("Data"); // accessing the Data JsonArray

        List<Article> articleList = new ArrayList<>();

        for (JsonValue value : jArray) {
            Article a = new Article();
            JsonObject articleJson = value.asJsonObject();

            // Set basic article fields
            a.setId(articleJson.getInt("ID"));
            a.setPublishedDate(articleJson.getJsonNumber("PUBLISHED_ON").longValue());
            a.setImageUrl(articleJson.getString("IMAGE_URL"));
            a.setTitle(articleJson.getString("TITLE"));
            a.setUrl(articleJson.getString("URL"));
            a.setBody(articleJson.getString("BODY"));
            a.setTags(articleJson.getString("KEYWORDS"));

            JsonArray categoryData = articleJson.getJsonArray("CATEGORY_DATA");
            List<String> categories = new ArrayList<>();
            if (categoryData != null) {
                for (JsonValue category : categoryData) {
                    categories.add(category.asJsonObject().getString("CATEGORY"));
                }
            }
            a.setCategories(String.join(", ", categories)); // Join categories as a comma-separated string

            // Add to article list
            articleList.add(a);
        }
        return articleList;
    }

    public Article getArticleById(String articleId) {
        String redisKey = "article:" + articleId;

        // Retrieve the object from Redis
        Object articleObj = template.opsForValue().get(redisKey);

        // Return null if not found
        if (articleObj == null) {
            return null;
        }

        return (Article) articleObj;
    }

    public List<Article> getNewsForCoin(String coinSymbol) {
        String url = UriComponentsBuilder.fromUriString("https://data-api.cryptocompare.com/news/v1/article/list")
                .queryParam("lang", "EN")
                .queryParam("categories", coinSymbol)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();

        // Make the GET request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        // Check for a successful response
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            // Parse the response JSON
            JsonReader reader = Json.createReader(new StringReader(response.getBody()));
            JsonObject responseObject = reader.readObject();

            // Extract the "Data" array
            JsonArray articlesArray = responseObject.getJsonArray("Data");

            if (articlesArray == null || articlesArray.isEmpty()) {
                // No articles found
                return List.of();
            }

            // Map the JSON array to a list of Article objects
            List<Article> articles = new ArrayList<>();
            for (JsonValue value : articlesArray) {
                if (articles.size() == 3)
                    break; // Limit to 3 articles
                JsonObject articleJson = value.asJsonObject();
                Article article = new Article();
                article.setId(articleJson.getInt("ID"));
                article.setPublishedDate(articleJson.getJsonNumber("PUBLISHED_ON").longValue());
                article.setImageUrl(articleJson.getString("IMAGE_URL"));
                article.setTitle(articleJson.getString("TITLE"));
                article.setUrl(articleJson.getString("URL"));
                article.setBody(articleJson.getString("BODY"));
                article.setTags(articleJson.getString("KEYWORDS"));

                // Parse categories
                JsonArray categoryData = articleJson.getJsonArray("CATEGORY_DATA");
                if (categoryData != null) {
                    List<String> categories = new ArrayList<>();
                    for (JsonValue category : categoryData) {
                        categories.add(category.asJsonObject().getString("CATEGORY"));
                    }
                    article.setCategories(String.join(", ", categories));
                }

                articles.add(article);
            }

            return articles;
        }

        // Return an empty list if response is not OK
        logger.warn("No news found for symbol: {}", coinSymbol);
        return List.of();
    }
}
