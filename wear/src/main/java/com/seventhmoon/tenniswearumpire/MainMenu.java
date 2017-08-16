package com.seventhmoon.tenniswearumpire;


import android.content.Context;

import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;


import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.seventhmoon.tenniswearumpire.Data.Constants;
import com.seventhmoon.tenniswearumpire.Data.InitData;
import com.seventhmoon.tenniswearumpire.Data.State;

import java.util.ArrayDeque;
import java.util.ArrayList;

import java.util.Deque;

import static com.seventhmoon.tenniswearumpire.Audio.VoicePlay.audioPlayMulti;

import static com.seventhmoon.tenniswearumpire.Data.InitData.is_debug;
import static com.seventhmoon.tenniswearumpire.Data.InitData.mGoogleApiClient;

import static com.seventhmoon.tenniswearumpire.Data.InitData.mSensorManager;
import static com.seventhmoon.tenniswearumpire.Data.InitData.mStepCounter;



public class MainMenu extends FragmentActivity {
    private static final String TAG = MainMenu.class.getName();

    private static final String TAB_1_TAG = "tab_1";
    private static final String TAB_2_TAG = "tab_2";

    private static Context context;

    public static String set;
    public static String tiebreak;
    public static String deuce;
    public static String serve;
    public static long startTime;
    public static long endTime;
    private static Handler handler;
    private static long time_use = 0;

    public static Deque<State> stack = new ArrayDeque<>();

    private static long previous_time = 0;
    private static long current_time = 0;


    //step
    public static float step_count_start = 0;
    public static float step_count_end = 0;
    //private static long step_count = 0;

    private static ArrayList<Integer> voiceList = new ArrayList<>();

    private static int count = 0;
    public static boolean is_finish = false;

    //private static long current_message_length = 0;
    private static long previous_message_length = 0;
    SensorEventListener sensorEventListener;
    private static boolean is_step_count_change = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        context = getBaseContext();

        InitData.is_running = true;

        setContentView(R.layout.main_menu);

        is_debug = false;

        //wear sync


        InitData.is_running = true;
        InitData.is_voice_enable = false;

        is_finish = false;

        //Button btnYouScore;
        //ImageView btnImgBack;
        //Button btnOpptScore;
        //ImageView btnImgReset;

        //setAmbientEnabled();

        handler = new Handler();

