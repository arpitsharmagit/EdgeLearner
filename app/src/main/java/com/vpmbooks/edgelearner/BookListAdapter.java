package com.vishalprakashan.edgelearner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.vishalprakashan.edgelearner.Models.BookModel;
import java.util.ArrayList;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<BookModel> bookmarkList;
    private OnItemClickListener listener;

    interface OnItemClickListener {
        void onItemClick(BookModel item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
        public void bind(final BookModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public BookListAdapter(Context context,
                               ArrayList<BookModel> bookmarkList,
                               OnItemClickListener listener) {
        this.context = context;
        this.bookmarkList = bookmarkList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BookModel item = bookmarkList.get(position);
        holder.name.setText(item.getBookName());
        holder.bind(bookmarkList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public void removeBookModel(int position) {
        bookmarkList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreBookModel(BookModel item, int position) {
        bookmarkList.add(position, item);
        notifyItemInserted(position);
    }
}