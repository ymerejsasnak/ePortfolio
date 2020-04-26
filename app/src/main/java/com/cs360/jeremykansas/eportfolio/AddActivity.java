package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/*
 *  Add new data row to table
 */
@SuppressWarnings("SpellCheckingInspection")
public class AddActivity extends AppCompatActivity {

    private EditText editTitle;
    private TextView pathText;

    private static final int REQUEST_CODE_OPEN = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTitle = findViewById(R.id.editTitle);
        pathText = findViewById(R.id.pathText);
        ImageButton fileButton = findViewById(R.id.fileButton);
        Button addButton = findViewById(R.id.addButton);

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] type = {"image/*", "video/*", "audio/*", "text/*",
                        "application/msword", "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, type);
                startActivityForResult(intent, REQUEST_CODE_OPEN);

            }
        });

        // only add if item has a path (ie file was chosen)
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(AddActivity.this);
                String path = pathText.getText().toString();
                String title = editTitle.getText().toString().trim();
                boolean ready = true;

                if (path.equals("")) {
                    Toast.makeText(AddActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
                    ready = false;
                }
                if (title.equals("")) {
                    Toast.makeText(AddActivity.this, "Please title your item", Toast.LENGTH_SHORT).show();
                    ready = false;
                }

                if (ready) {
                    db.addItem(title, path);
                }

            }
        });

    }

    // update path textview on return from file picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_CODE_OPEN && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                String path = uri.toString();
                String title = path.substring(path.lastIndexOf('/'));
                pathText.setText(path);
                editTitle.setText(title);
            }
        }
    }
}
