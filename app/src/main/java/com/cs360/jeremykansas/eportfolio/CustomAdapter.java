package com.cs360.jeremykansas.eportfolio;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*
 *  This class is used in creating each individual item in the RecyclerView list
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    Activity activity;
    private ArrayList itemIDs, itemTitles, itemPaths;

    CustomAdapter(Activity _activity,
                  Context _context,
                  ArrayList _itemIDs,
                  ArrayList _itemTitles,
                  ArrayList _itemPaths) {
        activity = _activity;
        context = _context;
        itemIDs = _itemIDs;
        itemTitles = _itemTitles;
        itemPaths = _itemPaths;
    }

    // on creation, inflates the view described in recycler_row.xml resource
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater  inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(view);
    }


    // primary functionality of each row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        // set title
        holder.itemTitle.setText(String.valueOf(itemTitles.get(position)));

        // load path to extract mime type (drop subtype) and use to set icon
        Uri uri = Uri.parse(String.valueOf(itemPaths.get(position)));
        ContentResolver cR = context.getContentResolver();

        String mime = cR.getType(uri);
        String type = mime.substring(0, mime.indexOf('/'));

        switch (type) {
            case "audio":
                holder.typeImage.setImageDrawable(context.getDrawable(R.drawable.ic_audio));
                break;
            case "video":
                holder.typeImage.setImageDrawable(context.getDrawable(R.drawable.ic_video));
                break;
            case "image":
                holder.typeImage.setImageDrawable(context.getDrawable(R.drawable.ic_image));
                break;
            case "text":
                holder.typeImage.setImageDrawable(context.getDrawable(R.drawable.ic_text));
                break;
        }








        // display/play file when row is clicked on
        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //**************************
                //this is where the file loading/viewing/playing code goes (or at least intent to new activity or whatever)
                String text = "[placeholder] " + itemTitles.get(position);
                Toast.makeText(v.getContext(), text ,Toast.LENGTH_SHORT).show();
                // *********************
            }
        });

        // go to update activity when row's update arrow button is clicked
        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(itemIDs.get(position)));
                intent.putExtra("title", String.valueOf(itemTitles.get(position)));
                intent.putExtra("path", String.valueOf(itemPaths.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemIDs.size();
    }


    // stores references to row's views for use in onBindViewHolder above
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitle;
        ImageView typeImage;
        ImageButton updateButton;
        LinearLayout rowLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitleText);
            typeImage = itemView.findViewById(R.id.typeImage);
            updateButton = itemView.findViewById(R.id.updateButton);

            rowLayout = itemView.findViewById(R.id.rowLayout);
        }

    }
}
