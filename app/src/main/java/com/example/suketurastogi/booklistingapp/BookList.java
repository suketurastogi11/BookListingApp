package com.example.suketurastogi.booklistingapp;

public class BookList {

    private String mTitle;
    private String mAuthor;

    public BookList(String title,String author){

        mTitle = title;
        mAuthor = author;

    }
    public String getTitle(){
        return mTitle;
    }

    public String getAuthor(){
        return mAuthor;
    }
}
