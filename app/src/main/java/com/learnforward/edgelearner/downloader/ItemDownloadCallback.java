package com.learnforward.edgelearner.downloader;

public interface ItemDownloadCallback {

    void onDownloadEnqueued(DownloadableItem downloadableItem);

    void onDownloadStarted(DownloadableItem downloadableItem);

    void onDownloadComplete(DownloadableItem downloadableItem);
}