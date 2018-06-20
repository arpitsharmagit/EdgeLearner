package com.ninjas.edgelearner.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class BookDetails implements Parcelable {
    private String bookId;
    private String downloadUrl;
    private String bookName;

    public BookDetails(String bookId,String bookName,String downloadUrl){
        this.bookId = bookId;
        this.bookName = bookName;
        this.downloadUrl = downloadUrl;
    }

    public String getBookId() {
        return bookId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public BookDetails(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.bookId = data[0];
        this.bookName = data[1];
        this.downloadUrl = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.bookId,
                this.bookName,
                this.downloadUrl});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BookDetails createFromParcel(Parcel in) {
            return new BookDetails(in);
        }

        public BookDetails[] newArray(int size) {
            return new BookDetails[size];
        }
    };
}
