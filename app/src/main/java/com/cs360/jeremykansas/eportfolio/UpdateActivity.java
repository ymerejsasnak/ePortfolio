package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    private EditText editTitle;
    private TextView pathText;

    private String id;
    private String title;
    private String path;

    private static final int REQUEST_CODE_OPEN = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        editTitle = findViewById(R.id.editTitleUpdate);
        pathText = findViewById(R.id.pathText);
        ImageButton fileButton = findViewById(R.id.fileButton);
        Button updateButton = findViewById(R.id.updateButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // same as in add activity...how to easily reuse the code? fragments?
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] type = {"image/*", "video/*", "audio/*", "text/*",
                        "application/msword", "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessing"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, type);
                startActivityForResult(intent, REQUEST_CODE_OPEN);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(UpdateActivity.this);

                boolean ready = true;
                if (path.equals("")) {
                    Toast.makeText(UpdateActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
                    ready = false;
                }
                if (title.equals("")) {
                    Toast.makeText(UpdateActivity.this, "Please title your item", Toast.LENGTH_SHORT).show();
                    ready = false;
                }
                if (ready) {
                    db.addItem(title, path);
                }

                db.updateItem(id, editTitle.getText().toString().trim(),
                        pathText.getText().toString().trim());
                finish();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });

        getAndSetIntentData(); // to display what is currently stored before updating

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }

    private void getAndSetIntentData() {
        if (getIntent().hasExtra("id") &&
            getIntent().hasExtra("title") &&
            getIntent().hasExtra("path")) {

            // get data from Main
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            path = getIntent().getStringExtra("path");

            // set data into UI elements
            editTitle.setText(title);
            pathText.setText(path);

        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    // user must confirm before deleting
    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + "?");
        builder.setMessage("Are you sure you want to delete " + title + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(UpdateActivity.this);
                db.deleteItem(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // clicking no does nothing
            }
        });
        builder.create().show();
    }

    // update pathText after using file picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_CODE_OPEN && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                pathText.setText(uri.toString());
            }
        }
    }
}
