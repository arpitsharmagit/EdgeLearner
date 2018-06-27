package com.anuprakashan.edgelearner.Models;

public class BookView {
    private Book Book;

    public Book getBook ()
    {
        return Book;
    }

    public void setBook (Book Book)
    {
        this.Book = Book;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Book = "+Book+"]";
    }
}
