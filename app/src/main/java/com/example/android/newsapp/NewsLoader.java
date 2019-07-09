package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**
     * Tag for Log messages
     */
    private final static String LOG_TAG = NewsLoader.class.getSimpleName();

    /**
     * News Query Url
     */
    private String newsUrl;

    public NewsLoader(Context context, String newsUrl) {
        super(context);
        this.newsUrl = newsUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        // Don't perform the request if there is no URLs, or the first URL is null
        if (newsUrl == null) {
            return null;
        }

        return NewsUtils.fetchNewsData(newsUrl);
    }
}
