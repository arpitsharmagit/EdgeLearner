package com.anuprakashan.edgelearner.utils;

import com.anuprakashan.edgelearner.Models.BookDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BookService {
    @GET("books/{id}")
    Call<BookDetails> getBookDetails(@Path("id") String bookId);
}