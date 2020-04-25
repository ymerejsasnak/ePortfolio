package com.cs360.jeremykansas.eportfolio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private TextView emptyText;
    private ImageView imageView;
    private TextView textView;


    private String userName;


    private DatabaseHelper db;
    private ArrayList<String> itemIDs;
    private ArrayList<String> itemTitles;
    private ArrayList<String> itemPaths;

    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);
        emptyText = findViewById(R.id.emptyText);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        // get user name from preferences
        SharedPreferences preferences = getSharedPreferences("sharedPrefFile", MODE_PRIVATE);
        userName = preferences.getString("user", "");
        setTitle(userName + "'s ePortfolio");

        // call add activity from floating add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                MainActivity.this.startActivityForResult(intent, 1);
            }
        });

        // once imageview is visible...if clicked it becomes invisible
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.INVISIBLE);
            }
        });

        // same for text view
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setVisibility(View.INVISIBLE);
            }
        });




        db = new DatabaseHelper(MainActivity.this);
        itemIDs = new ArrayList<>();
        itemTitles = new ArrayList<>();
        itemPaths = new ArrayList<>();
        loadData(); // fill arraylists with data from db

        customAdapter = new CustomAdapter(MainActivity.this,MainActivity.this, itemIDs, itemTitles, itemPaths);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    // refresh view after returning from other activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    // read data from DB and load data into arraylists
    private void loadData() {
        Cursor cursor = db.readData();
        if (cursor.getCount() == 0) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                itemIDs.add(cursor.getString(0));
                itemTitles.add(cursor.getString(1));
                itemPaths.add(cursor.getString(2));
            }
            emptyText.setVisibility(View.GONE);
        }
    }

    // add menu buttons to top bar - delete all and map
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // run appropriate code if menu option selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            confirmDialog();
        } else if (item.getItemId() == R.id.view_map) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.info) {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    // reset pref if back button hit from this activity
    @Override
    public boolean onSupportNavigateUp(){

        SharedPreferences preferences = getSharedPreferences("sharedPrefFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", "");
        editor.apply();
        finish();

        return true;
    }

    // confirmation before delete all
    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all items from your portfolio?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                db.deleteAllItems();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 'No' selection does nothing
            }
        });
        builder.create().show();
    }

}
