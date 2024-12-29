package com.nus.iss.sg.Mini_Project_A.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WatchlistEntry {

    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String logoUrl;

    @JsonProperty("current_price")
    private BigDecimal currentPrice;

    @JsonProperty("market_cap")
    private BigDecimal marketCap;

    @JsonProperty("price_change_percentage_24h")
    private BigDecimal priceChangePercentage24h;

    private String userNotes; 

    private boolean isInWatchlist;

    public WatchlistEntry() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public BigDecimal getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }

    public void setPriceChangePercentage24h(BigDecimal priceChangePercentage24h) {
        this.priceChangePercentage24h = priceChangePercentage24h;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isInWatchlist() {
        return isInWatchlist;
    }

    public void setInWatchlist(boolean inWatchlist) {
        isInWatchlist = inWatchlist;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

    @Override
    public String toString() {
        return id + "," + symbol + "," + name + "," + logoUrl
                + "," + currentPrice + "," + marketCap + ","
                + priceChangePercentage24h + "," + userNotes + "," + isInWatchlist;
    }

}