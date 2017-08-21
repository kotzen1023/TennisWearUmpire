package com.seventhmoon.tenniswearumpire.Audio;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.seventhmoon.tenniswearumpire.Data.Constants;
import com.seventhmoon.tenniswearumpire.Data.Constants.STATE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.seventhmoon.tenniswearumpire.Audio.FileOperation.check_user_voice_exist;

public class VoicePlay {
    private static final String TAG = VoicePlay.class.getName();

    private static Context context;

    public static File RootDirectory = new File("/");

    private static MediaPlayer mediaPlayer;

    public static boolean is_playing = false;

    private static STATE current_state = STATE.Created;
    private static float speed = 1;
    private static float current_volume = 0.5f;

    private static Thread myThread = null;
    private static int total_files = 0;
    private static int current_play = 0;

    public VoicePlay (Context context){
        this.context = context;
    }

    public void doExit() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (myThread != null) {
            Log.e(TAG, "myThread.interrupt()");
            myThread.interrupt();
            myThread = null;
        }
    }

    public static void audioPlayer(Context context, int res_id) {
        //String fileName){
        //Log.e(TAG, "audioPlayer start");
        /*if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //Log.d(TAG, "playing!");


        } else {
            //set up MediaPlayer
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }

            try {

                //mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);
                mediaPlayer = MediaPlayer.create(context, res_id);

                //mediaPlayer.prepare();

                mediaPlayer.start();


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            //is_playing = false;

        }*/
        //Log.e(TAG, "audioPlayer end");
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }

            try {

                //mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);
                mediaPlayer = MediaPlayer.create(context, res_id);

                //mediaPlayer.prepare();

                mediaPlayer.start();


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mIncomingHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.e(TAG, "mIncomingHandler: play finished!");

            current_state = STATE.PlaybackCompleted;

            if (current_play == (total_files - 1)) {
                Log.d(TAG, "Last file played, send finished");
                Intent newNotifyIntent = new Intent(Constants.ACTION.PLAY_MULTIFILES_COMPLETE);
                context.sendBroadcast(newNotifyIntent);
            }

            return true;
        }
    });

    public void doStopAudioPlayMulti() {
        Log.d(TAG, "doStopAudioPlayMulti start");

        if (myThread != null) {
            Log.e(TAG, "myThread.interrupt()");
            myThread.interrupt();
            myThread = null;
        }

        if (mediaPlayer != null) {

            try {
                if (mediaPlayer.isPlaying()) {
                    Log.e(TAG, "mediaPlayer.stop()");
                    mediaPlayer.stop();
                    current_state = STATE.Stopped;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            /*if (mediaPlayer.isPlaying()) {
                Log.e(TAG, "mediaPlayer.stop()");
                mediaPlayer.stop();
                current_state = STATE.Stopped;
            }*/
            try {
                Log.e(TAG, "mediaPlayer.reset()");
                mediaPlayer.reset();
                current_state = STATE.Idle;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            //Log.e(TAG, "mediaPlayer.release()");
            //mediaPlayer.release();
            //current_state = STATE.End;
            //mediaPlayer = null;
        }
        Log.d(TAG, "doStopAudioPlayMulti end");
    }

    public void doRawPlay(ArrayList<Integer> res_id) {
        for (int i = 0; i < res_id.size(); i++) {

            while (checkPlay()) ; //wait for play end

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();

            }
            else {
                //mediaPlayer.stop();
                if (mediaPlayer != null && current_state != STATE.Created &&
                        current_state != STATE.End &&
                        current_state != STATE.Error) {
                    try {
                        mediaPlayer.reset();
                        current_state = STATE.Idle;
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        current_state = STATE.End;
                    }

                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        current_state = STATE.End;
                    }
                }

                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }

            try {

                //mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);
                mediaPlayer = MediaPlayer.create(context, res_id.get(i));
                current_state = STATE.Created;

                //mediaPlayer.prepare();

                mediaPlayer.start();
                current_state = STATE.Started;

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {

                        current_state = STATE.Error;
                        Log.e(TAG, "=====> onError");
                        return false;
                    }
                });


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void singleplaying(String songPath){
        Log.d(TAG, "<singleplaying "+songPath+">");

        //int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
        //int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        //Log.d(TAG, "bitRate = "+bitRate+", sampleRate = "+sampleRate);

        if (mediaPlayer != null) {
            Log.e(TAG, "mediaPlayer != null");

            mediaPlayer.reset();
            //set state
            current_state = STATE.Idle;
            Log.d(TAG, "===>Idle");
        }

        if (mediaPlayer == null) {
            Log.e(TAG, "*** mediaPlayer == null (start)****");

            mediaPlayer = new MediaPlayer();
            //set state
            current_state = STATE.Created;
            Log.d(TAG, "===>Created");

            mediaPlayer.reset();
            //set state
            current_state = STATE.Idle;
            Log.d(TAG, "===>Idle");

            Log.e(TAG, "*** mediaPlayer == null (end)****");
        }


        if (current_state == STATE.Idle) {
            try {

                mediaPlayer.setDataSource(songPath);

                //set state
                current_state = STATE.Initialized;
                Log.d(TAG, "===>Initialized");

                Log.d(TAG, "--->set Prepare");
                mediaPlayer.prepare();
                current_state = STATE.Prepared;

                mediaPlayer.start();
                //set state
                current_state = STATE.Started;


                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d(TAG, "setOnCompletionListener");
                        Message msg = new Message();

                        mIncomingHandler.sendMessage(msg);


                    }
                });



            } catch (IOException e) {
                e.printStackTrace();
                //Intent newNotifyIntent = new Intent(Constants.ACTION.GET_PLAY_COMPLETE);
                //context.sendBroadcast(newNotifyIntent);
            }
        }


        Log.d(TAG, "</singleplaying>");
    }

    public void doFilePlay(ArrayList<String> nameList) {
        Log.d(TAG, "doFilePlay start");

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        total_files = nameList.size();

        for (int i = 0; i < nameList.size(); i++) {

            while (checkPlay()) ; //wait for play end

            if (check_user_voice_exist(nameList.get(i))) {
                String path = RootDirectory.getAbsolutePath() + "/.tennisWearBoard/user/"+nameList.get(i);
                singleplaying(path);
                current_play = i;
            }

        }

        Log.d(TAG, "doFilePlay end");


    }

    public void audioPlayMulti(final ArrayList<Integer> res_id) {


        if (myThread == null) {

            new Thread() {
                public void run() {
                    doRawPlay(res_id);
                }
            }.start();

        }

    }

    public void audioPlayMultiFile(final ArrayList<String> nameList) {


        if (myThread == null) {

            new Thread() {
                public void run() {
                    doFilePlay(nameList);
                }
            }.start();

        }

    }

    private boolean checkPlay() {

        if (mediaPlayer != null) {

            if (current_state != STATE.Error && current_state != STATE.End) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
