/*
    Name: BHARGAV KUMAR AATHAVA
 */

package com.example.ifingetproject;
public class NewsArticle {
    private String title;
    private String publishedDate;
    private String url;
    private String imageUrl;
    private String sentiment;
    private String sentimentReasoning;

    public NewsArticle(String title, String publishedDate, String url, String imageUrl, String sentiment, String sentimentReasoning) {
        this.title = title;
        this.publishedDate = publishedDate;
        this.url = url;
        this.imageUrl = imageUrl;
        this.sentiment = sentiment;
        this.sentimentReasoning = sentimentReasoning;
    }

    // Add getters for all fields
    public String getTitle() { return title; }
    public String getPublishedDate() { return publishedDate; }
    public String getUrl() { return url; }
    public String getImageUrl() { return imageUrl; }
    public String getSentiment() { return sentiment; }
    public String getSentimentReasoning() { return sentimentReasoning; }

    // Add a method to get formatted date
    public String getFormattedDate() {
        // Assuming the date is in the format "2024-11-24T17:30:00Z"
        return publishedDate.substring(0, 10);
    }
}