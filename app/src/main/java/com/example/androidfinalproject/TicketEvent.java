package com.example.androidfinalproject;

public class TicketEvent {
    private long id;
    private String eventId;
    private String name;
    private String start_date;
    private String minPrice;
    private String maxPrice;
    private String currency;
    private String url;
    private String imageUrl;

    public TicketEvent(long id, String eventId, String name, String start_date, String currency, String minPrice, String maxPrice,  String url, String imageUrl) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.start_date = start_date;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.currency = currency;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
