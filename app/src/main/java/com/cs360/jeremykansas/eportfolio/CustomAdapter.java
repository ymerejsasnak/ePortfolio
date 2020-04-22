package com.cs360.jeremykansas.eportfolio;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

        // set icon based on mime type
        Drawable icon = null;
        String type = getBaseType(position);

        switch (type) {
            case "audio":
                icon = context.getDrawable(R.drawable.ic_audio);
                break;
            case "video":
                icon = context.getDrawable(R.drawable.ic_video);
                break;
            case "image":
                icon = context.getDrawable(R.drawable.ic_image);
                break;
            case "text":
            case "application":
                icon = context.getDrawable(R.drawable.ic_text);
                break;
        }
        holder.typeImage.setImageDrawable(icon);


        // display/play file when row is clicked on
        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = String.valueOf(itemPaths.get(position));
                Uri uri = Uri.parse(path);
                String title = String.valueOf(itemTitles.get(position));

                String type = getBaseType(position);

                switch (type) {
                    case "audio":
                        // start activity to play audio
                        Intent audioIntent = new Intent(context, AudioActivity.class);
                        audioIntent.putExtra("path", path);
                        audioIntent.putExtra("title", title);
                        activity.startActivity(audioIntent);
                        break;
                    case "video":
                        // start activity that plays video, similar to audio player?
                        Intent videoIntent = new Intent(context, VideoActivity.class);
                        videoIntent.putExtra("path", path);
                        videoIntent.putExtra("title", title);
                        activity.startActivity(videoIntent);
                        break;
                    case "image":
                        // load image into invisible image view and set it to visible
                        // (covers rest of screen, onclick becomes invisible again)
                        ImageView imageView = activity.findViewById(R.id.imageView);
                        imageView.setImageURI(uri);
                        imageView.setVisibility(View.VISIBLE);
                        break;
                    case "text":
                        // do like above, but with a text view

                        File file = new File(path);

                        StringBuilder text = new StringBuilder();

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String line;

                            while ((line = br.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }
                            br.close();
                        }
                        catch (IOException e) {
                            // need to add proper error handling here
                        }

                        TextView textView = activity.findViewById(R.id.textView);
                        textView.setText(text.toString());
                        textView.setVisibility(View.VISIBLE);


                        break;
                    case "application":
                        // use intent to open outside app for pdf/doc/docx
                        Intent appIntent = new Intent();
                        appIntent.setAction(Intent.ACTION_SEND);
                        appIntent.setDataAndType(uri, type);
                        appIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        activity.startActivity(Intent.createChooser(appIntent, "Open with:"));


                        break;
                    default:
                        break;
                }

            }
        });

        // go to update activity when row's update button is clicked
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


        // allow user to share item to chosen app when share button is clicked
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get basic mime type to provide appropriate apps to share to, and file/path name
                String type = getItemType(position);
                String path = String.valueOf(itemPaths.get(position));

                createShareIntent(type, path);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemIDs.size();
    }


    private String getItemType(int position) {
        // parse path to uri, use CR to get mime type
        String path = String.valueOf(itemPaths.get(position));
        Uri uri = Uri.parse(path);
        ContentResolver cR = context.getContentResolver();
        String type = cR.getType(uri);

        return type;
    }

    private String getBaseType(int position) {
        String type = getItemType(position);
        return type.substring(0, type.indexOf('/'));
    }


    // stores references to row's views for use in onBindViewHolder above
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitle;
        ImageView typeImage;
        ImageButton updateButton, shareButton;
        LinearLayout rowLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitleText);
            typeImage = itemView.findViewById(R.id.typeImage);
            updateButton = itemView.findViewById(R.id.updateButton);
            shareButton = itemView.findViewById(R.id.shareButton);

            rowLayout = itemView.findViewById(R.id.rowLayout);
        }

    }


    // create, setup, start share intent
    private void createShareIntent(String type, String path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(type);
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        context.startActivity(Intent.createChooser(share, "Share your work to:"));
    }
}