        startTime = System.currentTimeMillis();

        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);

        Intent intent = getIntent();

        set = intent.getStringExtra("SETUP_SET");
        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
        deuce = intent.getStringExtra("SETUP_DEUCE");
        serve = intent.getStringExtra("SETUP_SERVE");

        Log.e(TAG, "SET = "+set);
        Log.e(TAG, "TIEBREAK = "+tiebreak);
        Log.e(TAG, "DEUCE = "+deuce);
        Log.e(TAG, "SERVE = "+serve);



        String initCommand = "init;"+set+";"+tiebreak+";"+deuce+";"+serve+";"+startTime;
        syncSendCommand(initCommand);

        //mContainerView = (BoxInsetLayout) findViewById(R.id.container);



        previous_time = 0;
        current_time = 0;




        //show tips
        toast(getResources().getString(R.string.game_show_tips));

        InitView();

        step_count_start = 0;
        //step counter
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d(TAG, "count = "+String.valueOf(event.values[0]));
                if (step_count_start == 0) {
                    step_count_start = event.values[0];
                }
                step_count_end = event.values[0];

                is_step_count_change = true;

                Intent intent = new Intent(Constants.ACTION.GET_STEP_COUNT_ACTION);
                sendBroadcast(intent);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mSensorManager.registerListener(sensorEventListener, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }
    @Override
    public void onResume() {

        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

        mGoogleApiClient.disconnect();

        time_use = 0;
        stack.clear();
        handler.removeCallbacks(updateTimer);
        InitData.is_running = false;

        //unregister sensor step
        mSensorManager.unregisterListener(sensorEventListener);
        sensorEventListener = null;

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        finish();
    }

    private void InitView() {
        FragmentTabHost mTabHost;

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //mTabHost.addTab(setIndicator(MainMenu.this, mTabHost.newTabSpec(TAB_1_TAG),
        //        R.drawable.tab_indicator_gen, getResources().getString(R.string.scm_history_tab), R.drawable.ic_history_white_48dp), HistoryFragment.class, null);
        mTabHost.addTab(setIndicator(MainMenu.this, mTabHost.newTabSpec(TAB_1_TAG),
                R.drawable.tab_indicator_gen, getResources().getString(R.string.main_point)), PointFragment.class, null);




        //mTabHost.addTab(setIndicator(MainMenu.this, mTabHost.newTabSpec(TAB_2_TAG),
        //        R.drawable.tab_indicator_gen, getResources().getString(R.string.scm_setting), R.drawable.ic_settings_white_48dp), SettingsFragment.class, null);
        mTabHost.addTab(setIndicator(MainMenu.this, mTabHost.newTabSpec(TAB_2_TAG),
                R.drawable.tab_indicator_gen, getResources().getString(R.string.main_game)), GameFragment.class, null);






        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {


                switch (tabId) {
                    case "tab_1":
                        //if (item_clear != null)
                        //    item_clear.setVisible(true);
                        //if (item_search != null)
                        //    item_search.setVisible(true);
                        break;
                    case "tab_2":
                        //if (item_clear != null)
                        //    item_clear.setVisible(false);
                        //if (item_search != null)
                        //    item_search.setVisible(false);
                        break;

                    default:
                        break;

                }
            }
        });
    }



    private TabHost.TabSpec setIndicator(Context ctx, TabHost.TabSpec spec,
                                         int resid, String string) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.tab_item, null);
        v.setBackgroundResource(resid);
        TextView tv = (TextView)v.findViewById(R.id.txt_tabtxt);
        //ImageView img = (ImageView)v.findViewById(R.id.img_tabtxt);

        tv.setText(string);
        //img.setBackgroundResource(genresIcon);
        return spec.setIndicator(v);
    }

    public static void calculateScore(boolean you_score) {
        byte current_set = 0;
        State new_state=null;
        //load top state first
        State current_state = stack.peek();

        int set_limit;
        switch (set)
        {
            case "0":
                set_limit = 1;
                break;
            case "1":
                set_limit = 3;
                break;
            case "2":
                set_limit = 5;
                break;
            default:
                set_limit = 1;
                break;
        }

        if (current_state != null) {
            current_set = current_state.getCurrent_set();
            Log.d(TAG, "########## current state start ##########");
            Log.d(TAG, "default:");
            Log.d(TAG, "set = " + set);
            //Log.d(TAG, "game = " + game);
            Log.d(TAG, "tiebreak = " + tiebreak);
            Log.d(TAG, "deuce = " + deuce);
            Log.d(TAG, "serve = " + serve);
            Log.d(TAG, "======================");

            Log.d(TAG, "current set : " + current_state.getCurrent_set());
            Log.d(TAG, "Serve : " + current_state.isServe());
            Log.d(TAG, "In tiebreak : " + current_state.isInTiebreak());
            Log.d(TAG, "Finish : " + current_state.isFinish());

            //Log.d(TAG, "set 1:");
            Log.d(TAG, "Game : " + current_state.getSet_game_up(current_set) + " / " + current_state.getSet_game_down(current_set));
            Log.d(TAG, "Point : " + current_state.getSet_point_up(current_set) + " / " + current_state.getSet_point_down(current_set));
            Log.d(TAG, "tiebreak : " + current_state.getSet_tiebreak_point_up(current_set) + " / " + current_state.getSet_tiebreak_point_down(current_set));
            Log.d(TAG, "########## current state end ##########");

            if (current_state.isFinish()) {
                Log.d(TAG, "*** Game is Over ***");
                //handler.removeCallbacks(updateTimer);
                //String message = "result";
                //syncSendCommand(message);

                /*Intent intent = new Intent(MainMenu.this, ResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("SET1_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x01)));
                intent.putExtra("SET1_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x01)));
                intent.putExtra("SET2_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x02)));
                intent.putExtra("SET2_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x02)));
                intent.putExtra("SET3_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x03)));
                intent.putExtra("SET3_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x03)));
                intent.putExtra("SET4_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x04)));
                intent.putExtra("SET4_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x04)));
                intent.putExtra("SET5_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x05)));
                intent.putExtra("SET5_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x05)));

                intent.putExtra("SET1_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x01)));
                intent.putExtra("SET1_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x01)));
                intent.putExtra("SET2_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x02)));
                intent.putExtra("SET2_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x02)));
                intent.putExtra("SET3_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x03)));
                intent.putExtra("SET3_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x03)));
                intent.putExtra("SET4_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x04)));
                intent.putExtra("SET4_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x04)));
                intent.putExtra("SET5_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x05)));
                intent.putExtra("SET5_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x05)));

                intent.putExtra("GAME_DURATION", String.valueOf(String.valueOf(time_use)));


                startActivity(intent);*/
            } else { //not finish
                /*if (is_pause) { //
                    is_pause = false;
                    imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                    handler.removeCallbacks(updateTimer);
                    handler.postDelayed(updateTimer, 1000);
                }*/
                Log.d(TAG, "*** Game is running ***");
                if (you_score) {
                    Log.d(TAG, "=== I score start ===");

                    if (stack.isEmpty()) { //the state stack is empty
                        new_state = new State();
                        new_state.setWho_win_this_point(true);
                        Log.d(TAG, "==>[Stack empty]");
                        if (serve.equals("0"))
                            new_state.setServe(true);
                        else
                            new_state.setServe(false);

                        //set current set = 1
                        new_state.setCurrent_set((byte) 0x01);

                        new_state.setDuration(time_use);

                        new_state.setSet_point_down((byte) 0x01, (byte) 0x01);
                        //new_state.setSet_1_point_down((byte)0x01);


                        //Log.e(TAG, "get_set_1_point_down = "+new_state.getSet_1_point_down()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));
                    } else {
                        Log.d(TAG, "==>[Stack not empty]");

                        if (current_state.isFinish()) {
                            Log.d(TAG, "**** Game Finish ****");
                        } else {
                            new_state = new State();
                            new_state.setWho_win_this_point(true);
                            //new_state = stack.peek();
                            // copy previous state;
                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setDuration(time_use);
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }


                            //you score!
                            byte point = current_state.getSet_point_down(current_set);
                            Log.d(TAG, "Your point " + point + " change to " + (++point));
                            new_state.setSet_point_down(current_set, point);

                            checkPoint(new_state);

                            checkGames(new_state);
                        }
                    }

                    Log.d(TAG, "=== I score end ===");
                } else {
                    Log.d(TAG, "=== Oppt score start ===");
                    if (stack.isEmpty()) { //the state stack is empty
                        new_state = new State();
                        new_state.setWho_win_this_point(false);
                        Log.d(TAG, "==>[Stack empty]");
                        if (serve.equals("0"))
                            new_state.setServe(true);
                        else
                            new_state.setServe(false);

                        //set current set = 1
                        new_state.setCurrent_set((byte) 0x01);

                        new_state.setDuration(time_use);

                        new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                        //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));

                    } else {
                        Log.d(TAG, "==>[Stack not empty]");
                        if (current_state.isFinish()) {
                            Log.d(TAG, "**** Game Finish ****");
                        } else {
                            new_state = new State();
                            new_state.setWho_win_this_point(false);
                            //new_state = stack.peek();
                            // copy previous state;
                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setDuration(time_use);
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            //oppt score!
                            byte point = current_state.getSet_point_up(current_set);
                            Log.d(TAG, "Opponent point " + point + " change to " + (++point));
                            new_state.setSet_point_up(current_set, point);

                            checkPoint(new_state);

                            checkGames(new_state);
                        }
                    }
                    Log.d(TAG, "=== Oppt score end ===");
                }

                if (new_state != null) {

                    Log.d(TAG, "########## new state start ##########");
                    Log.d(TAG, "current set : " + new_state.getCurrent_set());
                    Log.d(TAG, "Serve : " + new_state.isServe());
                    Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                    Log.d(TAG, "Finish : " + new_state.isFinish());

                    for (int i = 1; i <= set_limit; i++) {
                        Log.d(TAG, "================================");
                        Log.d(TAG, "[set " + i + "]");
                        Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                        Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                        Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
                    }

                    Log.d(TAG, "########## new state end ##########");




                    //push into stack
                    stack.push(new_state);
                }
            }
        } else {
            Log.d(TAG, "Stack is empty!");

            /*if (is_pause) { //
                is_pause = false;
                imgPlayOrPause.setImageResource(R.drawable.ic_pause_white_48dp);
                handler.removeCallbacks(updateTimer);
                handler.postDelayed(updateTimer, 1000);
            }*/
            Log.d(TAG, "*** Game is running ***");
            if (you_score) {
                Log.d(TAG, "=== I score start ===");

                //if (stack.isEmpty()) { //the state stack is empty
                new_state = new State();
                new_state.setWho_win_this_point(true);
                Log.d(TAG, "==>[Stack empty]");
                if (serve.equals("0"))
                    new_state.setServe(true);
                else
                    new_state.setServe(false);

                //set current set = 1
                new_state.setCurrent_set((byte) 0x01);

                new_state.setDuration(time_use);

                new_state.setSet_point_down((byte) 0x01, (byte) 0x01);
                //new_state.setSet_1_point_down((byte)0x01);


                //Log.e(TAG, "get_set_1_point_down = "+new_state.getSet_1_point_down()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));
                //}

                Log.d(TAG, "=== I score end ===");
            } else {
                Log.d(TAG, "=== Oppt score start ===");
                //if (stack.isEmpty()) { //the state stack is empty
                new_state = new State();
                new_state.setWho_win_this_point(false);
                Log.d(TAG, "==>[Stack empty]");
                if (serve.equals("0"))
                    new_state.setServe(true);
                else
                    new_state.setServe(false);

                //set current set = 1
                new_state.setCurrent_set((byte) 0x01);

                new_state.setDuration(time_use);

                new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));

                //}
                Log.d(TAG, "=== Oppt score end ===");
            }

            Log.d(TAG, "########## new state start ##########");
            Log.d(TAG, "current set : " + new_state.getCurrent_set());
            Log.d(TAG, "Serve : " + new_state.isServe());
            Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
            Log.d(TAG, "Finish : " + new_state.isFinish());

            for (int i = 1; i <= set_limit; i++) {
                Log.d(TAG, "================================");
                Log.d(TAG, "[set " + i + "]");
                Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
            }

            Log.d(TAG, "########## new state end ##########");



            //push into stack
            stack.push(new_state);
        }
    }

    private static void checkPoint(State new_state) {
        Log.d(TAG, "[Check point Start]");

        byte current_set = new_state.getCurrent_set();
        if (new_state.isInTiebreak()) { //in tiebreak
            Log.d(TAG, "[In Tiebreak]");
            byte game;

            /*if ((new_state.getSet_point_up(current_set) == 1 && new_state.getSet_point_down(current_set) == 0) ||
                    (new_state.getSet_point_up(current_set) == 0 && new_state.getSet_point_down(current_set) == 1)) {
                //in tiebreak, add first point should change serve
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }
            } else */
            if (new_state.getSet_point_up(current_set) == 7 && new_state.getSet_point_down(current_set) <= 5) {
                //7 : 0,1,2,3,4,5 => oppt win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_up(current_set);
                game++;
                new_state.setSet_game_up(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }

                //leave tiebreak;
                new_state.setInTiebreak(false);
                voiceList.clear();
                Integer call = R.raw.gbr_man_game;
                voiceList.add(call);
            } else if (new_state.getSet_point_up(current_set) <= 5 && new_state.getSet_point_down(current_set) == 7) {
                //0,1,2,3,4,5 : 7 => you win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_down(current_set);
                game++;
                new_state.setSet_game_down(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }
                //leave tiebreak;
                new_state.setInTiebreak(false);
                voiceList.clear();
                Integer call = R.raw.gbr_man_game;
                voiceList.add(call);
            } else if (new_state.getSet_point_up(current_set) >= 6 &&
                    new_state.getSet_point_down(current_set) >= 6 &&
                    (new_state.getSet_point_up(current_set) - new_state.getSet_point_down(current_set)) == 2) {
                //8:6, 9:7, 10:8.... => oppt win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_up(current_set);
                game++;
                new_state.setSet_game_up(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }

                //leave tiebreak;
                new_state.setInTiebreak(false);
                voiceList.clear();
                Integer call = R.raw.gbr_man_game;
                voiceList.add(call);
            } else if (new_state.getSet_point_up(current_set) >= 6 &&
                    new_state.getSet_point_down(current_set) >= 6 &&
                    (new_state.getSet_point_down(current_set) - new_state.getSet_point_up(current_set)) == 2) {
                //6:8, 7:9, 8:10.... => you win this game
                //set tiebreak point
                new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                //set point clean
                new_state.setSet_point_up(current_set, (byte)0);
                new_state.setSet_point_down(current_set, (byte)0);
                //add to game
                game = new_state.getSet_game_down(current_set);
                game++;
                new_state.setSet_game_down(current_set, game);
                //change serve
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }

                //leave tiebreak;
                new_state.setInTiebreak(false);
                voiceList.clear();
                Integer call = R.raw.gbr_man_game;
                voiceList.add(call);
            }

            byte plus = (byte) (new_state.getSet_point_up(current_set)+new_state.getSet_point_down(current_set));

            if (plus%2 == 1) {
                //change serve
                Log.d(TAG, "==>Points plus become odd, change serve!");
                if (new_state.isServe()) {
                    new_state.setServe(false);
                } else {
                    new_state.setServe(true);
                }
            }

        } else { //not in tiebreak;
            Log.d(TAG, "[Not in Tiebreak]");
            if (deuce.equals("0")) { //use deuce
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) ==4) { //40A:40A => 40:40
                    new_state.setSet_point_up(current_set, (byte)0x03);
                    new_state.setSet_point_down(current_set, (byte)0x03);

                } else if (new_state.getSet_point_up(current_set) == 5 &&
                        new_state.getSet_point_down(current_set) == 3) { //40A+ : 40 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    voiceList.clear();
                    Integer call = R.raw.gbr_man_game;
                    voiceList.add(call);

                } else if (new_state.getSet_point_up(current_set) == 3 &&
                        new_state.getSet_point_down(current_set) == 5) { //40 : 40A+ => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    voiceList.clear();
                    Integer call = R.raw.gbr_man_game;
                    voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 2) { //40A : 0, 40A : 15, 40A : 30 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    voiceList.clear();
                    Integer call = R.raw.gbr_man_game;
                    voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) <=2 &&
                        new_state.getSet_point_down(current_set) == 4) { //0 : 40A, 15 : 40A, 30: 40A => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    voiceList.clear();
                    Integer call = R.raw.gbr_man_game;
                    voiceList.add(call);
                }
                else {
                    Log.d(TAG, "[points change without arrange]");
                }
            } else { //use deciding point
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 3) { //40A : 40,30,15,0 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    voiceList.clear();
                    Integer call = R.raw.gbr_man_game;
                    voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) <= 3 &&
                        new_state.getSet_point_down(current_set) == 4) { //40,30,15,0 : 40A => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    voiceList.clear();
                    Integer call = R.raw.gbr_man_game;
                    voiceList.add(call);
                } else {
                    Log.d(TAG, "[points change without arrange]");
                }
            }
        }
        Log.d(TAG, "current_set = "+current_set);
        Log.d(TAG, "[Check point End]");
    }

    private static void checkGames(State new_state) {
        Log.d(TAG, "[Check Games Start]");
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();
        if (tiebreak.equals("0")) { //use tibreak
            Log.d(TAG, "[Use Tiebreak]");

            if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) == 6) {
                new_state.setInTiebreak(true); //into tiebreak;
                Integer call = R.raw.gbr_man_6;
                voiceList.add(call);
                Integer all = R.raw.gbr_man_all;
                voiceList.add(all);
                Integer tieBreak = R.raw.gbr_man_tiebreak;
                voiceList.add(tieBreak);
            } else if (new_state.getSet_game_up(current_set) == 7 &&
                    new_state.getSet_game_down(current_set) == 5) { // 7:5 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
                //play voice
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            } else if (new_state.getSet_game_up(current_set) == 5 &&
                    new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
                //play voice
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            } else if (new_state.getSet_game_up(current_set) == 7 &&
                    new_state.getSet_game_down(current_set) == 6) { // 7:6 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
                //play voice
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            } else if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            } else if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) <=4 ) { // 6:0,1,2,3,4 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
                //play voice
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            } else if (new_state.getSet_game_up(current_set) <= 4 &&
                    new_state.getSet_game_down(current_set) == 6) { // 0,1,2,3,4:6 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
                //play voice
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            }
        } else {
            if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) <= 5) { // 6:5 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
                //play voice
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            } else if (new_state.getSet_game_up(current_set) <= 5 &&
                    new_state.getSet_game_down(current_set) == 6) { // 5:6 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
                //play voice
                if (InitData.is_voice_enable) {
                    new Thread() {
                        public void run() {
                            audioPlayMulti(context, voiceList);
                        }
                    }.start();
                }
            }
        }


        Log.d(TAG, "[Check Games End]");
    }

    private static void checkSets(State new_state) {
        Log.d(TAG, "[Check sets Start]");
        //check if the game is over
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();

        Integer match;
        Integer gameSet;

        switch (set) {
            case "0": //only one set
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    //voice
                    match = R.raw.gbr_man_match;
                    voiceList.add(match);
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseGameVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseGameVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                    //handler.removeCallbacks(updateTimer);
                    //is_pause = false;
                    //imgPlayOrPause.setVisibility(View.GONE);
                    is_finish = true; //for sync
                    endTime = System.currentTimeMillis();
                } else {
                    is_finish = false;
                }

                break;
            case "1":
                if (setsWinUp == 2 || setsWinDown == 2) {
                    new_state.setFinish(true);
                    //voice
                    match = R.raw.gbr_man_match;
                    voiceList.add(match);
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseGameVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseGameVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                    //handler.removeCallbacks(updateTimer);
                    //is_pause = false;
                    //imgPlayOrPause.setVisibility(View.GONE);
                    is_finish = true;
                    endTime = System.currentTimeMillis();
                } else { // new set
                    //voice
                    switch (current_set) {
                        case 1:
                            gameSet = R.raw.gbr_man_first_set;
                            voiceList.add(gameSet);

                            break;
                        case 2:
                            gameSet = R.raw.gbr_man_second_set;
                            voiceList.add(gameSet);
                            break;
                    }

                    //and play this set
                    if (new_state.getSet_game_up(current_set) > new_state.getSet_game_down(current_set)) {
                        chooseGameVoice(new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    } else {
                        chooseGameVoice(new_state.getSet_game_down(current_set), new_state.getSet_game_up(current_set));
                    }

                    current_set++;
                    new_state.setCurrent_set(current_set);
                    is_finish = false;
                }

                break;
            case "2":
                if (setsWinUp == 3 || setsWinDown == 3) {
                    new_state.setFinish(true);
                    //voice
                    match = R.raw.gbr_man_match;
                    voiceList.add(match);
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseGameVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseGameVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                    //handler.removeCallbacks(updateTimer);
                    //is_pause = false;
                    //imgPlayOrPause.setVisibility(View.GONE);
                    is_finish = true;
                    endTime = System.currentTimeMillis();
                } else { // new set
                    //voice
                    switch (current_set) {
                        case 1:
                            gameSet = R.raw.gbr_man_first_set;
                            voiceList.add(gameSet);
                            break;
                        case 2:
                            gameSet = R.raw.gbr_man_second_set;
                            voiceList.add(gameSet);
                            break;
                        case 3:
                            gameSet = R.raw.gbr_man_third_set;
                            voiceList.add(gameSet);
                            break;
                        case 4:
                            gameSet = R.raw.gbr_man_forth_set;
                            voiceList.add(gameSet);
                            break;
                    }

                    //and play this set
                    if (new_state.getSet_game_up(current_set) > new_state.getSet_game_down(current_set)) {
                        chooseGameVoice(new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    } else {
                        chooseGameVoice(new_state.getSet_game_down(current_set), new_state.getSet_game_up(current_set));
                    }

                    current_set++;
                    new_state.setCurrent_set(current_set);
                    is_finish = false;
                }

                break;
            default:
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    //voice
                    match = R.raw.gbr_man_match;
                    voiceList.add(match);

                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseGameVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseGameVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }
                    //handler.removeCallbacks(updateTimer);
                    //is_pause = false;
                    //imgPlayOrPause.setVisibility(View.GONE);
                    is_finish = true;
                    endTime = System.currentTimeMillis();
                } else {
                    is_finish = false;
                }

                break;
        }

        Log.d(TAG, "[Check sets End]");
    }

    private static void chooseGameVoice(byte gameServe,  byte gameRecv) {
        Integer gameCall, gameCall2;
        if (gameServe == 0 && gameRecv == 1) {
            gameCall = R.raw.gbr_man_love;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_1;
            voiceList.add(gameCall2);
        } else if (gameServe == 0 && gameRecv == 2) {
            gameCall = R.raw.gbr_man_love;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_2;
            voiceList.add(gameCall2);
        } else if (gameServe == 0 && gameRecv == 3) {
            gameCall = R.raw.gbr_man_love;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_3;
            voiceList.add(gameCall2);
        } else if (gameServe == 0 && gameRecv == 4) {
            gameCall = R.raw.gbr_man_love;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_4;
            voiceList.add(gameCall2);
        } else if (gameServe == 0 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_love;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_5;
            voiceList.add(gameCall2);
        } else if (gameServe == 0 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_love;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        } else if (gameServe == 1 && gameRecv == 0) {
            gameCall = R.raw.gbr_man_1;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_love;
            voiceList.add(gameCall2);
        } else if (gameServe == 1 && gameRecv == 1) {
            gameCall = R.raw.gbr_man_1;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_all;
            voiceList.add(gameCall2);
        } else if (gameServe == 1 && gameRecv == 2) {
            gameCall = R.raw.gbr_man_1;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_2;
            voiceList.add(gameCall2);
        } else if (gameServe == 1 && gameRecv == 3) {
            gameCall = R.raw.gbr_man_1;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_3;
            voiceList.add(gameCall2);
        } else if (gameServe == 1 && gameRecv == 4) {
            gameCall = R.raw.gbr_man_1;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_4;
            voiceList.add(gameCall2);
        } else if (gameServe == 1 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_1;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_5;
            voiceList.add(gameCall2);
        } else if (gameServe == 1 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_1;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        } else if (gameServe == 2 && gameRecv == 0) {
            gameCall = R.raw.gbr_man_2;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_love;
            voiceList.add(gameCall2);
        } else if (gameServe == 2 && gameRecv == 1) {
            gameCall = R.raw.gbr_man_2;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_1;
            voiceList.add(gameCall2);
        } else if (gameServe == 2 && gameRecv == 2) {
            gameCall = R.raw.gbr_man_2;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_all;
            voiceList.add(gameCall2);
        } else if (gameServe == 2 && gameRecv == 3) {
            gameCall = R.raw.gbr_man_2;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_3;
            voiceList.add(gameCall2);
        } else if (gameServe == 2 && gameRecv == 4) {
            gameCall = R.raw.gbr_man_2;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_4;
            voiceList.add(gameCall2);
        } else if (gameServe == 2 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_2;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_5;
            voiceList.add(gameCall2);
        } else if (gameServe == 2 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_2;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        } else if (gameServe == 3 && gameRecv == 0) {
            gameCall = R.raw.gbr_man_3;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_love;
            voiceList.add(gameCall2);
        } else if (gameServe == 3 && gameRecv == 1) {
            gameCall = R.raw.gbr_man_3;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_1;
            voiceList.add(gameCall2);
        } else if (gameServe == 3 && gameRecv == 2) {
            gameCall = R.raw.gbr_man_3;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_2;
            voiceList.add(gameCall2);
        } else if (gameServe == 3 && gameRecv == 3) {
            gameCall = R.raw.gbr_man_3;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_all;
            voiceList.add(gameCall2);
        } else if (gameServe == 3 && gameRecv == 4) {
            gameCall = R.raw.gbr_man_3;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_4;
            voiceList.add(gameCall2);
        } else if (gameServe == 3 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_3;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_5;
            voiceList.add(gameCall2);
        } else if (gameServe == 3 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_3;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        } else if (gameServe == 4 && gameRecv == 0) {
            gameCall = R.raw.gbr_man_4;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_love;
            voiceList.add(gameCall2);
        } else if (gameServe == 4 && gameRecv == 1) {
            gameCall = R.raw.gbr_man_4;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_1;
            voiceList.add(gameCall2);
        } else if (gameServe == 4 && gameRecv == 2) {
            gameCall = R.raw.gbr_man_4;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_2;
            voiceList.add(gameCall2);
        } else if (gameServe == 4 && gameRecv == 3) {
            gameCall = R.raw.gbr_man_4;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_3;
            voiceList.add(gameCall2);
        } else if (gameServe == 4 && gameRecv == 4) {
            gameCall = R.raw.gbr_man_4;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_all;
            voiceList.add(gameCall2);
        } else if (gameServe == 4 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_4;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_5;
            voiceList.add(gameCall2);
        } else if (gameServe == 4 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_4;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 0) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_love;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 1) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_1;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 2) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_2;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 3) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_3;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 4) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_4;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_all;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        } else if (gameServe == 5 && gameRecv == 7) {
            gameCall = R.raw.gbr_man_5;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_7;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 0) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_love;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 1) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_1;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 2) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_2;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 3) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_3;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 4) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_4;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_5;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        } else if (gameServe == 6 && gameRecv == 7) {
            gameCall = R.raw.gbr_man_6;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_7;
            voiceList.add(gameCall2);
        } else if (gameServe == 7 && gameRecv == 5) {
            gameCall = R.raw.gbr_man_7;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_5;
            voiceList.add(gameCall2);
        } else if (gameServe == 7 && gameRecv == 6) {
            gameCall = R.raw.gbr_man_7;
            voiceList.add(gameCall);
            gameCall2 = R.raw.gbr_man_6;
            voiceList.add(gameCall2);
        }
    }

    private Runnable updateTimer = new Runnable() {
        public void run() {
            //final TextView time = (TextView) findViewById(R.id.currentTime);
            //NumberFormat f = new DecimalFormat("00");
            //Long spentTime = System.currentTimeMillis() - startTime;

            //計算目前已過分鐘數

            //計算目前已過秒數
            //Long seconds = (time_use) % 60;
            //time.setText(minius+":"+seconds);

            handler.postDelayed(this, 1000);

            //time_use++;
            long step_count = (long) (step_count_end - step_count_start);

            String message;

            if (is_finish) {
                //spentTime = endTime - startTime;
                message = "calibrate&"+step_count+"&"+set+"&"+tiebreak+"&"+deuce+"&"+serve+"&"+startTime+"&"+endTime;
            } else {
                message = "calibrate&"+step_count+"&"+set+"&"+tiebreak+"&"+deuce+"&"+serve+"&"+startTime+"&0";
            }

            //if (is_debug)
            //    Log.d(TAG, "spentTime = "+spentTime);
            String state_msg = "";
            if (!stack.isEmpty()) {
                message = message+"&";
                for (State s : stack) {
                    if (state_msg.equals(""))
                        state_msg = s.getWho_win_this_point() + "";
                    else
                        state_msg = s.getWho_win_this_point() + ";" + state_msg;
                }
                message = message + state_msg;
            }

            syncSendCommand(message);



            /*Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            sdf.setTimeZone(tz);//set time zone.
            Date netDate = new Date(System.currentTimeMillis());
            //Date gameDate = new Date(spentTime);
            Long hour = (spentTime/1000)/3600;
            Long min = (spentTime/1000)%3600/60;
            Long sec = (spentTime/1000)%60;*/
            /*textCurrentTime.setText(sdf.format(netDate));
            if (hour > 0) {
                textGameTime.setText(f.format(hour)+":"+f.format(min));
            } else {
                textGameTime.setText(f.format(min)+":"+f.format(sec));
            }*/

            //textGameTime.setText(sdf.format(gameDate));
        }
    };

    /*private void actionForMessage(String msg) {
        Log.d(TAG, "command action "+msg);

        if (msg.contains("file")) {
            String msgArray[] = msg.split("\\|");
            String fileArray[] = msgArray[1].split(";");
            if (fileArray[1].equals("start")) {
                Log.d(TAG, "receive start, send ok!");
                check_voice_file_exist(current_file_name);

                current_file_name = fileArray[0];
                Log.d(TAG, "current_file_name = "+current_file_name+"  file length = "+fileArray[2]);
                is_tranlate_file = true;
                //send back to send file
                String startMessage = "file|"+"test.txt"+";ok;";
                syncSendCommand(startMessage);


            }

        }
    }*/

    public static void syncSendCommand(String cmd) {
        if (is_debug)
            Log.d(TAG, "syncSendCommand start = "+step_count_start+" end = "+step_count_end);



        if(mGoogleApiClient==null) {
            Log.e(TAG, "mGoogleApiClient = null");
        } else {
            if (is_debug)
                Log.d(TAG, "mGoogleApiClient = " + mGoogleApiClient.isConnected() + " cmd = " + cmd);
            long current_message_length = cmd.length();
            if (current_message_length != previous_message_length) {

                PutDataMapRequest putRequest = PutDataMapRequest.create("/WEAR_COMMAND");
                DataMap map = putRequest.getDataMap();
                //map.putInt("color", Color.RED);
                map.putString("cmd", cmd);
                map.putLong("count", count);
                count++;
                Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());

                previous_message_length = current_message_length;

            } else {
                if (is_step_count_change) {

                    PutDataMapRequest putRequest = PutDataMapRequest.create("/WEAR_COMMAND");
                    DataMap map = putRequest.getDataMap();
                    //map.putInt("color", Color.RED);
                    map.putString("cmd", cmd);
                    map.putLong("count", count);
                    count++;
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());

                    previous_message_length = current_message_length;

                    is_step_count_change = false;
                } else {
                    if (is_debug)
                        Log.d(TAG, "string length is the same, won't send this message");
                }
            }
        }
    }

    /*protected void showResetlog() {

        View promptView = View.inflate(MainMenu.this, R.layout.dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainMenu.this);
        alertDialogBuilder.setView(promptView);

        final TextView title = (TextView) promptView.findViewById(R.id.txtTitle);
        title.setTextColor(Color.BLACK);
        title.setText(getResources().getString(R.string.game_reset));
        //alertDialogBuilder.setTitle(getResources().getString(R.string.game_reset));
        //alertDialogBuilder.setMessage(getResources().getString(R.string.game_reset));
        //final ImageView imgYes = (ImageView) promptView.findViewById(R.id.imgYes);
        //final ImageView imgNo = (ImageView) promptView.findViewById(R.id.imgYes);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                time_use = 0;
                stack.clear();
                handler.removeCallbacks(updateTimer);

                //send reset
                String message = "reset";
                syncSendCommand(message);


                Intent intent = new Intent(MainMenu.this, SetupMain.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }*/

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
