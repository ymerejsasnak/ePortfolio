package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.io.IOException;

public class AudioActivity extends AppCompatActivity {

    private MediaPlayer player;
    private ImageButton playPauseButton;
    private ImageButton stopButton;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        setTitle("Now Playing " + getIntent().getStringExtra("title"));

        //  load sound into mediaplayer (path name sent from mainactivity)
        player = MediaPlayer.create(this, Uri.parse(getIntent().getStringExtra("path")));

        playPauseButton = findViewById(R.id.playPauseButton);
        stopButton = findViewById(R.id.stopButton);
        seekBar = findViewById(R.id.seekBar);

        // set seekbar max value to duration of the sound
        seekBar.setMax(player.getDuration());


        // create handler to execute runnable every 500 ms to update seekbar position
        final Handler handler = new Handler();
        AudioActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(player != null){
                    int currentPos = player.getCurrentPosition();
                    seekBar.setProgress(currentPos, true);
                }
                // adds this anonymous runnable to message queue, then runs again, etc...
                handler.postDelayed(this, 500);
            }
        });

        // if sound plays to end, stop/reset player and seekbar (in reset method)
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                reset();
            }
        });

        // play/pause button function and icon depends on if playing or paused
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                    playPauseButton.setImageDrawable(getDrawable(R.drawable.ic_play));
                } else {
                    play(seekBar.getProgress());
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        // seekbar listener -- interact with seekbar to change playback position
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nothing needed here
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                if (player != null && fromTouch) {
                    play(progress);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null) {
                    play(seekBar.getProgress());
                }
            }
        });

    }

    // when activity is destroyed, clean up the mediaplayer
    @Override
    protected void onDestroy() {
        player.stop();
        player.release();
        player = null;
        super.onDestroy();
    }

    // stop player, reset to start, put play icon on play/pause button
    private void reset() {
        player.stop();
        seekBar.setProgress(0, true);
        try {
            player.prepare();
            player.seekTo(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        playPauseButton.setImageDrawable(getDrawable(R.drawable.ic_play));
    }

    // play: seek to 'progress', start play, change button icon to pause icon
    private void play(int progress) {
        player.seekTo(progress);
        player.start();
        playPauseButton.setImageDrawable(getDrawable(R.drawable.ic_pause));
    }

}
