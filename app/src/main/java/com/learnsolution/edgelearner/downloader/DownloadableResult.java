package com.learnsolution.edgelearner.downloader;

public class DownloadableResult {

    private int percent;
    private int downloadStatus;

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
