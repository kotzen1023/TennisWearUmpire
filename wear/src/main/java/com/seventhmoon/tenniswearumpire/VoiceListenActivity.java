package com.seventhmoon.tenniswearumpire;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seventhmoon.tenniswearumpire.Audio.VoicePlay;
import com.seventhmoon.tenniswearumpire.Data.ListenChooseArrayAdapter;
import com.seventhmoon.tenniswearumpire.Data.ListenChooseItem;

import java.util.ArrayList;
import java.util.Random;

import static com.seventhmoon.tenniswearumpire.PointActivity.step_count_end;
import static com.seventhmoon.tenniswearumpire.PointActivity.step_count_start;
import static com.seventhmoon.tenniswearumpire.PointActivity.voicePlay;


public class VoiceListenActivity extends WearableActivity {
    private static final String TAG = VoiceListenActivity.class.getName();

    public ArrayList<ListenChooseItem> listenList = new ArrayList<>();
    private ListenChooseArrayAdapter listenChooseArrayAdapter;
    private ListView listView;

    private Context context;

    private VoicePlay listenPlay;

    private static ArrayList<Integer> myPlayList = new ArrayList<>();
    private ArrayList<Integer> gbr_man_list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getBaseContext();

        setAmbientEnabled();

        setContentView(R.layout.voice_listen);

        listView = findViewById(R.id.voiceListenListView);


        if (voicePlay == null) {
            listenPlay = new VoicePlay(context);
        } else {
            Log.e(TAG, "voicePlay is running");
        }

        initVoiceArray();

        listenList.clear();

        ListenChooseItem item0 = new ListenChooseItem("GBR Man");
        listenList.add(item0);

        ListenChooseItem item1 = new ListenChooseItem("GBR Woman");
        listenList.add(item1);

        listenChooseArrayAdapter = new ListenChooseArrayAdapter(VoiceListenActivity.this, R.layout.voice_listen_choose_item, listenList);
        listView.setAdapter(listenChooseArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doRandomPlay(position);

            }
        });
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");


        super.onPause();
        //mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");


        super.onDestroy();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        Log.e(TAG, "onEnterAmbient");

    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        Log.e(TAG, "onUpdateAmbient");

    }

    @Override
    public void onExitAmbient() {
        Log.e(TAG, "onExitAmbient");

        super.onExitAmbient();
    }

    private void initVoiceArray() {
        gbr_man_list.clear();
        gbr_man_list.add(R.raw.gbr_man_0_15);
        gbr_man_list.add(R.raw.gbr_man_0_30);
        gbr_man_list.add(R.raw.gbr_man_0_40);
        gbr_man_list.add(R.raw.gbr_man_1);
        gbr_man_list.add(R.raw.gbr_man_10);
        gbr_man_list.add(R.raw.gbr_man_11);
        gbr_man_list.add(R.raw.gbr_man_12);
        gbr_man_list.add(R.raw.gbr_man_13);
        gbr_man_list.add(R.raw.gbr_man_14);
        gbr_man_list.add(R.raw.gbr_man_15);
        gbr_man_list.add(R.raw.gbr_man_15_0);
        gbr_man_list.add(R.raw.gbr_man_15_15);
        gbr_man_list.add(R.raw.gbr_man_15_30);
        gbr_man_list.add(R.raw.gbr_man_15_40);
        gbr_man_list.add(R.raw.gbr_man_16);
        gbr_man_list.add(R.raw.gbr_man_17);
        gbr_man_list.add(R.raw.gbr_man_18);
        gbr_man_list.add(R.raw.gbr_man_19);
        gbr_man_list.add(R.raw.gbr_man_2);
        gbr_man_list.add(R.raw.gbr_man_20);
        gbr_man_list.add(R.raw.gbr_man_3);
        gbr_man_list.add(R.raw.gbr_man_30);
        gbr_man_list.add(R.raw.gbr_man_30_0);
        gbr_man_list.add(R.raw.gbr_man_30_30);
        gbr_man_list.add(R.raw.gbr_man_30_40);
        gbr_man_list.add(R.raw.gbr_man_4);
        gbr_man_list.add(R.raw.gbr_man_40);
        gbr_man_list.add(R.raw.gbr_man_40_0);
        gbr_man_list.add(R.raw.gbr_man_40_15);
        gbr_man_list.add(R.raw.gbr_man_40_30);
        gbr_man_list.add(R.raw.gbr_man_40_40);
        gbr_man_list.add(R.raw.gbr_man_5);
        gbr_man_list.add(R.raw.gbr_man_50);
        gbr_man_list.add(R.raw.gbr_man_6);
        gbr_man_list.add(R.raw.gbr_man_60);
        gbr_man_list.add(R.raw.gbr_man_7);
        gbr_man_list.add(R.raw.gbr_man_70);
        gbr_man_list.add(R.raw.gbr_man_8);
        gbr_man_list.add(R.raw.gbr_man_80);
        gbr_man_list.add(R.raw.gbr_man_9);
        gbr_man_list.add(R.raw.gbr_man_90);
        gbr_man_list.add(R.raw.gbr_man_ad_recv);
        gbr_man_list.add(R.raw.gbr_man_ad_serve);
        gbr_man_list.add(R.raw.gbr_man_all);
        gbr_man_list.add(R.raw.gbr_man_deciding_point);
        gbr_man_list.add(R.raw.gbr_man_first_set);
        gbr_man_list.add(R.raw.gbr_man_forth_set);
        gbr_man_list.add(R.raw.gbr_man_game);
        gbr_man_list.add(R.raw.gbr_man_love);
        gbr_man_list.add(R.raw.gbr_man_match);
        gbr_man_list.add(R.raw.gbr_man_second_set);
        gbr_man_list.add(R.raw.gbr_man_set);
        gbr_man_list.add(R.raw.gbr_man_third_set);
        gbr_man_list.add(R.raw.gbr_man_tiebreak);
    }

    private void doRandomPlay(int listenChoose) {
        Log.d(TAG, "doRandomPlay");
        Random r = new Random();
        int call = r.nextInt(gbr_man_list.size());
        Log.d(TAG, "call = "+call);
        myPlayList.clear();
        switch (listenChoose) {
            case 0://gbr man
                myPlayList.add(gbr_man_list.get(call));
                if (voicePlay == null) {
                    listenPlay.doStopAudioPlayMulti();
                    listenPlay.audioPlayMulti(myPlayList);
                } else {
                    voicePlay.doStopAudioPlayMulti();
                    voicePlay.audioPlayMulti(myPlayList);
                }

                break;
        }
        Log.d(TAG, "doRandomPlay");
    }
}
