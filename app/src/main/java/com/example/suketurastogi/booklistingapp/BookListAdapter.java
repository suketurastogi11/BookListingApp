package com.example.suketurastogi.booklistingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookListAdapter extends ArrayAdapter<BookList> {

    public BookListAdapter(Activity context, ArrayList<BookList> books){
        super(context,0,books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        BookList currentBook = getItem(position);

        TextView bookTitle = (TextView)listItemView.findViewById(R.id.book_title_list_item);
        bookTitle.setText(currentBook.getTitle());

        TextView bookAuthor = (TextView)listItemView.findViewById(R.id.book_author_list_item);
        bookAuthor.setText(currentBook.getAuthor());

        return listItemView;
    }
}
