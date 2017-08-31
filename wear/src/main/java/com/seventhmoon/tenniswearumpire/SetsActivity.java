package com.seventhmoon.tenniswearumpire;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.wearable.activity.WearableActivity;

import android.support.wearable.view.BoxInsetLayout;

import android.util.Log;


import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.seventhmoon.tenniswearumpire.Data.InitData;


import java.util.ArrayList;






public class SetsActivity extends WearableActivity {
    private static final String TAG = SetsActivity.class.getName();

    Context context;

    private BoxInsetLayout mContainerView;

    WheelPicker wheelPicker;

    //SettingAdapter settingAdapter;

    ArrayList<String> myList = new ArrayList<>();
    private int selected = 0;

    AlertDialog ad;

    public static InitData myData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sets_layout);

        context = getBaseContext();

        setAmbientEnabled();

        mContainerView = findViewById(R.id.set_container);
        //mTextView = (TextView) findViewById(R.id.text);
        //mClockView = (TextView) findViewById(R.id.clock);

        Log.d(TAG, "onCreate");

        myData = new InitData();

        myData.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        myData.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "mGoogleApiClient ==> onConnected");
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();

        myData.mGoogleApiClient.connect();



        wheelPicker = findViewById(R.id.wheel_picker_sets);

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
        myList.add(getResources().getString(R.string.setup_one_set));
        myList.add(getResources().getString(R.string.setup_three_sets));
        myList.add(getResources().getString(R.string.setup_five_sets));

        wheelPicker.setData(myList);

       //settingAdapter = new SettingAdapter(myList);
        //wearableRecyclerView.setAdapter(settingAdapter);

        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                Log.d(TAG, "select position "+i);
                selected = i;

                //showResetlog();
                Intent intent = new Intent(SetsActivity.this, SetsConfirmActivity.class);
                intent.putExtra("SETUP_SET", String.valueOf(selected));
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
        myData.mGoogleApiClient.disconnect();
        super.onDestroy();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(Color.BLACK);
            wheelPicker.setItemTextColor(Color.WHITE);
            wheelPicker.setIndicatorColor(Color.WHITE);
            //mTextView.setTextColor(getResources().getColor(android.R.color.white));
            //mClockView.setVisibility(View.VISIBLE);

            //mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            wheelPicker.setItemTextColor(Color.GRAY);
            wheelPicker.setIndicatorColor(Color.GRAY);
            //mTextView.setTextColor(getResources().getColor(android.R.color.black));
            //mClockView.setVisibility(View.GONE);
        }
    }
}
