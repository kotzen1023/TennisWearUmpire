package com.seventhmoon.tenniswearumpire;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
//import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
//import com.seventhmoon.tenniswearboard.Data.SettingAdapter;

import java.util.ArrayList;


public class ServeActivity extends WearableActivity {
    private static final String TAG = ServeActivity.class.getName();

    Context context;

    //WearableRecyclerView wearableRecyclerView;

    WheelPicker wheelPicker;

    //SettingAdapter settingAdapter;

    ArrayList<String> myList = new ArrayList<>();
    private int selected = 0;

    AlertDialog ad;
    private static String sets;
    private static String games;
    private static String tiebreak;
    private static String deuce;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deuce_layout);

        setAmbientEnabled();

        Log.d(TAG, "onCreate");

        context = getBaseContext();

        sets = getIntent().getStringExtra("SETUP_SET");
        games = getIntent().getStringExtra("SETUP_GAMES");
        tiebreak = getIntent().getStringExtra("SETUP_TIEBREAK");
        deuce = getIntent().getStringExtra("SETUP_DEUCE");

        Log.d(TAG, "Get set = "+sets);
        Log.d(TAG, "Get games = "+games);
        Log.d(TAG, "Get tiebreak = "+tiebreak);
        Log.d(TAG, "Get deuce = "+deuce);


        wheelPicker = (WheelPicker) findViewById(R.id.wheel_picker_deuce);

        //wheelPicker.setCyclic(true);
        wheelPicker.setIndicator(true);
        wheelPicker.setIndicatorColor(Color.GRAY);
        wheelPicker.setAtmospheric(true);
        //wheelPicker.setCurtain(true);
        //wheelPicker.setCurtainColor(Color.BLUE);

        //wearableRecyclerView = (WearableRecyclerView) findViewById(R.id.recycler_launcher_view);

        //wearableRecyclerView.setCenterEdgeItems(true);

        //wearableRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        /*SettingItem item1 = new SettingItem();
        item1.setTitle("1 Set");

        myList.add(item1);

        SettingItem item2 = new SettingItem();
        item2.setTitle("3 Sets");

        myList.add(item2);

        SettingItem item3 = new SettingItem();
        item3.setTitle("5 Sets");

        myList.add(item3);*/
        myList.add(getResources().getString(R.string.setup_serve_first));
        myList.add(getResources().getString(R.string.setup_receive));


        wheelPicker.setData(myList);

        //settingAdapter = new SettingAdapter(myList);
        //wearableRecyclerView.setAdapter(settingAdapter);

        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                Log.d(TAG, "select position "+i);
                selected = i;

                //showResetlog();
                Intent intent = new Intent(ServeActivity.this, ServeConfirmActivity.class);
                intent.putExtra("SETUP_SET", sets);
                intent.putExtra("SETUP_GAMES", games);
                intent.putExtra("SETUP_TIEBREAK", tiebreak);
                intent.putExtra("SETUP_DEUCE", deuce);
                intent.putExtra("SETUP_SERVE", String.valueOf(selected));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");

        if (ad != null)
            ad.cancel();

        super.onPause();
        //mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        //mGoogleApiClient.disconnect();
        super.onDestroy();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        //updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        //updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        //updateDisplay();
        super.onExitAmbient();
    }
}