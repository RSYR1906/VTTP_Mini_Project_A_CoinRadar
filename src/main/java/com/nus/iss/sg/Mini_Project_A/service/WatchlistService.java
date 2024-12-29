package com.nus.iss.sg.Mini_Project_A.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nus.iss.sg.Mini_Project_A.constants.Constants;
import com.nus.iss.sg.Mini_Project_A.constants.Url;
import com.nus.iss.sg.Mini_Project_A.model.CryptoData;
import com.nus.iss.sg.Mini_Project_A.model.WatchlistEntry;
import com.nus.iss.sg.Mini_Project_A.repo.UserRepository;

@Service
public class WatchlistService {

    private static final Logger logger = LoggerFactory.getLogger(WatchlistService.class);
    private static final long CACHE_EXPIRATION = 60; // Cache expiry time in seconds

    @Autowired
    private RedisTemplate<String, Object> watchListRedisTemplate;

    @Autowired
    private UserRepository userRepository;

    private RestTemplate restTemplate;

    public void fetchAndCacheIndividualCryptos() {
        try {
            CryptoData[] fetchedData = fetchCryptoDataFromApi();
            if (fetchedData == null || fetchedData.length == 0) {
                logger.warn("No cryptocurrency data fetched from the API.");
                return;
            }

            cacheCryptoData(fetchedData);
        } catch (Exception e) {
            logger.error("Error fetching or caching cryptocurrency data: {}", e.getMessage(), e);
        }
    }

    public CryptoData getCryptoFromRedis(String id) {
        logger.info("Fetching cryptocurrency with ID: {} from Redis", id);

        String key = Constants.CRYPTO_PREFIX + id;
        CryptoData crypto = (CryptoData) watchListRedisTemplate.opsForValue().get(key);

        if (crypto == null) {
            logger.info("Cache miss for ID: {}. Refetching data...", id);
            fetchAndCacheIndividualCryptos();
            crypto = (CryptoData) watchListRedisTemplate.opsForValue().get(key);
        }

        return crypto;
    }

    public List<CryptoData> getAllCryptosFromRedis() {
        logger.info("Fetching all cryptocurrencies from Redis");

        List<String> allIds = getCachedCryptoIds();

        if (allIds == null || allIds.isEmpty()) {
            logger.info("Cache miss for all cryptos. Refetching data...");
            fetchAndCacheIndividualCryptos();
            allIds = getCachedCryptoIds();
        }

        return allIds == null ? List.of() : mapCryptoIdsToData(allIds);
    }

    public List<WatchlistEntry> getUserWatchlist(String username) {
        logger.info("Fetching watchlist for user: {}", username);
        return userRepository.getWatchlist(username);
    }

    public void updateUserWatchlist(String username, List<WatchlistEntry> watchlist) {
        logger.info("Updating watchlist for user: {}", username);
        userRepository.saveWatchlist(username, watchlist);
    }

    public void addToUserWatchlist(String username, WatchlistEntry entry) {
        logger.info("Adding entry to watchlist for user: {}", username);

        List<WatchlistEntry> currentWatchlist = getUserWatchlist(username);
        if (currentWatchlist == null) {
            currentWatchlist = new ArrayList<>();
        }

        currentWatchlist.add(entry);
        updateUserWatchlist(username, currentWatchlist);
    }

    public boolean updateNoteForCrypto(String username, String cryptoId, String note) {
        List<WatchlistEntry> watchlist = getUserWatchlist(username);

        for (WatchlistEntry entry : watchlist) {
            if (entry.getId().equals(cryptoId)) {
                entry.setUserNotes(note);
                updateUserWatchlist(username, watchlist);
                return true;
            }
        }
        return false;
    }

    private CryptoData[] fetchCryptoDataFromApi() {
        return restTemplate.getForObject(Url.cryptoListUrl, CryptoData[].class);
    }

    private void cacheCryptoData(CryptoData[] fetchedData) {
        List<String> allIds = new ArrayList<>();

        for (CryptoData crypto : fetchedData) {
            String key = Constants.CRYPTO_PREFIX + crypto.getId();
            watchListRedisTemplate.opsForValue().set(key, crypto, CACHE_EXPIRATION, TimeUnit.SECONDS);
            allIds.add(crypto.getId());
        }

        watchListRedisTemplate.opsForValue().set(Constants.ALL_CRYPTOS_KEY, allIds, CACHE_EXPIRATION, TimeUnit.SECONDS);
        logger.info("Successfully cached {} cryptocurrencies.", fetchedData.length);
    }

    private List<String> getCachedCryptoIds() {
        return (List<String>) watchListRedisTemplate.opsForValue().get(Constants.ALL_CRYPTOS_KEY);
    }

    private List<CryptoData> mapCryptoIdsToData(List<String> allIds) {
        return allIds.stream()
                .map(id -> (CryptoData) watchListRedisTemplate.opsForValue().get(Constants.CRYPTO_PREFIX + id))
                .filter(Objects::nonNull)
                .toList();
    }
}
