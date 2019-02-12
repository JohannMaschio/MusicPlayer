package com.johannbm.musicplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private ImageView line;
    private TextView songName;
    private TextView artistName;
    private SeekBar seekBarID;
    private TextView leftTime;
    private TextView rightTime;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBarID.setMax(mediaPlayer.getDuration());
        seekBarID.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTime.setText(dateFormat.format(new Date(currentPos)));
                rightTime.setText(dateFormat.format( new Date(duration - currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // Set up from layout
    public void setUpUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kalimba);

        artistImage = findViewById(R.id.imageViewOval);
        line = findViewById(R.id.imageViewLine);
        songName = findViewById(R.id.textViewSongNameID);
        artistName = findViewById(R.id.textViewArtistName);
        seekBarID = findViewById(R.id.seekBarMusicID);
        leftTime = findViewById(R.id.leftTime);
        rightTime = findViewById(R.id.rightTime);
        prevButton = findViewById(R.id.prevButton);
        playButton = findViewById(R.id.playButton);
        nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.prevButton:
                backMusic();
                break;

            case R.id.playButton:
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                } else {
                    startMusic();
                }
                break;

            case R.id.nextButton:
                nextMusic();
                break;
        }
    }

    // Start music metod and set pause button
    public void startMusic(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }

    }

    // pause metod and set play button
    public void pauseMusic(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);

        }
    }

    public void backMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
            Toast toast = Toast.makeText(getApplicationContext(), "Back to start", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void nextMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
            Toast toast = Toast.makeText(getApplicationContext(), "TO the end", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void updateThread(){

        thread = new Thread(){
            @Override
            public void run() {
                while (mediaPlayer != null && mediaPlayer.isPlaying()){
                    try {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBarID.setMax(newMax);
                                seekBarID.setProgress(newPosition);

                                leftTime.setText(String.valueOf(new SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getCurrentPosition()))));
                                rightTime.setText(String.valueOf(new SimpleDateFormat("mm:ss").format( new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));
                            }
                        });
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;

        super.onDestroy();
    }
}
