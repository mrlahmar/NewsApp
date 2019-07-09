package com.example.android.newsapp;

import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsUtils {

    /**
     * Tag for Log messages
     */
    private final static String LOG_TAG = NewsUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link NewsUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NewsUtils (and an object instance of NewsUtils is not needed).
     */
    private NewsUtils() {
    }

    // Query the "The Guardian" data set and return a list of News objects
    public static List<News> fetchNewsData (String newsUrl) {

        try {
            Thread.sleep(2000);
        }catch (InterruptedException e) {
            Log.e(LOG_TAG,"Interrupted Exception");
        }

        // Create URL object
        URL url = createUrl(newsUrl);

        // Perform an HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem making the HTTP Request");
        }

        // Extract relevant fields from the JSON response and create a list of News
        // Return the list of News
        return extractNewsDataFromJSON(jsonResponse);
    }

    private static List<News> extractNewsDataFromJSON(String jsonResponse) {

        // if the string is empty or null, return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding News objects to
        List<News> newsList = new ArrayList<>();

        // Trying to parse the jsonResponse if there is a problem when parsing
        // the jsonResponse the system will throw a JSONException
        try{

            // Create a JSONObject the JSON response string
            JSONObject rootJSONobject = new JSONObject(jsonResponse);

            // Extract JSONObject from rootJSONobject by using key: "response"
            JSONObject responseJSONobject = rootJSONobject.getJSONObject("response");

            // Extract JSONArray from responseJSONobject by using key: "results"
            // This JSONArray contains a list of News
            JSONArray resultJSONarray = responseJSONobject.getJSONArray("results");

            // Extract {@link News} objects from this resultJSONarray
            for(int i=0; i<resultJSONarray.length(); i++) {

                // Extract a {@link News} object at a specific position
                JSONObject currentNewsJObject = resultJSONarray.getJSONObject(i);

                // Extract the String associated with the key: "sectionName"
                String category = currentNewsJObject.getString("sectionName");

                // Extract the String associated with the key: "webPublicationDate"
                String date = currentNewsJObject.getString("webPublicationDate")
                        .substring(0,10);

                // Extract the String associated with the key: "webTitle"
                String title = currentNewsJObject.getString("webTitle");

                // Extract the String associated with the key: "webUrl"
                String newsUrl = currentNewsJObject.getString("webUrl");

                // Extract the JSONObject associated with the key: "fields"
                // This JSONObject contains 2 strings which are "headline" and "thumbnail"
                // Now we're going to extract the Thumbnail (image) Url
                JSONObject fields = currentNewsJObject.getJSONObject("fields");
                String imgUrl = fields.getString("thumbnail");

                // Extract the String for the key called "webTitle"
                // Which represents the Writer of the post
                String writer;

                // Extract the JSONArray associated with the key: "tags"
                // This JSONArray is an array of JSONObjects and each object contains
                // details about the writer, but we're going to use these objects
                // to extract only the name of the Writer
                if(currentNewsJObject.has("tags")) {

                    JSONArray tagsArray = currentNewsJObject.getJSONArray("tags");

                    if (!currentNewsJObject.isNull("tags") && tagsArray.length() > 0) {
                        JSONObject tagsItem = (JSONObject) tagsArray.get(0);
                        writer = tagsItem.getString("webTitle");
                    } else {
                        // If the condition is true, then it will be no writer name available
                        // so we are going to display "Unknown Writer"
                        writer = "^ Unknown Writer ^";
                    }
                } else {
                    // If the condition is true, then it means that no object available
                    // so we are going to display "Missing Info of Writer"
                    writer = "^ Missing Info of Writer ^";
                    Log.i(LOG_TAG, writer);
                }

                // Add a new {@link News} object to the newsList
                // with the title,category,writer,date,imgUrl,newsUrl
                // that we've extracted from the jsonResponse
                newsList.add(new News(title,category,writer,date,imgUrl,newsUrl));
            }

        }catch (JSONException e) {
            Log.e(LOG_TAG,"Error when parsing the JSON response" , e);
        }

        // Return the list of News
        return newsList;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000 /*Milliseconds*/);
            httpURLConnection.setConnectTimeout(15000 /*Milliseconds*/);
            httpURLConnection.connect();

            // if the request was successful (response code 200)
            // then read the input stream and parse the response
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code " + httpURLConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (inputStream != null) {
                // closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method siqnature specifies that an IOexceo
                // could be thrown
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    // Convert the InputStream into a String which contains the
    // whole JSON response from the server
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(inputStreamReader);

            String line = br.readLine();
            while (line != null) {
                output.append(line);
                line = br.readLine();
            }
            br.close();
        }
        return output.toString();
    }

    // Return an URL object from the given Url String
    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"Malformed URL Exception");
        }

        return url;
    }
}
