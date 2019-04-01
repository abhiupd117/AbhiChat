package com.amandeep.abhichat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.amandeep.abhichat.Model.Chat;

public class VideoviewPly extends AppCompatActivity {
    private VideoView mVideoView;
    private TextView  mCurrenttime;
    private TextView mDurrationTime;
    private ImageView mPlayButton;
    private ProgressBar mCurrentProgressbar;
    private Uri mvideouri;
    private ProgressBar mBufferprogressbar;
    private Boolean isPlaying;
    private int tCurrent=0;
    private int tDuration=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        mCurrenttime=findViewById(R.id.current_time);
        mDurrationTime=findViewById(R.id.duration_time);
        mPlayButton=findViewById(R.id.video_play_btn);
        mVideoView=findViewById(R.id.videoView);
        mCurrentProgressbar=findViewById(R.id.progressBar3);
        mBufferprogressbar=findViewById(R.id.bufferProgressbar);
        isPlaying=true;
        mPlayButton.setImageResource(R.drawable.pause_button);
        mCurrentProgressbar.setMax(100);

        Intent getVideourl= getIntent();

        mvideouri= Uri.parse(getVideourl.getStringExtra("videourl"));
        mVideoView.setVideoURI(mvideouri);
        mVideoView.requestFocus();

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int media_start, int extra) {

                if (media_start==mp.MEDIA_INFO_BUFFERING_START){
                    mBufferprogressbar.setVisibility(View.VISIBLE);
                }
                else if (extra==mp.MEDIA_INFO_BUFFERING_END){
                    mBufferprogressbar.setVisibility(View.INVISIBLE);
                }
                return false;
            }


        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                tDuration=mp.getDuration()/1000;
                String durationTime=String.format("%02d: %02d",tDuration /60, tDuration%60);
                mDurrationTime.setText(durationTime);
            }
        });


        mVideoView.start();
        isPlaying=true;

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying){
                    mVideoView.pause();
                    isPlaying=false;
                    mPlayButton.setImageResource(R.drawable.play_button);
                }
                else {
                    mVideoView.start();

                    isPlaying=true;
                    mPlayButton.setImageResource(R.drawable.pause_button);
                    new VideoProgress().execute();
                }
            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();
        isPlaying=false;
    }

    public class VideoProgress extends AsyncTask<Void, Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            do{
                if (isPlaying) {

                    tCurrent = mVideoView.getCurrentPosition() / 1000;
                    publishProgress(tCurrent);
                }

            }
            while (mCurrentProgressbar.getProgress()<=100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try {
                int currentpercent=tCurrent * 100/tDuration;
                mCurrentProgressbar.setProgress(currentpercent);
                String durationTime=String.format("%02d: %02d",tDuration /60, tDuration%60);
                mCurrenttime.setText(durationTime);

                publishProgress(currentpercent);
            }
            catch (Exception e){

            }
        }
    }
}
