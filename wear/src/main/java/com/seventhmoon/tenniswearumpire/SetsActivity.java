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
import android.view.View;
import android.widget.FrameLayout;


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

    private WheelPicker wheelPicker;
    private WheelPicker gameInSet_WheelPicker;
    private WheelPicker tieBreak_WheelPicker;
    private WheelPicker deuce_WheelPicker;
    private WheelPicker serve_WheelPicker;
    private FrameLayout frameLayout;

    //SettingAdapter settingAdapter;

    ArrayList<String> myList = new ArrayList<>();
    ArrayList<String> myList_games_in_set = new ArrayList<>();
    ArrayList<String> myList_tiebreak = new ArrayList<>();
    ArrayList<String> myList_deuce = new ArrayList<>();
    ArrayList<String> myList_serve = new ArrayList<>();
    private int selected = 0;
    private int games_in_set_selected = 0;
    private int tiebreak_selected = 0;
    private int deuce_selected = 0;
    private int serve_selected = 0;

    AlertDialog ad;

    public static InitData myData;

    int REQUEST_CODE = 0;
    int current_choose = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sets_layout);

        context = getBaseContext();

        setAmbientEnabled();

        mContainerView = findViewById(R.id.set_container);
        frameLayout = findViewById(R.id.frameLayoutbBack);
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
        gameInSet_WheelPicker = findViewById(R.id.wheel_picker_games_in_set);
        tieBreak_WheelPicker = findViewById(R.id.wheel_picker_tiebreak);
        deuce_WheelPicker = findViewById(R.id.wheel_picker_deuce);
        serve_WheelPicker = findViewById(R.id.wheel_picker_serve);


        //wheelPicker.setCyclic(true);
        wheelPicker.setIndicator(true);
        wheelPicker.setIndicatorColor(Color.GRAY);
        wheelPicker.setAtmospheric(true);

        gameInSet_WheelPicker.setIndicator(true);
        gameInSet_WheelPicker.setIndicatorColor(Color.GRAY);
        gameInSet_WheelPicker.setAtmospheric(true);

        tieBreak_WheelPicker.setIndicator(true);
        tieBreak_WheelPicker.setIndicatorColor(Color.GRAY);
        tieBreak_WheelPicker.setAtmospheric(true);

        deuce_WheelPicker.setIndicator(true);
        deuce_WheelPicker.setIndicatorColor(Color.GRAY);
        deuce_WheelPicker.setAtmospheric(true);

        serve_WheelPicker.setIndicator(true);
        serve_WheelPicker.setIndicatorColor(Color.GRAY);
        serve_WheelPicker.setAtmospheric(true);
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

        myList_games_in_set.add(getResources().getString(R.string.setup_six_games));
        myList_games_in_set.add(getResources().getString(R.string.setup_four_game));
        gameInSet_WheelPicker.setData(myList_games_in_set);

        myList_tiebreak.add(getResources().getString(R.string.setup_tiebreak));
        myList_tiebreak.add(getResources().getString(R.string.setup_deciding_game));
        tieBreak_WheelPicker.setData(myList_tiebreak);

        myList_deuce.add(getResources().getString(R.string.setup_deuce));
        myList_deuce.add(getResources().getString(R.string.setup_deciding_point));
        deuce_WheelPicker.setData(myList_deuce);

        myList_serve.add(getResources().getString(R.string.setup_serve_first));
        myList_serve.add(getResources().getString(R.string.setup_receive));
        serve_WheelPicker.setData(myList_serve);

       //settingAdapter = new SettingAdapter(myList);
        //wearableRecyclerView.setAdapter(settingAdapter);

        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                Log.d(TAG, "select position "+i);
                selected = i;

                Intent newIntent = new Intent(SetsActivity.this, DialogActivity.class);
                newIntent.putExtra("TITLE", myList.get(selected));
                startActivityForResult(newIntent, REQUEST_CODE);

            }
        });

        gameInSet_WheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                Log.d(TAG, "select position "+i);
                games_in_set_selected = i;

                Intent newIntent = new Intent(SetsActivity.this, DialogActivity.class);
                newIntent.putExtra("TITLE", myList_games_in_set.get(games_in_set_selected));
                startActivityForResult(newIntent, REQUEST_CODE);

            }
        });

        tieBreak_WheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                Log.d(TAG, "select position "+i);
                tiebreak_selected = i;

                Intent newIntent = new Intent(SetsActivity.this, DialogActivity.class);
                newIntent.putExtra("TITLE", myList_tiebreak.get(tiebreak_selected));
                startActivityForResult(newIntent, REQUEST_CODE);

            }
        });

        deuce_WheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                Log.d(TAG, "select position "+i);
                deuce_selected = i;

                Intent newIntent = new Intent(SetsActivity.this, DialogActivity.class);
                newIntent.putExtra("TITLE", myList_deuce.get(deuce_selected));
                startActivityForResult(newIntent, REQUEST_CODE);

            }
        });

        serve_WheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                Log.d(TAG, "select position "+i);
                serve_selected = i;

                Intent newIntent = new Intent(SetsActivity.this, DialogActivity.class);
                newIntent.putExtra("TITLE", myList_serve.get(serve_selected));
                startActivityForResult(newIntent, REQUEST_CODE);

            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_choose == 1) { //game in set
                    wheelPicker.setVisibility(View.VISIBLE);
                    gameInSet_WheelPicker.setVisibility(View.GONE);
                    tieBreak_WheelPicker.setVisibility(View.GONE);
                    deuce_WheelPicker.setVisibility(View.GONE);
                    serve_WheelPicker.setVisibility(View.GONE);
                    //first, set gone
                    frameLayout.setVisibility(View.GONE);
                    current_choose = 0;
                } else if (current_choose == 2) { //tiebreak
                    wheelPicker.setVisibility(View.GONE);
                    gameInSet_WheelPicker.setVisibility(View.VISIBLE);
                    tieBreak_WheelPicker.setVisibility(View.GONE);
                    deuce_WheelPicker.setVisibility(View.GONE);
                    serve_WheelPicker.setVisibility(View.GONE);
                    current_choose = 1;
                } else if (current_choose == 3) { //deuce
                    wheelPicker.setVisibility(View.GONE);
                    gameInSet_WheelPicker.setVisibility(View.GONE);
                    tieBreak_WheelPicker.setVisibility(View.VISIBLE);
                    deuce_WheelPicker.setVisibility(View.GONE);
                    serve_WheelPicker.setVisibility(View.GONE);
                    current_choose = 2;
                } else if (current_choose == 4) { //serve
                    wheelPicker.setVisibility(View.GONE);
                    gameInSet_WheelPicker.setVisibility(View.GONE);
                    tieBreak_WheelPicker.setVisibility(View.GONE);
                    deuce_WheelPicker.setVisibility(View.VISIBLE);
                    serve_WheelPicker.setVisibility(View.GONE);
                    current_choose = 3;
                }
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
            gameInSet_WheelPicker.setItemTextColor(Color.WHITE);
            gameInSet_WheelPicker.setIndicatorColor(Color.WHITE);
            tieBreak_WheelPicker.setItemTextColor(Color.WHITE);
            tieBreak_WheelPicker.setIndicatorColor(Color.WHITE);
            deuce_WheelPicker.setItemTextColor(Color.WHITE);
            deuce_WheelPicker.setIndicatorColor(Color.WHITE);
            serve_WheelPicker.setItemTextColor(Color.WHITE);
            serve_WheelPicker.setIndicatorColor(Color.WHITE);
            //mTextView.setTextColor(getResources().getColor(android.R.color.white));
            //mClockView.setVisibility(View.VISIBLE);

            //mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            wheelPicker.setItemTextColor(Color.GRAY);
            wheelPicker.setIndicatorColor(Color.GRAY);
            gameInSet_WheelPicker.setItemTextColor(Color.GRAY);
            gameInSet_WheelPicker.setIndicatorColor(Color.GRAY);
            tieBreak_WheelPicker.setItemTextColor(Color.GRAY);
            tieBreak_WheelPicker.setIndicatorColor(Color.GRAY);
            deuce_WheelPicker.setItemTextColor(Color.GRAY);
            deuce_WheelPicker.setIndicatorColor(Color.GRAY);
            serve_WheelPicker.setItemTextColor(Color.GRAY);
            serve_WheelPicker.setIndicatorColor(Color.GRAY);
            //mTextView.setTextColor(getResources().getColor(android.R.color.black));
            //mClockView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.e(TAG, "requestCode = "+resultCode);
        switch(resultCode){
            case RESULT_OK:
                Log.e(TAG, "OK, select "+selected);

                if (current_choose == 0) { //sets select
                    wheelPicker.setVisibility(View.GONE);
                    gameInSet_WheelPicker.setVisibility(View.VISIBLE);
                    tieBreak_WheelPicker.setVisibility(View.GONE);
                    deuce_WheelPicker.setVisibility(View.GONE);
                    serve_WheelPicker.setVisibility(View.GONE);
                    current_choose = 1;

                    frameLayout.setVisibility(View.VISIBLE);
                } else if (current_choose == 1) { //games in set select

                    wheelPicker.setVisibility(View.GONE);
                    gameInSet_WheelPicker.setVisibility(View.GONE);
                    tieBreak_WheelPicker.setVisibility(View.VISIBLE);
                    deuce_WheelPicker.setVisibility(View.GONE);
                    serve_WheelPicker.setVisibility(View.GONE);
                    current_choose = 2;

                    //frameLayout.setVisibility(View.VISIBLE);
                } else if (current_choose == 2) { //tiebreak select

                    wheelPicker.setVisibility(View.GONE);
                    gameInSet_WheelPicker.setVisibility(View.GONE);
                    tieBreak_WheelPicker.setVisibility(View.GONE);
                    deuce_WheelPicker.setVisibility(View.VISIBLE);
                    serve_WheelPicker.setVisibility(View.GONE);
                    current_choose = 3;

                    //frameLayout.setVisibility(View.VISIBLE);
                } else if (current_choose == 3) { //tiebreak select

                    wheelPicker.setVisibility(View.GONE);
                    gameInSet_WheelPicker.setVisibility(View.GONE);
                    tieBreak_WheelPicker.setVisibility(View.GONE);
                    deuce_WheelPicker.setVisibility(View.GONE);
                    serve_WheelPicker.setVisibility(View.VISIBLE);
                    current_choose = 4;

                    //frameLayout.setVisibility(View.VISIBLE);
                }

                else { //serve select
                    /*Intent intent = new Intent(SetsActivity.this, GamesInSetActivity.class);
                    intent.putExtra("SETUP_SET", String.valueOf(selected));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);*/
                    Intent intent = new Intent(SetsActivity.this, PointActivity.class);
                    intent.putExtra("SETUP_SET", String.valueOf(selected));
                    intent.putExtra("SETUP_GAMES", String.valueOf(games_in_set_selected));
                    intent.putExtra("SETUP_TIEBREAK", String.valueOf(tiebreak_selected));
                    intent.putExtra("SETUP_DEUCE",  String.valueOf(deuce_selected));
                    intent.putExtra("SETUP_SERVE", String.valueOf(serve_selected));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                }


                //finish();

                break;
            case RESULT_CANCELED:
                Log.e(TAG, "CANCELED");
                break;
            //case EDIT:
            //    Toast.makeText(this, data.getExtras().getString("B"), 0).show();
        }
    }
}
