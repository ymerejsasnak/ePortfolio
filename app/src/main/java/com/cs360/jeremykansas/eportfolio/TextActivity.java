package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TextActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        setTitle(getIntent().getStringExtra("title"));

        textView = findViewById(R.id.textView);

        textView.setText(getAssetText());
    }


    String getAssetText() {
        Uri uri = getIntent().getData();

        // Stringbuilder to 'gather' the characters
        StringBuilder text = new StringBuilder();

        // read uri content character by character and append it to the string builder
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);

            int in = reader.read();

            while (in != -1) {
                text.append((char)in);
                in = reader.read();
            }
            reader.close();
        }

        catch (IOException e) {
            // need to add proper error handling here
            Toast.makeText(TextActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return text.toString();
    }
}


