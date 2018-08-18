package com.learnsolution.edgelearner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.learnsolution.edgelearner.Models.BookModel;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<BookModel> implements View.OnClickListener{

    private ArrayList<BookModel> dataSet;
    Context mContext;
    AlertDialog.Builder alert;
    BookModel bookModelToDelete;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtPages;
        ImageView info;
        ImageButton btndelete;
    }

    public CustomAdapter(ArrayList<BookModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
        alert = new AlertDialog.Builder(this.mContext);

    }

    @Override
    public void onClick(final View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        bookModelToDelete=(BookModel)object;

        alert.setTitle("Delete Book "+ bookModelToDelete.getBookName());
        alert.setMessage("Are you sure you want to delete?");

        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(bookModelToDelete.getBookPath());
                if(file.exists()){
                    file.delete();
                    Snackbar.make(v, "Book Deleted " +bookModelToDelete.getBookName(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });

        switch (v.getId())
        {
            case R.id.item_info:
                alert.show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookModel BookModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtPages = (TextView) convertView.findViewById(R.id.pages);
            viewHolder.info =(ImageView) convertView.findViewById(R.id.default_book);
//            viewHolder.btndelete =(ImageButton) convertView.findViewById(R.id.btnDelete);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(BookModel.getBookName());
        viewHolder.txtPages.setText("Total Pages: "+BookModel.getBookPages());
        //viewHolder.btndelete.setOnClickListener(this);
        //viewHolder.btndelete.setTag(position);
        viewHolder.info.setTag(position);
        return convertView;
    }
}
