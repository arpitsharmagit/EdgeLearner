package com.learnsolution.edgelearner.Models;

public class BookModel {
    String bookId;
    String bookName;
    String bookPages;
    String bookPath;

    public BookModel(String bookId,String bookName, String bookPath,String bookPages) {
        this.bookId=bookId;
        this.bookName=bookName;
        this.bookPath =bookPath;
        this.bookPages=bookPages;
    }

    public String getBookName() {
        return bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookPath() {
        return bookPath;
    }

    public String getBookPages() {
        return bookPages;
    }
}
