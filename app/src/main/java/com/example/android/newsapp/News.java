package com.example.android.newsapp;

public class News {

    /**
     * Title of the News
     */
    private String title;

    /**
     * Category of the News
     */
    private String category;

    /**
     * Writer of the News
     */
    private String writer;

    /**
     * Publishing Date of the News
     */
    private String date;

    /**
     * Image URL of the News
     */
    private String imageUrl;

    /**
     * News URL
     */
    private String newsUrl;

    /**
     * @param title, The Title of the News
     * @param category, The Category of the News
     * @param writer, The person who have written the News
     * @param date, The Publishing date of the News
     * @param imageUrl, The URL of the News Image
     * @param newsUrl, The News Link
     */
    public News(String title, String category, String writer, String date, String imageUrl, String newsUrl) {
        this.title = title;
        this.category = category;
        this.writer = writer;
        this.date = date;
        this.imageUrl = imageUrl;
        this.newsUrl = newsUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getWriter() {
        return writer;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNewsUrl() {
        return newsUrl;
    }
}
