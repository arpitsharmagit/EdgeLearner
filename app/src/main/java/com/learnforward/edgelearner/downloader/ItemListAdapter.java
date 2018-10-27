package com.learnforward.edgelearner.downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.learnforward.edgelearner.Models.Book.Book;
import com.learnforward.edgelearner.R;
import com.learnforward.edgelearner.utils.ApplicationHelper;
import com.learnforward.edgelearner.utils.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class ItemListAdapter extends RecyclerView.Adapter implements
        ItemDownloadCallback, ItemPercentCallback {

    private final ArrayList<DownloadableItem> itemsList;

    private int currentDownloadsCount = 0;
    private final DownloadManager downloadManager;
    private static final String TAG = ItemListAdapter.class.getSimpleName();
    private final ItemDownloadPercentObserver mItemDownloadPercentObserver;
    private final DownloadRequestsSubscriber mDownloadRequestsSubscriber;
    private final WeakReference<Context> contextWeakReference;
    private final RecyclerView recyclerView;

    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(DownloadableItem item);
    }

    public ItemListAdapter(Context context, ArrayList<DownloadableItem> downloadableItemList,
                           RecyclerView recyclerView,OnBookClickListener listener) {
        this.itemsList = downloadableItemList;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.contextWeakReference = new WeakReference(context);
        this.recyclerView = recyclerView;
        this.listener = listener;

        //Observable for percent of individual downloads.
        mItemDownloadPercentObserver = new ItemDownloadPercentObserver(this);
        //Observable for download request
        mDownloadRequestsSubscriber = new DownloadRequestsSubscriber(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_downloadbook_details, parent, false);
        ItemDetailsViewHolder itemDetailsViewHolder =
                new ItemDetailsViewHolder(view, contextWeakReference.get(), this);
        return itemDetailsViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof ItemDetailsViewHolder)) {
            return;
        }
        final DownloadableItem downloadableItem = itemsList.get(position);
        ItemDetailsViewHolder itemDetailsViewHolder = (ItemDetailsViewHolder) holder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBookClick(downloadableItem);
            }
        });
        if(downloadableItem.getDownloadingStatus() == DownloadingStatus.IN_PROGRESS){
            RxDownloadManagerHelper.queryDownloadPercents(downloadManager, downloadableItem,
                    mItemDownloadPercentObserver.getPercentageObservableEmitter());
        }
        itemDetailsViewHolder.updateImageDetails(downloadableItem);
    }

    public void onDownloadEnqueued(DownloadableItem downloadableItem) {
        mDownloadRequestsSubscriber.emitNextItem(downloadableItem);
    }

    @Override
    public int getItemCount() {
        if (itemsList == null) {
            return 0;
        }
        return itemsList.size();
    }

    @Override
    public void onDownloadStarted(DownloadableItem downloadableItem) {
        //Increment the current number of downloads by 1
        currentDownloadsCount++;
        String downloadUrl = downloadableItem.getBookDownloadUrl();
        String savePath = downloadableItem.getBookZipPath();
        File zipBook = new File(savePath.replace("file:///",""));
        if(zipBook.exists()) {
            zipBook.delete();
        }
        long downloadId =
                RxDownloadManagerHelper.enqueueDownload(downloadManager, downloadUrl, savePath);
        if (downloadId == Constants.INVLALID_ID) {
            return;
        }
        downloadableItem.setDownloadId(downloadId);
        downloadableItem.setPages("Downloading...");
        downloadableItem.setDownloadingStatus(DownloadingStatus.IN_PROGRESS);
        updateDownloadableItem(downloadableItem);
        RxDownloadManagerHelper.queryDownloadPercents(downloadManager, downloadableItem,
                mItemDownloadPercentObserver.getPercentageObservableEmitter());
    }

    @Override
    public void onDownloadComplete(DownloadableItem downloadableItem) {
        //Decrement the current number of downloads by 1
        currentDownloadsCount--;
        mDownloadRequestsSubscriber.requestSongs(Constants.MAX_COUNT_OF_SIMULTANEOUS_DOWNLOADS -
                currentDownloadsCount);
        Utilities.unzip(
                downloadableItem.getBookZipPath().replace("file://",""),
                downloadableItem.getBookPath());
        downloadableItem.setDownloadingStatus(DownloadingStatus.EXTRACTED);
        Book bookData =getBook(downloadableItem);
        if(bookData!=null){
            downloadableItem.setBookName(bookData.getName());
            downloadableItem.setPages("Total Pages: "+String.valueOf(bookData.getPages().length));
        }
        DownloadItemHelper.saveDownloadItems(itemsList);
        updateDownloadableItem(downloadableItem);
    }
    Book getBook(DownloadableItem book){
        Gson gson =new Gson();
        StringBuilder builder = new StringBuilder();
        builder.append(book.getBookPath()).append("/book.json");
        try {
            return gson.fromJson(new FileReader(builder.toString()),Book.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<DownloadableItem> getDownloadItems(){
        return itemsList;
    }

    public void addDownload(final DownloadableItem newItem) {
       DownloadableItem searchedItem =  findItem(newItem.getBookId());
        if(searchedItem ==null) {
            itemsList.add(newItem);
            notifyItemInserted(itemsList.indexOf(newItem));
            DownloadItemHelper.saveDownloadItems(itemsList);
        }
    }

    public void removeDownloadItem(int position) {
        itemsList.remove(position);
        notifyItemRemoved(position);
        DownloadItemHelper.saveDownloadItems(itemsList);
    }

    public void restoreDownloadItem(DownloadableItem item, int position) {
        itemsList.add(position, item);
        notifyItemInserted(position);
        DownloadItemHelper.saveDownloadItems(itemsList);
    }

    DownloadableItem findItem(String bookId) {
        for(DownloadableItem item : itemsList) {
            if(item.getBookId().equals(bookId)) {
                return item;
            }
        }
        return null;
    }

    public void performCleanUp() {
        mItemDownloadPercentObserver.performCleanUp();
        mDownloadRequestsSubscriber.performCleanUp();
    }

    @Override
    public void updateDownloadableItem(DownloadableItem downloadableItem) {
        if (downloadableItem == null || contextWeakReference.get() == null) {
            return;
        }

        int position = Integer.parseInt(downloadableItem.getId()) - 1;
        ItemDetailsViewHolder itemDetailsViewHolder = (ItemDetailsViewHolder)
                recyclerView.findViewHolderForLayoutPosition(position);

        //It means that the viewholder is not currently displayed.
        if (itemDetailsViewHolder == null) {
            if (downloadableItem.getBookDownloadPercent() == Constants.DOWNLOAD_COMPLETE_PERCENT) {
                downloadableItem.setDownloadingStatus(DownloadingStatus.DOWNLOADED);
                onDownloadComplete(downloadableItem);
            }
            return;
        }
        itemDetailsViewHolder.updateImageDetails(downloadableItem);
    }
}
