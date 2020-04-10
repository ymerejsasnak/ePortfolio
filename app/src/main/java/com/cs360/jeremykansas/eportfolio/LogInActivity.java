package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    EditText userName;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.userName);
        login = findViewById(R.id.login);

        // get preferences ("user")
        final SharedPreferences preferences = getSharedPreferences("sharedPrefFile", MODE_PRIVATE);
        String user = preferences.getString("user", "");


        // if user string is not empty, jump right into main activity with user string
        if (!user.equals("")) {
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }

        // log in button listener checks that a username has been input (if not, shows toast)
        // if user name is input, it is stored in in sharedPref and main activity is started
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = userName.getText().toString();
                if (user.equals("")) {
                    Toast.makeText(LogInActivity.this, "Must enter user name", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("user", user);
                    editor.apply();

                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}
