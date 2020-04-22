package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView view;
    ImageButton playButton, stopButton;
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        setTitle("Now Playing " + getIntent().getStringExtra("title"));

        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);

        view = findViewById(R.id.videoView);
        view.setVideoPath(getIntent().getStringExtra("path"));




        view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                view.setVideoPath(getIntent().getStringExtra("path"));
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.isPlaying()) {
                    view.seekTo(0);
                } else {
                    view.start();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.isPlaying()) {
                    view.stopPlayback();

                    view.setVideoPath(getIntent().getStringExtra("path"));


                }
            }
        });
    }
}
