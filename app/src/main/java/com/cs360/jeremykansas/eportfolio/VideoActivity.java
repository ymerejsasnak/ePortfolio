package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private VideoView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        setTitle("Now Playing " + getIntent().getStringExtra("title"));

        ImageButton playButton = findViewById(R.id.playButton);
        ImageButton stopButton = findViewById(R.id.stopButton);

        view = findViewById(R.id.videoView);
        view.setVideoPath(getIntent().getStringExtra("path"));


        view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoActivity.this, "An error has occured with your video.", Toast.LENGTH_SHORT).show();
                finish();
                return true; // handled
            }
        });

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
