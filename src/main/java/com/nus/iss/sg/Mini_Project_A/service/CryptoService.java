package com.nus.iss.sg.Mini_Project_A.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nus.iss.sg.Mini_Project_A.constants.Constants;
import com.nus.iss.sg.Mini_Project_A.constants.Url;
import com.nus.iss.sg.Mini_Project_A.model.CryptoData;

@Service
public class CryptoService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    private static final long CACHE_EXPIRY_SECONDS = 60;

    private static final String CRYPTO_DETAILS_URL = Url.BASE_URL + "coins/{id}";
    private static final String MARKET_DATA_URL = Url.BASE_URL + "coins/{id}/tickers";

    @Autowired
    private RedisTemplate<String, List<CryptoData>> cryptoDataRedisTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(value = "cryptoData", key = "'cryptoList'", unless = "#result.isEmpty()")
    public List<CryptoData> fetchAndCacheCryptos() {
        logger.info("Fetching cryptocurrency data from CoinGecko API...");
        try {
            CryptoData[] fetchedData = restTemplate.getForObject(Url.cryptoListUrl, CryptoData[].class);
            if (fetchedData != null) {
                List<CryptoData> cryptoList = Arrays.asList(fetchedData);
                cacheCryptoList(cryptoList);
                logger.info("Successfully fetched and cached {} cryptocurrencies.", cryptoList.size());
                return cryptoList;
            }
        } catch (Exception e) {
            logger.error("Error fetching data from CoinGecko API: {}", e.getMessage(), e);
        }
        logger.warn("Returning an empty list due to failed API fetch.");
        return List.of();
    }

    public List<CryptoData> getCryptos(int page, int size) {
        logger.info("Fetching paginated cryptocurrencies for page {} with size {}.", page, size);
        List<CryptoData> cryptoList = getCachedCryptoList();
        if (cryptoList.isEmpty()) {
            logger.warn("No cryptocurrencies found in cache. Fetching data...");
            cryptoList = fetchAndCacheCryptos();
        }
        return paginateCryptoList(cryptoList, page, size);
    }

    public List<CryptoData> searchCryptos(String query) {
        logger.info("Searching cryptocurrencies for query: {}", query);
        List<CryptoData> cryptoList = getCachedCryptoList();
        if (cryptoList.isEmpty()) {
            logger.warn("No cryptocurrencies found in cache. Fetching data...");
            cryptoList = fetchAndCacheCryptos();
        }
        return filterCryptoList(cryptoList, query);
    }

    @Cacheable(value = "cryptoData", key = "#id", unless = "#result == null")
    public Map<String, Object> fetchCryptoDetails(String id) {
        logger.info("Fetching details for crypto ID: {}", id);
        return restTemplate.getForObject(CRYPTO_DETAILS_URL, Map.class, id);
    }

    @Cacheable(value = "cryptoData", key = "'marketData:' + #id", unless = "#result == null")
    public Map<String, Object> fetchMarketData(String id) {
        logger.info("Fetching market data for crypto ID: {}", id);
        return restTemplate.getForObject(MARKET_DATA_URL, Map.class, id);
    }

    @Scheduled(fixedRate = 300000)
    @CacheEvict(value = "cryptoData", allEntries = true)
    public void refreshCryptoList() {
        logger.info("Scheduled refresh: Fetching and caching cryptocurrencies...");
        fetchAndCacheCryptos();
    }

    //Helper Methods
    private void cacheCryptoList(List<CryptoData> cryptoList) {
        cryptoDataRedisTemplate.opsForValue().set(Constants.CRYPTO_LIST_KEY, cryptoList, CACHE_EXPIRY_SECONDS, TimeUnit.SECONDS);
    }

    private List<CryptoData> getCachedCryptoList() {
        List<CryptoData> cachedList = cryptoDataRedisTemplate.opsForValue().get(Constants.CRYPTO_LIST_KEY);
        return cachedList != null ? cachedList : List.of();
    }

    private List<CryptoData> paginateCryptoList(List<CryptoData> cryptoList, int page, int size) {
        int start = (page - 1) * size;
        int end = Math.min(start + size, cryptoList.size());
        if (start >= cryptoList.size()) {
            logger.warn("Pagination exceeds data size. Returning an empty list.");
            return List.of();
        }
        return cryptoList.subList(start, end);
    }

    private List<CryptoData> filterCryptoList(List<CryptoData> cryptoList, String query) {
        String lowerCaseQuery = query.toLowerCase();
        return cryptoList.stream()
                .filter(crypto -> crypto.getName().toLowerCase().contains(lowerCaseQuery)
                        || crypto.getSymbol().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
    }
}
