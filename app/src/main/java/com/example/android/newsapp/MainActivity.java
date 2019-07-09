package com.example.android.newsapp;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    /**
     * Tag for Log messages
     */
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * A Custom Adapter for the {@link News} object
     */
    private NewsAdapter newsAdapter;

    /**
     * A String that will holds the Query Url
     */
    private String queryUrl = "";

    /** Constant value for the Book Loader ID , we can choose an integer
     *  This is really comes to play if you're using multiple loaders
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Text to show when the list is empty
     */
    TextView listEmptyView;

    /**
     * Search Bar
     */
    SearchView searchView;

    /**
     * Search Button
     */
    Button searchButton;

    /**
     * Circle progress bar
     */
    View circleProgressBar;

    /**
     * A boolean variable to check if the connection is established or not
     */
    boolean connectionEstablished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to {@link ConnectivityManager} for checking internet connection
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        checkConnectivity(connectivityManager);

        // Find a reference to {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        // Create a new {@link NewsAdapter} of News
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(newsAdapter);

        // Setting the Empty View
        listEmptyView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(listEmptyView);

        // Initializing the circle progress bar
        circleProgressBar = findViewById(R.id.loading_spinner);

        if (connectionEstablished) {
            // Get a reference to the LoaderManager.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader.
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {

            circleProgressBar.setVisibility(View.GONE);
            // Set empty state text to display "No internet connection."
            listEmptyView.setText(R.string.no_internet_connection);
        }


        // Find a reference to {@link Button} in the layout
        searchButton = findViewById(R.id.search_button);

        // Find a reference to {@link SearchView} in the layout
        searchView = findViewById(R.id.search_view_field);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check connection status
                checkConnectivity(connectivityManager);

                if (connectionEstablished) {
                    // Update the URL and restart loader to displaying new result of searching
                    updateTheUrl(searchView.getQuery().toString());
                    reLaunchLoader();
                    Log.i(LOG_TAG, "Search value: " + searchView.getQuery().toString());
                } else {
                    // Clear the adapter of previous newses data
                    newsAdapter.clear();
                    // Set listEmptyView visible
                    listEmptyView.setVisibility(View.VISIBLE);
                    // Display "No internet connection" as a message
                    listEmptyView.setText(R.string.no_internet_connection);
                }
            }
        });

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the Current News that was clicked on
                News currentNews = newsAdapter.getItem(position);

                // Convert the String URL to an URI object to it pass into the Intent Constructor
                Uri newsUri = Uri.parse(currentNews.getNewsUrl());

                // Create a new Intent to View the News URI
                Intent newsIntent = new Intent(Intent.ACTION_VIEW,newsUri);

                // Send the Intent to start a new Activity
                startActivity(newsIntent);
            }
        });
    }

    private void reLaunchLoader() {
        listEmptyView.setVisibility(View.GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
    }

    private void checkConnectivity(ConnectivityManager connectivityManager) {
        // Getting Internet Connection status
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        connectionEstablished = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void updateTheUrl(String query) {
        if (query.contains(" ")) {
            query.replace(" ","+");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("https://content.guardianapis.com/search?q=")
                .append(query)
                .append("&order-date=published&show-section=true&show-fields=headline,thumbnail&show-tags=contributor&page=10&page-size=20&api-key=bddc0a05-1587-4802-8c19-0e3b454d4556");
        this.queryUrl = sb.toString();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        // We are testing if the Search Field does not contains nothing
        // we are going to show the recent news
        // we are going to tell the user about that in a Toast message

        SearchView searchView = findViewById(R.id.search_view_field);
        if (searchView.getQuery().length() > 0) {
            updateTheUrl(searchView.getQuery().toString());
        } else {
            Toast.makeText(this,"Recent News Displayed, Search for something.",Toast.LENGTH_LONG).show();
            this.queryUrl = "http://content.guardianapis.com/search?q=android&order-by=newest&order-date=published&show-section=true&show-fields=headline,thumbnail&show-references=author&show-tags=contributor&page=1&page-size=20&api-key=bddc0a05-1587-4802-8c19-0e3b454d4556";
        }
        return new NewsLoader(this,queryUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Progress bar mapping
        View circleProgressBar = findViewById(R.id.loading_spinner);
        circleProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        listEmptyView.setText(R.string.no_news);

        // Clear the adapter of previous earthquake data
        newsAdapter.clear();

        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if(news != null && !news.isEmpty()){
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        newsAdapter.clear();
    }
}
