package com.learnsolution.edgelearner.downloader;

import android.net.Uri;

import com.learnsolution.edgelearner.utils.ApplicationHelper;

public class DownloadableItem {

    private String id;
    private String bookId;
    private String bookName;
    private String pages;
    private String bookPath;

    private long downloadId;
    private DownloadingStatus downloadingStatus;
    private String bookDownloadUrl;
    private String bookZipPath;
    private int bookDownloadPercent;
    private long lastEmittedDownloadPercent = -1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getBookPath() {
        return bookPath;
    }

    public DownloadingStatus getDownloadingStatus() {
        return downloadingStatus;
    }

    public void setDownloadingStatus(DownloadingStatus downloadingStatus) {
        this.downloadingStatus = downloadingStatus;
    }

    public String getBookDownloadUrl() {
        return bookDownloadUrl;
    }

    public void setBookDownloadUrl(String bookDownloadUrl) {
        this.bookDownloadUrl = bookDownloadUrl;
        this.bookZipPath = "file://"+ApplicationHelper.zipFolder + "/"+ Uri.parse(bookDownloadUrl).getLastPathSegment();
        this.bookPath = ApplicationHelper.booksFolder.getAbsolutePath()+"/"+bookId;
    }

    public String getBookZipPath() {
        return bookZipPath;
    }

    public void setBookZipPath(String bookZipPath) {
        this.bookZipPath = bookZipPath;
    }

    public int getBookDownloadPercent() {
        return bookDownloadPercent;
    }

    public void setBookDownloadPercent(int bookDownloadPercent) {
        this.bookDownloadPercent = bookDownloadPercent;
    }

    public long getLastEmittedDownloadPercent() {
        return lastEmittedDownloadPercent;
    }

    public void setLastEmittedDownloadPercent(long lastEmittedDownloadPercent) {
        this.lastEmittedDownloadPercent = lastEmittedDownloadPercent;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }
}
