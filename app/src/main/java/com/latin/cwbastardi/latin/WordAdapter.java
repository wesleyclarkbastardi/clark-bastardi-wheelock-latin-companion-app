package com.latin.cwbastardi.latin;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<Word> {

    private int colorResource;

    public WordAdapter(Activity context, ArrayList<Word> words, int color) {
        super(context, 0, words);
        colorResource = color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Word word = getItem(position);

        TextView latin = listItemView.findViewById(R.id.latin_word);
        latin.setText(word.getLatin());

        TextView defaultTranslation = listItemView.findViewById(R.id.english_word);
        defaultTranslation.setText(word.getDefaultTranslation());

        // Get a reference for the part of the list item that contains the text
        View textContainer = listItemView.findViewById(R.id.text_container);
        // Find the color that the resource ID maps to
        int color = ContextCompat.getColor(getContext(), colorResource);
        // Set the background color of the text container View
        textContainer.setBackgroundColor(color);

        return listItemView;
    }
}
