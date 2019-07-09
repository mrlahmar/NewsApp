package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Tag for Log Messages
     */
    private final static String LOG_TAG = NewsAdapter.class.getSimpleName();

    public NewsAdapter(@NonNull Context context,@NonNull List<News> objects) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent,false);
        }

        // Find the Current News
        News currentNews = getItem(position);
        Log.i(LOG_TAG,"Position of the Item : " + position);

        // Setting the Title of the News
        TextView currentNewsTitle = listItemView.findViewById(R.id.title);
        currentNewsTitle.setText(currentNews.getTitle());

        // Setting the Category of the News
        TextView currentNewsCategory = listItemView.findViewById(R.id.category);
        currentNewsCategory.setText(currentNews.getCategory());

        // Setting the Writer of the News
        TextView currentNewsWriter = listItemView.findViewById(R.id.writer);
        String writer = "By " + currentNews.getWriter();
        currentNewsWriter.setText(writer);

        // Setting the Date of the News
        TextView currentNewsDate = listItemView.findViewById(R.id.date);
        String date = "On " + currentNews.getDate();
        currentNewsDate.setText(date);

        // Setting the Image of the News
        ImageView currentNewsImage = listItemView.findViewById(R.id.image);
        Picasso.get().load(currentNews.getImageUrl()).into(currentNewsImage);

        Log.i(LOG_TAG, "ListItem has been returned");

        return listItemView;
    }
}
