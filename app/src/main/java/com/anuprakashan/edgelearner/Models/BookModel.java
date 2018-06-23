package com.anuprakashan.edgelearner.Models;

public class BookModel {
    String bookId;
    String bookName;
    String bookDetails;

    public BookModel(String bookId,String bookName, String bookDetails) {
        this.bookId=bookId;
        this.bookName=bookName;
        this.bookDetails =bookDetails;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookDetails() {
        return bookDetails;
    }
}
