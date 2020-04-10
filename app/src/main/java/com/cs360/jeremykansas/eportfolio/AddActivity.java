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
public class AddActivity extends AppCompatActivity {

    EditText editTitle;
    TextView pathText;
    ImageButton fileButton;
    Button addButton;

    private static final int REQUEST_CODE_OPEN = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTitle = findViewById(R.id.editTitle);
        pathText = findViewById(R.id.pathText);
        fileButton = findViewById(R.id.fileButton);
        addButton = findViewById(R.id.addButton);

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] type = {"image/*", "video/*", "audio/*", "text/*"}; // rough list for now...
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
                if (path.equals("")) {
                    Toast.makeText(AddActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
                } else {
                    db.addItem(editTitle.getText().toString().trim(), path);
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
                pathText.setText(uri.toString());
            }
        }
    }
}
