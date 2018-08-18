package com.learnsolution.edgelearner.downloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.learnsolution.edgelearner.R;

public class ItemDetailsViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private TextView bookName,bookPages;
    private BookDownloaderIconView imageDownloadIcon;
    private DownloadableItem downloadableItem;
    private Context context;
    private ItemDownloadCallback callback;

    public ItemDetailsViewHolder(View itemView, Context context, ItemDownloadCallback callback) {
        super(itemView);

        if (itemView == null) {
            return;
        }

        bookName = (TextView) itemView.findViewById(R.id.book_name);
        bookPages = (TextView) itemView.findViewById(R.id.book_pages);
        imageDownloadIcon = (BookDownloaderIconView) itemView.findViewById(R.id.icon_image_download);
        imageDownloadIcon.init();
        imageDownloadIcon.setOnClickListener(this);
        this.context = context;
        this.callback = callback;
    }

    public void updateImageDetails(DownloadableItem downloadableItem) {
        this.downloadableItem = downloadableItem;
        bookName.setText(downloadableItem.getBookName());
        bookPages.setText(downloadableItem.getPages());
        imageDownloadIcon.setItemId(downloadableItem.getId());
        imageDownloadIcon.updateDownloadingStatus(downloadableItem.getDownloadingStatus());

        if (downloadableItem.getDownloadingStatus() == DownloadingStatus.DOWNLOADED) {
            setImageToCompletedState(downloadableItem.getId());
        } else if (downloadableItem.getDownloadingStatus() == DownloadingStatus.IN_PROGRESS &&
                downloadableItem.getBookDownloadPercent()
                        == Constants.DOWNLOAD_COMPLETE_PERCENT) {
            setImageToCompletedState(downloadableItem.getId());
            callback.onDownloadComplete(downloadableItem);
        } else if (downloadableItem.getDownloadingStatus() == DownloadingStatus.IN_PROGRESS) {
            bookPages.setText("Downloading... "+downloadableItem.getBookDownloadPercent()+"%");
            setImageInProgressState(downloadableItem.getBookDownloadPercent(), downloadableItem.getId());
        }
    }

    @Override
    public void onClick(View v) {
        DownloadingStatus downloadingStatus = imageDownloadIcon.getDownloadingStatus();
        //Only when the icon is in not downloaded state, then do the following.
        if (downloadingStatus == DownloadingStatus.NOT_DOWNLOADED) {
            setImageToWaitingState(downloadableItem.getId());
            callback.onDownloadEnqueued(downloadableItem);
        }
    }

    public void setImageToWaitingState(String itemId) {
        if (!downloadableItem.getId().equalsIgnoreCase(itemId)) {
            return;
        }
        imageDownloadIcon.updateDownloadingStatus(DownloadingStatus.WAITING);
    }

    public void setImageToCompletedState(String itemId) {
        if (!downloadableItem.getId().equalsIgnoreCase(itemId)) {
            return;
        }
        imageDownloadIcon.setOnClickListener(null);
        imageDownloadIcon.updateDownloadingStatus(DownloadingStatus.DOWNLOADED);
    }

    public void setImageInProgressState(int progress, String itemId) {
        if (!downloadableItem.getId().equalsIgnoreCase(itemId)) {
            return;
        }
        imageDownloadIcon.updateProgress(context, progress);
        imageDownloadIcon.updateDownloadingStatus(DownloadingStatus.IN_PROGRESS);
    }
}
