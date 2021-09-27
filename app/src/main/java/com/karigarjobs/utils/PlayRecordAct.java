package com.karigarjobs.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.R;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PlayRecordAct extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    String textstr="",filep="",destfile="",uid="",filenum="";

    private static int MAX_TIME_REC_MS = 20*1000; //20 ms
    private static int MAX_FILE_REC_SZ = 40*1000;  //40 kbyte
    private static final String LOG_TAG = "PlayRecordAct";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;


    boolean mStartPlayingQuest = true;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;


    //private RecordButton recordButton = null;
    private MediaRecorder recorder = null;
    //private PlayButton   playButton = null;
    private MediaPlayer   player = null;


    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    ImageButton recordButton, recplayButton;
    RelativeLayout rec_layout,recplay_layout;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record);
        rec_layout = findViewById(R.id.record_layout);
        recplay_layout = findViewById(R.id.recplay_layout);
        RelativeLayout full_layout = findViewById(R.id.all_record_lay);
        full_layout.setOnTouchListener(new TouchHandler(this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                //Toast.makeText(getApplicationContext(), "Swipe Left gesture detected", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                //Toast.makeText(getApplicationContext(), "Swipe Right gesture detected", Toast.LENGTH_SHORT).show();
            }
        });



        Bundle b = getIntent().getExtras();
        if(b != null) {
            textstr = b.getString("str");
            filep = b.getString("file");
            destfile = b.getString("desfile");
            filenum = b.getString("filenum");
            uid = b.getString("uid");

        }
        TextView textTitle =  (TextView)findViewById(R.id.textView1);
        textTitle.setText(textstr);

        //TextView textstartstop = findViewById(R.id.stop_start_title);
        //textstartstop.setText(getResources().getString(R.string.title_startrecord));

        //TextView textlistenres = findViewById(R.id.textView);
        //textlistenres.setText(getResources().getString(R.string.title_listenanswer));


        final ImageButton imgplay = (ImageButton)findViewById(R.id.button_play);
        imgplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgplay.setEnabled(false);
                stopother1();
                if(!isQuesPlaying())
                    mStartPlayingQuest = true;
                onPlayQuestion(mStartPlayingQuest);
                mStartPlayingQuest = !mStartPlayingQuest;
                imgplay.setEnabled(true);
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }

        final ImageButton rejectbtn = (ImageButton)findViewById(R.id.cancel_btn);
        rejectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectbtn.setEnabled(false);
                //delete audio file
                File fdelete = new File(fileName);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Log.d("PlayRecordAct:"," File deleted on reject!");
                    }
                }

                onStop();
                //finish();
                recplay_layout.setVisibility(View.GONE);
                rec_layout.setVisibility(View.VISIBLE);
                rejectbtn.setEnabled(true);
            }
        });

        final ImageButton acceptbtn = (ImageButton)findViewById(R.id.accp_btn);
        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptbtn.setEnabled(false);
                onStop();
                finish();
            }
        });

        recordButton = (ImageButton)findViewById(R.id.button_startrec);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordButton.setEnabled(false);
                stopother3();
                if(!isRecording())
                    mStartRecording = true;

                onRecord(mStartRecording);

                if(!mStartRecording) {
                    recplay_layout.setVisibility(View.VISIBLE);
                    rec_layout.setVisibility(View.GONE);
                }

                mStartRecording = !mStartRecording;
                recordButton.setEnabled(true);
            }
        });


        recplayButton = (ImageButton)findViewById(R.id.button_speakplay);
        recplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recplayButton.setEnabled(false);
                stopother2();
                if(!isRecPlaying())
                    mStartPlaying = true;

                onPlay(mStartPlaying);
                mStartPlaying = !mStartPlaying;
                recplayButton.setEnabled(true);
            }
        });

        stopother1();
        onPlayQuestion(mStartPlayingQuest);
        mStartPlayingQuest = !mStartPlayingQuest;



        //file name
        fileName = destfile  + filenum + ".mp3";
        File f = new File(getExternalFilesDir(null),fileName);
        try {
            if(!f.exists()) {
                f.createNewFile();
                rec_layout.setVisibility(View.VISIBLE);
                recplay_layout.setVisibility(View.GONE);
            } else {
                recplay_layout.setVisibility(View.VISIBLE);
                rec_layout.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            rec_layout.setVisibility(View.VISIBLE);
            recplay_layout.setVisibility(View.GONE);
        }

    }


    void stopother3()
    {
        //stop both question mplayer and rec mplayer
        //mStartPlayingQuest = false;
        onPlayQuestion(false);

        //mStartPlaying = false;
        onPlay(false);

    }

    void stopother2()
    {
        //stop both question mplayer and recorder
        //mStartPlayingQuest = false;
        onPlayQuestion(false);

        //mStartRecording = false;
        onRecord(false);
    }

    void stopother1()
    {

        //stop both recorder and rec mplayer
        //mStartPlaying = false;
        onPlay(false);

        //mStartRecording = false;
        onRecord(false);

    }

    private boolean isQuesPlaying()
    {
        return (mediaPlayer != null) && (mediaPlayer.isPlaying());
    }

    private boolean isRecording()
    {
        return (recorder != null);
    }

    private boolean isRecPlaying()
    {
        return (player != null) && (player.isPlaying());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.updateResources(base));
    }

    @Override
    public void onBackPressed() {
        //Intent mIntent = new Intent(getApplicationContext(), HomeActivityU.class);
        //startActivity(mIntent);
        onStop();
        File fdelete = new File(fileName);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("PlayRecordAct:"," File deleted on reject!");
            }
        }


        finish();
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }

            if (player != null) {
                player.release();
                player = null;
            }

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "release() failed,"+e.toString());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted )
            finish();

    }


    private void onPlayQuestion(boolean start) {
        if (start) {
            startPlayingQues();
        } else {
            stopPlayingQues();
        }
    }

    private void startPlayingQues() {
        //mediaPlayer = new MediaPlayer();
        if(mediaPlayer!=null)
        {
            stopPlayingQues();
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(this, Integer.valueOf(filep));
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            //mediaPlayer.prepare();


        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() ques failed"+e.toString());
        }
    }

    private void stopPlayingQues() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG, "release() failed"+e.toString());
        }
    }



    private void onRecord(boolean start) {
        if (start) {
            startRecording();
            //setText("Stop recording");
            recordButton.setImageResource(R.mipmap.stopbutton);
            recplayButton.setEnabled(false);
            updatetimestp();
        } else {
            stopRecording();
            //setText("Start recording");
            recordButton.setImageResource(R.mipmap.recordbutton);
            recplayButton.setEnabled(true);
            TextView tv_rectm = findViewById(R.id.time_textview);
            tv_rectm.setText("00:00");
            removehandle();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        try {
            if(player!= null)
                stopPlaying();

            player = new MediaPlayer();
            player.setDataSource(fileName);
            player.prepare();
            player.start();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        try {
            if (player != null) {
                player.release();
                player = null;
            }
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG, "release() failed");
        }
    }

    private void startRecording() {

        if(recorder!= null)
        {
            stopRecording();
        }
        recorder = new MediaRecorder();
        recorder.setMaxDuration(MAX_TIME_REC_MS);
        recorder.setMaxFileSize(MAX_FILE_REC_SZ);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }


    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }


    Runnable runnable1;
    private void updatetimestp()
    {
        final int interval = 1000; // 1 Second
        final int[] texttime = {0};
        runnable1 = new Runnable(){
            public void run() {
                TextView tv_rectm = findViewById(R.id.time_textview);
                int val = texttime[0]+1000;
                String strval = "00:";
                if(texttime[0] < 10000)
                    strval = strval + "0" + val/1000;
                else
                    strval = strval + val/1000;

                tv_rectm.setText(strval);
                texttime[0] = texttime[0] + 1000;
                if(texttime[0] < MAX_TIME_REC_MS )
                    handler.postDelayed(runnable1, interval);
                else {
                    //Stop recording after MAX_TIME_REC_MS
                    onRecord(false);
                    handler.removeCallbacksAndMessages(null);
                }
            }
        };

        handler.postAtTime(runnable1, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable1, interval);
    }

    private void removehandle()
    {
        handler.removeCallbacksAndMessages(null);
    }

}
