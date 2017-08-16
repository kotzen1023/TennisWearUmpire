package com.seventhmoon.tenniswearumpire.Audio;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VoicePlay {
    private static final String TAG = VoicePlay.class.getName();

    public static File RootDirectory = new File("/");

    private static MediaPlayer mediaPlayer;

    public static boolean is_playing = false;


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

    public static void audioPlayMulti(Context context, ArrayList<Integer> res_id) {
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
        for (int i=0;i<res_id.size(); i++) {

            while (checkPlay(context)); //wait for play end

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
                mediaPlayer = MediaPlayer.create(context, res_id.get(i));

                //mediaPlayer.prepare();

                mediaPlayer.start();


                //mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }

    public static boolean checkPlay(Context context) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }
}
