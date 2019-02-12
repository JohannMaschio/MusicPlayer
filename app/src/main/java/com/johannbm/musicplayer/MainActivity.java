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

        // call method to set up
        setUpUI();

        // set the max for seekbar and Listener
        seekBarID.setMax(mediaPlayer.getDuration());
        seekBarID.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // check if the changed come from user and go to the progress passed
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }

                // format the time to be displayed
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                // set time for the fields
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

    // Set up for layout
    public void setUpUI(){

        // init de media player and pass the music
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kalimba);

        //set up
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

        // Set up listener for the buttons
        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    // onClick and switch
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //case previous button clicked
            case R.id.prevButton:
                backMusic();
                break;

            //case play button clicked
            case R.id.playButton:
                //check if media is playing
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                } else {
                    startMusic();
                }
                break;

            //case next button clicked
            case R.id.nextButton:
                nextMusic();
                break;
        }
    }

    // Start music method and set pause button
    public void startMusic(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }

    }

    // pause method and set play button
    public void pauseMusic(){
        if (mediaPlayer != null){
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);

        }
    }

    //back method *for now the method just back to the beginning of the song*
    public void backMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
            Toast toast = Toast.makeText(getApplicationContext(), "Back to start", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // next music method *for now the method just go to the end of the song*
    public void nextMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
            Toast toast = Toast.makeText(getApplicationContext(), "TO the end", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //thread for move seekbar and update the time left and right
    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                //while mediaplayer is not null and is playing the thread will update
                while (mediaPlayer != null && mediaPlayer.isPlaying()){
                    //thy catch for erros
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
        // Start the thread
        thread.start();
    }

    //clearing the memory
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
