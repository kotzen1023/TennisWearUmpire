package com.seventhmoon.tenniswearumpire;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.seventhmoon.tenniswearumpire.Audio.VoicePlay;
import com.seventhmoon.tenniswearumpire.Data.Constants;
import com.seventhmoon.tenniswearumpire.Data.InitData;
import com.seventhmoon.tenniswearumpire.Data.State;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;

import static com.seventhmoon.tenniswearumpire.Data.Constants.VOICE_TYPE.GBR_MAN;
//import static com.seventhmoon.tenniswearboard.Data.InitData.is_debug;
//import static com.seventhmoon.tenniswearboard.Data.InitData.mGoogleApiClient;
//import static com.seventhmoon.tenniswearboard.Data.InitData.mSensorManager;
//import static com.seventhmoon.tenniswearboard.Data.InitData.mStepCounter;
import static com.seventhmoon.tenniswearumpire.SetsActivity.myData;


public class PointActivity extends WearableActivity {
    private static final String TAG = PointActivity.class.getName();



    private Context context;

    private BoxInsetLayout pointContainer;

    private TextView headOppt;
    private TextView headYou;

    private TextView pointUp;
    private TextView pointDown;
    private ImageView imgServeUp;
    private ImageView imgServeDown;

    private TextView btnPoints;
    private TextView btnGames;

    private LinearLayout layoutFBack;
    private LinearLayout layoutBtnBack;
    private LinearLayout layoutBtnVoice;
    private ImageView imageViewPointVoice;
    private LinearLayout layoutPointStepCount;
    private TextView stepCountPoint;

    private LinearLayout layoutBtn;
    private TextView textViewTime;

    public static String set;
    public static String games;
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
    private static ArrayList<String> voiceUserList = new ArrayList<>();

    private static int count = 0;
    public static boolean is_finish = false;

    //private static long current_message_length = 0;
    private static long previous_message_length = 0;
    SensorEventListener sensorEventListener;
    private static boolean is_step_count_change = false;
    private static boolean is_current_game_over = false;

    public static VoicePlay voicePlay;
    public static boolean voiceOn = false;

    public static Constants.VOICE_TYPE current_voice_type = GBR_MAN;

    private static boolean am_I_Tiebreak_First_Serve = false;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.point_activity);

        setAmbientEnabled();

        context = getBaseContext();

        Intent intent = getIntent();

        set = intent.getStringExtra("SETUP_SET");
        games = intent.getStringExtra("SETUP_GAMES");
        tiebreak = intent.getStringExtra("SETUP_TIEBREAK");
        deuce = intent.getStringExtra("SETUP_DEUCE");
        serve = intent.getStringExtra("SETUP_SERVE");

        Log.e(TAG, "SET = "+set);
        Log.e(TAG, "GAMES = "+games);
        Log.e(TAG, "TIEBREAK = "+tiebreak);
        Log.e(TAG, "DEUCE = "+deuce);
        Log.e(TAG, "SERVE = "+serve);

        pointContainer = (BoxInsetLayout) findViewById(R.id.pointContainer);

        layoutFBack = (LinearLayout) findViewById(R.id.layoutPointPFBack);
        layoutBtnBack = findViewById(R.id.layoutBtnBack);
        layoutBtnVoice = findViewById(R.id.layoutBtnVoice);
        imageViewPointVoice = findViewById(R.id.imageViewPointVoice);
        layoutPointStepCount = (LinearLayout) findViewById(R.id.layoutPointStepCount);
        stepCountPoint = (TextView) findViewById(R.id.stepCountPoint);

        headOppt = (TextView) findViewById(R.id.headOppt);
        headYou = (TextView) findViewById(R.id.headYou);

        pointUp = (TextView) findViewById(R.id.textViewPointPFPointUp);
        pointDown = (TextView) findViewById(R.id.textViewPointPFPointdown);

        imgServeUp = (ImageView) findViewById(R.id.imageViewPointPFServeUp);
        imgServeDown = (ImageView) findViewById(R.id.imageViewPointPFServeDown);

        btnPoints = (TextView) findViewById(R.id.textViewPointPoints);
        btnGames = (TextView) findViewById(R.id.textViewPointGames);

        layoutBtn = (LinearLayout) findViewById(R.id.layoutBtn);
        textViewTime = (TextView) findViewById(R.id.textViewpointTime);

        loadState();

        if (myData == null) {

        } else {
            if (myData.mGoogleApiClient != null) {

                if (!myData.mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "mGoogleApiClient is not connected.");
                    myData.mGoogleApiClient.connect();
                }
                else {
                    Log.d(TAG, "mGoogleApiClient is connected.");
                }
            } else { //mGoogleApiClient == null
                myData.mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle connectionHint) {
                                Log.e(TAG, "mGoogleApiClient ==> onConnected");
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
            }

            myData.is_running = true;

            myData.is_debug = false;

            myData.is_running = true;
            myData.is_voice_enable = false;
        }








        voicePlay = new VoicePlay(context);



        is_finish = false;

        handler = new Handler();

        startTime = System.currentTimeMillis();

        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);

        String initCommand = "init;"+set+";"+games+";"+tiebreak+";"+deuce+";"+serve+";"+startTime;
        syncSendCommand(initCommand);

        previous_time = 0;
        current_time = 0;

        //show tips
        toast(getResources().getString(R.string.game_show_tips));

        step_count_start = 0;

        layoutBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (voiceOn) {
                    voiceOn = false;
                    imageViewPointVoice.setImageResource(R.drawable.ic_keyboard_voice_black_off_48dp);
                    toast("Voice Off");
                } else {
                    voiceOn = true;
                    imageViewPointVoice.setImageResource(R.drawable.ic_keyboard_voice_black_48dp);
                    toast("Voice On");
                }*/
            }
        });

        layoutBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stack.isEmpty()) {
                    Log.d(TAG, "stack is empty!");

                } else {
                    if (is_finish) { //is end?
                        startTime = startTime + (System.currentTimeMillis() - endTime);
                    }

                    is_finish = false;


                    byte current_set;
                    //handler.removeCallbacks(updateTimer);
                    //handler.postDelayed(updateTimer, 1000);
                    //stack.pop();
                    if (stack.pop() != null) { //pop out current
                        State back_state = stack.peek();
                        if (back_state != null) {
                            current_set = back_state.getCurrent_set();

                            //gameUp.setText(String.valueOf(back_state.getSet_game_up(current_set)));
                            //gameDown.setText(String.valueOf(back_state.getSet_game_down(current_set)));

                            if (back_state.isServe()) {
                                imgServeUp.setVisibility(View.INVISIBLE);
                                imgServeDown.setVisibility(View.VISIBLE);
                            } else {
                                imgServeUp.setVisibility(View.VISIBLE);
                                imgServeDown.setVisibility(View.INVISIBLE);
                            }

                            if (!back_state.isInTiebreak()) { //not in tiebreak
                                if (back_state.getSet_point_up(current_set) == 1) {
                                    pointUp.setText(String.valueOf(15));
                                } else if (back_state.getSet_point_up(current_set) == 2) {
                                    pointUp.setText(String.valueOf(30));
                                } else if (back_state.getSet_point_up(current_set) == 3) {
                                    pointUp.setText(String.valueOf(40));
                                } else if (back_state.getSet_point_up(current_set) == 4) {
                                    String msg = String.valueOf(40)+"A";
                                    pointUp.setText(msg);
                                } else {
                                    pointUp.setText("0");
                                }
                            } else { //tie break;
                                pointUp.setText(String.valueOf(back_state.getSet_point_up(current_set)));
                            }

                            if (!back_state.isInTiebreak()) { //not in tiebreak
                                if (back_state.getSet_point_down(current_set) == 1) {
                                    pointDown.setText(String.valueOf(15));
                                } else if (back_state.getSet_point_down(current_set) == 2) {
                                    pointDown.setText(String.valueOf(30));
                                } else if (back_state.getSet_point_down(current_set) == 3) {
                                    pointDown.setText(String.valueOf(40));
                                } else if (back_state.getSet_point_down(current_set) == 4) {
                                    String msg = String.valueOf(40)+"A";
                                    pointDown.setText(msg);
                                } else {
                                    pointDown.setText("0");
                                }
                            } else {
                                pointDown.setText(String.valueOf(back_state.getSet_point_down(current_set)));
                            }

                            /*if (back_state.getSetsUp() > 0 || back_state.getSetsDown() > 0) {
                                setLayout.setVisibility(View.VISIBLE);
                                setUp.setText(String.valueOf(back_state.getSetsUp()));
                                setDown.setText(String.valueOf(back_state.getSetsDown()));
                            } else {
                                setLayout.setVisibility(View.GONE);
                                setUp.setText("0");
                                setDown.setText("0");
                            }*/

                            Log.d(TAG, "########## back state start ##########");
                            Log.d(TAG, "current set : " + back_state.getCurrent_set());
                            Log.d(TAG, "Serve : " + back_state.isServe());
                            Log.d(TAG, "In tiebreak : " + back_state.isInTiebreak());
                            Log.d(TAG, "Finish : " + back_state.isFinish());

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


                            for (int i = 1; i <= set_limit; i++) {
                                Log.d(TAG, "================================");
                                Log.d(TAG, "[set " + i + "]");
                                Log.d(TAG, "[Game : " + back_state.getSet_game_up((byte) i) + " / " + back_state.getSet_game_down((byte) i) + "]");
                                Log.d(TAG, "[Point : " + back_state.getSet_point_up((byte) i) + " / " + back_state.getSet_point_down((byte) i) + "]");
                                Log.d(TAG, "[tiebreak : " + back_state.getSet_tiebreak_point_up((byte) i) + " / " + back_state.getSet_tiebreak_point_down((byte) i) + "]");
                            }


                            Log.d(TAG, "########## back state end ##########");

                        } else {
                            //gameUp.setText("0");
                            //gameDown.setText("0");

                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);

                            pointUp.setText("0");
                            pointDown.setText("0");

                            if (serve.equals("0")) { //you server first
                                imgServeUp.setVisibility(View.INVISIBLE);
                                imgServeDown.setVisibility(View.VISIBLE);
                            } else {
                                imgServeUp.setVisibility(View.VISIBLE);
                                imgServeDown.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }
        });

        pointDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateScore(true);
                //String message = "you";
                //syncSendCommand(message);
                loadState();
            }
        });



        pointUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateScore(false);
                //String message = "oppt";
                //syncSendCommand(message);
                loadState();
            }
        });


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
        myData.mStepCounter = myData.mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        myData.mSensorManager.registerListener(sensorEventListener, myData.mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);

        TextView btnGame = (TextView) findViewById(R.id.textViewPointGames);
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PointActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });


        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_STEP_COUNT_ACTION)) {
                    Log.d(TAG, "receive brocast !");

                    stepCountPoint.setText(String.valueOf((int)(step_count_end - step_count_start)));


                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.GET_STEP_COUNT_ACTION);
            context.registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
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
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        myData.mGoogleApiClient.disconnect();

        time_use = 0;
        stack.clear();
        handler.removeCallbacks(updateTimer);
        myData.is_running = false;

        //unregister sensor step
        myData.mSensorManager.unregisterListener(sensorEventListener);
        sensorEventListener = null;

        voicePlay.doExit();
        voicePlay = null;

        if (isRegister && mReceiver != null) {
            try {
                context.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
        }

        super.onDestroy();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        Log.e(TAG, "onEnterAmbient");
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        Log.e(TAG, "onUpdateAmbient");
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        Log.e(TAG, "onExitAmbient");
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {

        /*
        layoutFBack = (LinearLayout) findViewById(R.id.layoutPointPFBack);
            layoutPointStepCount = (LinearLayout) findViewById(R.id.layoutPointStepCount);
         */

        if (isAmbient()) {

            pointContainer.setBackgroundColor(Color.BLACK);

            SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

            headOppt.setTextColor(Color.WHITE);
            headYou.setTextColor(Color.WHITE);

            pointUp.setTextColor(Color.WHITE);
            pointDown.setTextColor(Color.WHITE);

            layoutBtn.setVisibility(View.GONE);
            textViewTime.setVisibility(View.VISIBLE);

            textViewTime.setText(AMBIENT_DATE_FORMAT.format(new Date()));

            layoutFBack.setVisibility(View.GONE);
            layoutPointStepCount.setVisibility(View.VISIBLE);

        } else {
            pointContainer.setBackgroundColor(Color.WHITE);

            headOppt.setTextColor(Color.GRAY);
            headYou.setTextColor(Color.GRAY);

            pointUp.setTextColor(Color.GRAY);
            pointDown.setTextColor(Color.GRAY);

            textViewTime.setVisibility(View.GONE);
            layoutBtn.setVisibility(View.VISIBLE);

            //mContainerView.setBackground(null);
            //mTextView.setTextColor(getResources().getColor(android.R.color.black));
           // mClockView.setVisibility(View.GONE);
            layoutFBack.setVisibility(View.VISIBLE);
            layoutPointStepCount.setVisibility(View.GONE);
        }
    }

    public void calculateScore(boolean you_score) {
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

    private void checkPoint(State new_state) {
        Log.d(TAG, "[Check point Start]");

        byte current_set = new_state.getCurrent_set();
        if (new_state.isInTiebreak()) { //in tiebreak
            Log.d(TAG, "[In Tiebreak]");
            byte game;

            if (games.equals("0")) { //6 game in a set
                Log.d(TAG, "[6 games in a set]"); //6:6 => tiebreak

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
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                } else {
                    Log.d(TAG, "Other tie break, isServe = "+new_state.isServe());

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

                    is_current_game_over = false;

                    if (new_state.getSet_point_up(current_set) > 99 ||
                            new_state.getSet_point_down(current_set) > 99) { //point > 99, don't play voice
                        toast("The voice will not support while points more than 99");
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                    } else {

                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);
                    }
                }
            } else {
                Log.d(TAG, "[4 games in a set]"); //4:4 => tiebreak

                if (new_state.getSet_point_up(current_set) == 5 && new_state.getSet_point_down(current_set) <= 3) {
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
                    //if (new_state.isServe()) {
                    //if (am_I_Tiebreak_First_Serve) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) <= 3 && new_state.getSet_point_down(current_set) == 5) {
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
                    //if (new_state.isServe()) {
                    //if (am_I_Tiebreak_First_Serve) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) >= 4 &&
                        new_state.getSet_point_down(current_set) >= 4 &&
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
                    //if (new_state.isServe()) {
                    //if (am_I_Tiebreak_First_Serve) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else if (new_state.getSet_point_up(current_set) >= 4 &&
                        new_state.getSet_point_down(current_set) >= 4 &&
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
                    //if (new_state.isServe()) {
                    //if (am_I_Tiebreak_First_Serve) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                    is_current_game_over = true;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                    //voiceList.add(call);
                } else {
                    Log.d(TAG, "Other tie break, isServe = "+new_state.isServe());



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

                    is_current_game_over = false;

                    if (new_state.getSet_point_up(current_set) > 99 ||
                            new_state.getSet_point_down(current_set) > 99) { //point > 99, don't play voice
                        toast("The voice will not support while points more than 99");
                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                    } else {

                        //do stop play
                        voicePlay.doStopAudioPlayMulti();
                        //add voice
                        voiceList.clear();
                        voiceUserList.clear();
                        choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                        //voiceList.add(call);
                    }
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

                    is_current_game_over = false;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));

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

                    is_current_game_over = true;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));

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

                    is_current_game_over = true;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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

                    is_current_game_over = true;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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

                    is_current_game_over = true;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                }
                else {
                    Log.d(TAG, "[points change without arrange]");
                    is_current_game_over = false;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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

                    is_current_game_over = true;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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

                    is_current_game_over = true;

                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
                } else {
                    Log.d(TAG, "[points change without arrange]");
                    is_current_game_over = false;
                    //do stop play
                    voicePlay.doStopAudioPlayMulti();
                    //add voice
                    voiceList.clear();
                    voiceUserList.clear();
                    choosePointVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_point_up(current_set), new_state.getSet_point_down(current_set));
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

            if (games.equals("0")) { //6 game in a set
                Log.d(TAG, "[6 game in a set start]");

                if (new_state.getSet_game_up(current_set) == 6 &&
                        new_state.getSet_game_down(current_set) == 6) {
                    new_state.setInTiebreak(true); //into tiebreak;

                    //am I(down) first serve?
                    if (new_state.isServe()) {
                        am_I_Tiebreak_First_Serve = true;
                    } else {
                        am_I_Tiebreak_First_Serve = false;
                    }

                    //add voice

                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 7 &&
                        new_state.getSet_game_down(current_set) == 5) { // 7:5 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 7 &&
                        new_state.getSet_game_down(current_set) == 6) { // 7:6 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 6 &&
                        new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 6 &&
                        new_state.getSet_game_down(current_set) <=4 ) { // 6:0,1,2,3,4 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) <= 4 &&
                        new_state.getSet_game_down(current_set) == 6) { // 0,1,2,3,4:6 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else {
                    Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));
                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                }
            } else {
                Log.d(TAG, "[4 game in a set start]");

                if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) == 4) {
                    new_state.setInTiebreak(true); //into tiebreak;

                    //am I(down) first serve?
                    if (new_state.isServe()) {
                        am_I_Tiebreak_First_Serve = true;
                    } else {
                        am_I_Tiebreak_First_Serve = false;
                    }

                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 3) { // 5:3 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 3 &&
                        new_state.getSet_game_down(current_set) == 5) { // 3:5 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 4) { // 5:4 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) == 5) { // 4:5 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) <=2 ) { // 4:0,1,2 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else if (new_state.getSet_game_up(current_set) <= 2 &&
                        new_state.getSet_game_down(current_set) == 4) { // 0,1,2:6 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                } else {
                    Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));
                    //add voice
                    if(is_current_game_over)
                        chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    //play voice
                    if (voiceOn) {
                        voicePlay.audioPlayMulti(voiceList);
                    }
                }
                Log.d(TAG, "[4 game in a set end]");
            }


        } else {
            Log.d(TAG, "[Use deciding point start]");
            if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) <= 5) { // 6:5 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
                //play voice
                if (voiceOn) {
                    voicePlay.audioPlayMulti(voiceList);
                }
            } else if (new_state.getSet_game_up(current_set) <= 5 &&
                    new_state.getSet_game_down(current_set) == 6) { // 5:6 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
                //play voice
                if (voiceOn) {
                    voicePlay.audioPlayMulti(voiceList);
                }
            } else {
                Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));
                //add voice
                if(is_current_game_over)
                    chooseGameVoice(new_state.isServe(), new_state.isInTiebreak(), new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                //play voice
                if (voiceOn) {
                    voicePlay.audioPlayMulti(voiceList);
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
        String fileName;

        switch (set) {
            case "0": //only one set
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    //voice
                    switch (current_voice_type) {
                        case GBR_MAN:
                            match = R.raw.gbr_man_match;
                            voiceList.add(match);
                            break;
                        case USER_RECORD:
                            fileName = "user_match.m4a";
                            voiceUserList.add(fileName);
                            break;

                    }

                    //voiceList.add(match);
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }

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
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
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
                        chooseSetVoice(new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    } else {
                        chooseSetVoice(new_state.getSet_game_down(current_set), new_state.getSet_game_up(current_set));
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
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
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
                        chooseSetVoice(new_state.getSet_game_up(current_set), new_state.getSet_game_down(current_set));
                    } else {
                        chooseSetVoice(new_state.getSet_game_down(current_set), new_state.getSet_game_up(current_set));
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
                    switch (current_voice_type) {
                        case GBR_MAN:
                            match = R.raw.gbr_man_match;
                            voiceList.add(match);
                            break;
                        case USER_RECORD:
                            fileName = "user_match.m4a";
                            voiceUserList.add(fileName);
                            break;

                    }

                    //voiceList.add(match);
                    //match and play all
                    for (int i =1; i<=current_set; i++) {
                        if (setsWinUp > setsWinDown) {
                            chooseSetVoice(new_state.getSet_game_up((byte) i), new_state.getSet_game_down((byte) i));
                        } else {
                            chooseSetVoice(new_state.getSet_game_down((byte) i), new_state.getSet_game_up((byte) i));
                        }
                    }

                    is_finish = true; //for sync
                    endTime = System.currentTimeMillis();
                } else {
                    is_finish = false;
                }

                break;
        }

        Log.d(TAG, "[Check sets End]");
    }

    private static int choosePointVoice(boolean down_serve, boolean is_tiebreak, byte up_point, byte down_point) {
        int call = 0;
        int call2 = 0;
        int call3 = 0;
        int call4 = 0;
        int call5 = 0;

        String fileName0;
        String fileName1;
        String fileName2;
        String fileName3;
        String fileName4;


        Log.d(TAG, "<choosePointVoice start>");

        if (is_current_game_over) { //current game over
            Log.d(TAG, "current game is over.");
            switch (current_voice_type) {
                case GBR_MAN:
                    call = R.raw.gbr_man_game;
                    voiceList.add(call);
                    break;
                case USER_RECORD:
                    fileName0 = "user_game.m4a";
                    voiceUserList.add(fileName0);
                    break;
            }

        } else { //still in game

            if (!is_tiebreak) { //not in tiebreak

                if (up_point == 0 && down_point == 1) { //0:15
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 0 && down_point == 2) { //0:30
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 0 && down_point == 3) { //0:40
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 1 && down_point == 0) { //15:0
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_0.m4a";
                                voiceUserList.add(fileName0);
                        }
                    }
                } else if (up_point == 1 && down_point == 1) { //15:15
                    switch (current_voice_type) {
                        case GBR_MAN:
                            call = R.raw.gbr_man_15_15;
                            voiceList.add(call);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_15_15.m4a";
                            voiceUserList.add(fileName0);
                            break;
                    }
                } else if (up_point == 1 && down_point == 2) { //15:30
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 1 && down_point == 3) { //15:40
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 2 && down_point == 0) { //30:0
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 2 && down_point == 1) { //30:15
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 2 && down_point == 2) { //30:30
                    switch (current_voice_type) {
                        case GBR_MAN:
                            call = R.raw.gbr_man_30_30;
                            voiceList.add(call);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_30_30.m4a";
                            voiceUserList.add(fileName0);
                            break;
                    }
                } else if (up_point == 2 && down_point == 3) { //30:40
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 0) { //40:0
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_0_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_0_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_0;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_0.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 1) { //40:15
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_15_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_15_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_15;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_15.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 2) { //40:30
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_30_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_30_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_30;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_30.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    }
                } else if (up_point == 3 && down_point == 3) { //40:40

                    if (deuce.equals("0")) { //use deuce
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_40_40;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_40_40.m4a";
                                voiceUserList.add(fileName0);
                                break;
                        }
                    } else { //use deciding point
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_deciding_point;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_deciding_point.m4a";
                                voiceUserList.add(fileName0);
                                break;

                        }
                    }


                } else if (up_point == 3 && down_point == 4) { //40:Ad
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_serve;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_down.m4a";
                                voiceUserList.add(fileName1);
                                break;

                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_recv;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_down.m4a";
                                voiceUserList.add(fileName1);
                                break;
                        }
                    }


                } else if (up_point == 4 && down_point == 3) { //Ad:40

                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_recv;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_up.m4a";
                                voiceUserList.add(fileName1);
                                break;
                        }
                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = R.raw.gbr_man_ad_serve;
                                voiceList.add(call);
                                break;
                            case USER_RECORD:
                                fileName0 = "user_ad.m4a";
                                voiceUserList.add(fileName0);
                                fileName1 = "user_player_up.m4a";
                                voiceUserList.add(fileName1);
                                break;
                        }
                    }


                }

                //voiceList.add(call);
            } else { // in tiebreak
                Log.e(TAG, "voice choose in tiebreak==>");

                if (up_point == 0 && down_point <= 6) { //0:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(down_point);
                                voiceList.add(call);

                                call2 = getPointByNum((byte)0); //0
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                fileName1 = "user_to.m4a";
                                voiceUserList.add(fileName1);

                                fileName2 = getPointByNumString((byte)0);
                                voiceUserList.add(fileName2);
                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum((byte)0); //0
                                voiceList.add(call);

                                call2 = getPointByNum(down_point);
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString((byte)0); //0
                                voiceUserList.add(fileName0);

                                fileName1 = "user_to.m4a";
                                voiceUserList.add(fileName1);

                                fileName2 = getPointByNumString(down_point);
                                voiceUserList.add(fileName2);
                                break;
                        }

                    }
                } else if (up_point == 1 && down_point <= 6) { //1:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(down_point);
                                voiceList.add(call);

                                if (down_point == 1)
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(up_point); //1
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 1) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2= getPointByNumString(up_point); //1
                                    voiceUserList.add(fileName2);
                                }


                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(up_point); //1
                                voiceList.add(call);

                                if (down_point == 1)  //1:1
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(down_point);

                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //1
                                voiceUserList.add(fileName0);

                                if (down_point == 1) { //1:1
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }


                    }
                } else if (up_point == 2 && down_point <= 6) { //2:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(down_point);
                                voiceList.add(call);

                                if (down_point == 2)
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(up_point); //2
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 2) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //2
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(up_point); //2
                                voiceList.add(call);

                                if (down_point == 2)  //2:2
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(down_point);

                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //2
                                voiceUserList.add(fileName0);

                                if (down_point == 2) {  //2:2
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 3 && down_point <= 6) { //3:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(down_point);
                                voiceList.add(call);

                                if (down_point == 3)
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(up_point); //3
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 3) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //3
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(up_point); //3
                                voiceList.add(call);

                                if (down_point == 3) { //3:3
                                    call2 = getPointByNum((byte)100); //all
                                } else {
                                    call2 = getPointByNum(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //3
                                voiceUserList.add(fileName0);

                                if (down_point == 3) { //3:3
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 4 && down_point <= 6) { //4:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(down_point);
                                voiceList.add(call);

                                if (down_point == 4)
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(up_point); //4
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 4) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //4
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(up_point); //4
                                voiceList.add(call);

                                if (down_point == 4) { //4:4
                                    call2 = getPointByNum((byte)100); //all
                                } else {
                                    call2 = getPointByNum(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //4
                                voiceUserList.add(fileName0);

                                if (down_point == 4) { //4:4
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 5 && down_point <= 6) { //5:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(down_point);
                                voiceList.add(call);

                                if (down_point == 5)
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(up_point); //5
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 5) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //5
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(up_point); //5
                                voiceList.add(call);

                                if (down_point == 5) { //5:5
                                    call2 = getPointByNum((byte)100); //all
                                } else {
                                    call2 = getPointByNum(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //5
                                voiceUserList.add(fileName0);

                                if (down_point == 5) { //5:5
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }

                    }
                } else if (up_point == 6 && down_point <= 6) { //6:1,2,3,4,5,6
                    if (down_serve) { //you serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(down_point);
                                voiceList.add(call);

                                if (down_point == 6)
                                    call2 = getPointByNum((byte)100); //all
                                else
                                    call2 = getPointByNum(up_point); //6
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(down_point);
                                voiceUserList.add(fileName0);

                                if (down_point == 6) {
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2 = getPointByNumString(up_point); //6
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }


                    } else { //oppt serve
                        switch (current_voice_type) {
                            case GBR_MAN:
                                call = getPointByNum(up_point); //6
                                voiceList.add(call);

                                if (down_point == 6) { //6:6
                                    call2 = getPointByNum((byte)100); //all
                                } else {
                                    call2 = getPointByNum(down_point);
                                }
                                voiceList.add(call2);
                                break;
                            case USER_RECORD:
                                fileName0 = getPointByNumString(up_point); //6
                                voiceUserList.add(fileName0);

                                if (down_point == 6) { //6:6
                                    fileName1 = "user_all.m4a"; //all
                                    voiceUserList.add(fileName1);
                                } else {
                                    fileName1 = "user_to.m4a";
                                    voiceUserList.add(fileName1);

                                    fileName2= getPointByNumString(down_point);
                                    voiceUserList.add(fileName2);
                                }

                                break;
                        }


                    }
                } else { //point more than 6
                    Log.e(TAG, "up_point = "+up_point+ ", down_point = "+down_point);

                    if (up_point == down_point) { // x all

                        if (up_point <= 20) {
                            switch (current_voice_type) {
                                case GBR_MAN:
                                    call = getPointByNum(up_point);
                                    voiceList.add(call);
                                    call2 = getPointByNum((byte)100);
                                    voiceList.add(call2);
                                    break;
                                case USER_RECORD:
                                    fileName0 = getPointByNumString(up_point);
                                    voiceUserList.add(fileName0);
                                    fileName1 = "user_all.m4a";
                                    voiceUserList.add(fileName1);
                                    break;
                            }


                        } else { //up_point > 20
                            switch (current_voice_type) {
                                case GBR_MAN:
                                    if (up_point % 10 == 0) { //30, 40, 50, 60, 70, 80, 90
                                        call = getPointByNum(up_point);
                                        voiceList.add(call);
                                    } else {
                                        call = getPointByNum((byte)(up_point/10*10));
                                        voiceList.add(call);
                                        call2 = getPointByNum((byte)(up_point%10));
                                        voiceList.add(call2);
                                    }
                                    call3 = getPointByNum((byte)100);
                                    voiceList.add(call3);
                                    break;
                                case USER_RECORD:
                                    if (up_point % 10 == 0) { //30, 40, 50, 60, 70, 80, 90
                                        fileName0 = getPointByNumString(up_point);
                                        voiceUserList.add(fileName0);
                                    } else {
                                        fileName1 = getPointByNumString((byte)(up_point/10*10));
                                        voiceUserList.add(fileName1);
                                        fileName2 = getPointByNumString((byte)(up_point%10));
                                        voiceUserList.add(fileName2);
                                    }
                                    fileName3 = "user_all.m4a";
                                    voiceUserList.add(fileName3);
                                    break;
                            }
                        }
                    } else {
                        if (up_point <= 20 && down_point <= 20) {
                            if (down_serve) { //you serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                        call = getPointByNum(down_point);
                                        voiceList.add(call);
                                        call2 = getPointByNum(up_point);
                                        voiceList.add(call2);
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString(down_point);
                                        voiceUserList.add(fileName0);
                                        fileName1 = "user_to.m4a";
                                        voiceUserList.add(fileName1);
                                        fileName2 = getPointByNumString(up_point);
                                        voiceUserList.add(fileName2);
                                        break;
                                }

                            } else { //oppt serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                        call = getPointByNum(up_point);
                                        voiceList.add(call);
                                        call2 = getPointByNum(down_point);
                                        voiceList.add(call2);
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString(up_point);
                                        voiceUserList.add(fileName0);
                                        fileName1 = "user_to.m4a";
                                        voiceUserList.add(fileName1);
                                        fileName2 = getPointByNumString(down_point);
                                        voiceUserList.add(fileName2);
                                        break;
                                }

                            }
                        } else { //up_point > 20
                            if (down_serve) { //you serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                        call = getPointByNum((byte)(down_point/10*10));
                                        voiceList.add(call);
                                        if (down_point%10 > 0) {
                                            call2 = getPointByNum((byte) (down_point % 10));
                                            voiceList.add(call2);
                                        }

                                        call3 = getPointByNum((byte)(up_point/10*10));
                                        voiceList.add(call3);
                                        if (up_point%10 > 0) {
                                            call4 = getPointByNum((byte) (up_point % 10));
                                            voiceList.add(call4);
                                        }
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString((byte)(down_point/10*10));
                                        voiceUserList.add(fileName0);
                                        if (down_point%10 > 0) {
                                            fileName1 = getPointByNumString((byte) (down_point % 10));
                                            voiceUserList.add(fileName1);
                                        }
                                        //to
                                        fileName4 = "user_to.m4a";
                                        voiceUserList.add(fileName4);

                                        fileName2 = getPointByNumString((byte)(up_point/10*10));
                                        voiceUserList.add(fileName2);
                                        if (up_point%10 > 0) {
                                            fileName3 = getPointByNumString((byte) (up_point % 10));
                                            voiceUserList.add(fileName3);
                                        }
                                        break;
                                }

                            } else { //oppt serve
                                switch (current_voice_type) {
                                    case GBR_MAN:
                                        call = getPointByNum((byte)(up_point/10*10));
                                        voiceList.add(call);
                                        if (up_point%10 > 0) {
                                            call2 = getPointByNum((byte) (up_point % 10));
                                            voiceList.add(call2);
                                        }

                                        call3 = getPointByNum((byte)(down_point/10*10));
                                        voiceList.add(call3);
                                        if (down_point%10 > 0) {
                                            call4 = getPointByNum((byte) (down_point % 10));
                                            voiceList.add(call4);
                                        }
                                        break;
                                    case USER_RECORD:
                                        fileName0 = getPointByNumString((byte)(up_point/10*10));
                                        voiceUserList.add(fileName0);
                                        if (up_point%10 > 0) {
                                            fileName1 = getPointByNumString((byte) (up_point % 10));
                                            voiceUserList.add(fileName1);
                                        }

                                        //to
                                        fileName4 = "user_to.m4a";
                                        voiceUserList.add(fileName4);

                                        fileName2 = getPointByNumString((byte)(down_point/10*10));
                                        voiceUserList.add(fileName2);
                                        if (down_point%10 > 0) {
                                            fileName3 = getPointByNumString((byte) (down_point % 10));
                                            voiceUserList.add(fileName3);
                                        }
                                        break;
                                }

                            }
                        }
                    }
                }

                Log.e(TAG, "<== voice choose in tiebreak");
            }

        }

        Log.d(TAG, "<choosePointVoice end>");

        return call;
    }



    private static int getPointByNum(byte num) {
        int call = 0;

        switch (num) {
            case 0:
                call = R.raw.gbr_man_love;
                break;
            case 1:
                call = R.raw.gbr_man_1;
                break;
            case 2:
                call = R.raw.gbr_man_2;
                break;
            case 3:
                call = R.raw.gbr_man_3;
                break;
            case 4:
                call = R.raw.gbr_man_4;
                break;
            case 5:
                call = R.raw.gbr_man_5;
                break;
            case 6:
                call = R.raw.gbr_man_6;
                break;
            case 7:
                call = R.raw.gbr_man_7;
                break;
            case 8:
                call = R.raw.gbr_man_8;
                break;
            case 9:
                call = R.raw.gbr_man_9;
                break;
            case 10:
                call = R.raw.gbr_man_10;
                break;
            case 11:
                call = R.raw.gbr_man_11;
                break;
            case 12:
                call = R.raw.gbr_man_12;
                break;
            case 13:
                call = R.raw.gbr_man_13;
                break;
            case 14:
                call = R.raw.gbr_man_14;
                break;
            case 15:
                call = R.raw.gbr_man_15;
                break;
            case 16:
                call = R.raw.gbr_man_16;
                break;
            case 17:
                call = R.raw.gbr_man_17;
                break;
            case 18:
                call = R.raw.gbr_man_18;
                break;
            case 19:
                call = R.raw.gbr_man_19;
                break;
            case 20:
                call = R.raw.gbr_man_20;
                break;
            case 30:
                call = R.raw.gbr_man_30;
                break;
            case 40:
                call = R.raw.gbr_man_40;
                break;
            case 50:
                call = R.raw.gbr_man_50;
                break;
            case 60:
                call = R.raw.gbr_man_60;
                break;
            case 70:
                call = R.raw.gbr_man_70;
                break;
            case 80:
                call = R.raw.gbr_man_80;
                break;
            case 90:
                call = R.raw.gbr_man_90;
                break;
            case 100:
                call = R.raw.gbr_man_all;
                break;
        }

        return call;
    }

    private static String getPointByNumString(byte num) {
        String call = "";

        switch (num) {
            case 0:
                call = "user_love.m4a";
                break;
            case 1:
                call = "user_1.m4a";
                break;
            case 2:
                call = "user_2.m4a";
                break;
            case 3:
                call = "user_3.m4a";
                break;
            case 4:
                call = "user_4.m4a";
                break;
            case 5:
                call = "user_5.m4a";
                break;
            case 6:
                call = "user_6.m4a";
                break;
            case 7:
                call = "user_7.m4a";
                break;
            case 8:
                call = "user_8.m4a";
                break;
            case 9:
                call = "user_9.m4a";
                break;
            case 10:
                call = "user_10.m4a";
                break;
            case 11:
                call = "user_11.m4a";
                break;
            case 12:
                call = "user_12.m4a";
                break;
            case 13:
                call = "user_13.m4a";
                break;
            case 14:
                call = "user_14.m4a";
                break;
            case 15:
                call = "user_15.m4a";
                break;
            case 16:
                call = "user_16.m4a";
                break;
            case 17:
                call = "user_17.m4a";
                break;
            case 18:
                call = "user_18.m4a";
                break;
            case 19:
                call = "user_19.m4a";
                break;
            case 20:
                call = "user_20.m4a";
                break;
            case 30:
                call = "user_30.m4a";
                break;
            case 40:
                call = "user_40.m4a";
                break;
            case 50:
                call = "user_50.m4a";
                break;
            case 60:
                call = "user_60.m4a";
                break;
            case 70:
                call = "user_70.m4a";
                break;
            case 80:
                call = "user_80.m4a";
                break;
            case 90:
                call = "user_90.m4a";
                break;
            case 100:
                call = "user_all.m4a";
                break;
        }

        return call;
    }

    private static void chooseGameVoice(boolean down_serve, boolean is_tiebreak, byte gameUp,  byte gameDown) {
        Integer gameCall, gameCall2, gameCall3;
        String fileName0, fileName1, fileName2, fileName3;
        Log.d(TAG, "[chooseGameVoice start]");
        if (is_tiebreak) { //enter tiebreak
            Log.d(TAG, "in tiebreak");
            /*if (games.equals("0")) { //6 game in a set
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_6;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        gameCall3 = R.raw.gbr_man_tiebreak;
                        voiceList.add(gameCall3);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_6.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        fileName2 = "user_tiebreak.m4a";
                        voiceUserList.add(fileName2);
                        break;
                }
            } else { //4 game in a set
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_4;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        gameCall3 = R.raw.gbr_man_tiebreak;
                        voiceList.add(gameCall3);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_4.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        fileName2 = "user_tiebreak.m4a";
                        voiceUserList.add(fileName2);
                        break;
                }
            }*/
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    gameCall3 = R.raw.gbr_man_tiebreak;
                    voiceList.add(gameCall3);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_games_all.m4a";
                    voiceUserList.add(fileName1);
                    fileName2 = "user_tiebreak.m4a";
                    voiceUserList.add(fileName2);
                    break;
            }


        } else {
            Log.d(TAG, "Not in tiebreak, gameUp = "+gameUp+" : gameDown = "+gameDown);

            if (gameUp == 0 && gameDown == 1) { //0:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 2) { //0:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 3) { //0:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 4) { //0:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;

                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 5) { //0:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 0 && gameDown == 6) { //0:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 0) { //1:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 1) { //1:1
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_1;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_1.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_game_all.m4a";
                        voiceUserList.add(fileName1);
                        fileName2 = "user_all.m4a";
                        voiceUserList.add(fileName2);
                        break;
                }
            } else if (gameUp == 1 && gameDown == 2) { //1:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 3) { //1:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 4) { //1:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 5) { //1:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 1 && gameDown == 6) { //1:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 0) { //2:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 1) { //2:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 2) { //2:2
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_2;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_2.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 2 && gameDown == 3) { //2:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 4) { //2:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 5) { //2:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 2 && gameDown == 6) { //2:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 0) { //3:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 1) { //3:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 2) { //3:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 3) { //3:3
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_3;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_3.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 3 && gameDown == 4) { //3:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 5) { //3:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 3 && gameDown == 6) { //3:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 0) { //4:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 1) { //4:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;

                    }

                }
            } else if (gameUp == 4 && gameDown == 2) { //4:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 3) { //4:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 4) { //4:4
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_4;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_4.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;

                }
            } else if (gameUp == 4 && gameDown == 5) { //4:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;

                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 4 && gameDown == 6) { //4:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 0) { //5:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 1) { //5:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 2) { //5:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 3) { //5:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 4) { //5:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 5) { //5:5
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_5;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_5.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 5 && gameDown == 6) { //5:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 5 && gameDown == 7) { //5:7
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_7.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 0) { //6:0
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_love;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_love.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }


                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_love;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_love.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 1) { //6:1
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_1;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_1.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_game_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_1;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_1.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 2) { //6:2
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_2;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_2.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_2;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_2.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 3) { //6:3
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_3;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_3.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_3;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_3.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 4) { //6:4
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_4;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_4.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_4;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_4.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 5) { //6:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 6 && gameDown == 6) { //6:6
                switch (current_voice_type) {
                    case GBR_MAN:
                        gameCall = R.raw.gbr_man_6;
                        voiceList.add(gameCall);
                        gameCall2 = R.raw.gbr_man_all;
                        voiceList.add(gameCall2);
                        break;
                    case USER_RECORD:
                        fileName0 = "user_6.m4a";
                        voiceUserList.add(fileName0);
                        fileName1 = "user_games_all.m4a";
                        voiceUserList.add(fileName1);
                        break;
                }
            } else if (gameUp == 6 && gameDown == 7) { //6:7
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 7 && gameDown == 5) { //7:5
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_5;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_5.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_7.m4a";
                            voiceUserList.add(fileName2);
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_5;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_5.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                }
            } else if (gameUp == 7 && gameDown == 6) { //7:6
                if (down_serve) { //down serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_6;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_7;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_6.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_7.m4a";
                            voiceUserList.add(fileName2);
                            break;
                    }

                } else { //oppt serve
                    switch (current_voice_type) {
                        case GBR_MAN:
                            gameCall = R.raw.gbr_man_7;
                            voiceList.add(gameCall);
                            gameCall2 = R.raw.gbr_man_6;
                            voiceList.add(gameCall2);
                            break;
                        case USER_RECORD:
                            fileName0 = "user_7.m4a";
                            voiceUserList.add(fileName0);
                            fileName1 = "user_games_to.m4a";
                            voiceUserList.add(fileName1);
                            fileName2 = "user_6.m4a";
                            voiceUserList.add(fileName2);
                    }

                }
            } else {
                Log.e(TAG, "unknown to choose voice");
            }
        }



        Log.d(TAG, "[chooseGameVoice end]");
    }

    private static void chooseSetVoice(byte gameServe,  byte gameRecv) {
        Integer gameCall, gameCall2;
        String fileName0, fileName1;
        if (gameServe == 0 && gameRecv == 1) {

            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 0 && gameRecv == 2) {

            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 0 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 0 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 0 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 0 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_love;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_love.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }
        } else if (gameServe == 1 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 1 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_1;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_1.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }


        } else if (gameServe == 2 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2e.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }


        } else if (gameServe == 2 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 2 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_2;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_2.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 3 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_3;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_3.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 4 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_4;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_4.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 5 && gameRecv == 7) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_5;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_7;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_5.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_7.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 0) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_love;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_love.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 1) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_1;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_1.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 2) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_2;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_2.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 3) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_3;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_3.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 4) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_4;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_4.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_all;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_all.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 6 && gameRecv == 7) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_6;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_7;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_6.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_7.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 7 && gameRecv == 5) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_7;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_5;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_7.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_5.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        } else if (gameServe == 7 && gameRecv == 6) {
            switch (current_voice_type) {
                case GBR_MAN:
                    gameCall = R.raw.gbr_man_7;
                    voiceList.add(gameCall);
                    gameCall2 = R.raw.gbr_man_6;
                    voiceList.add(gameCall2);
                    break;
                case USER_RECORD:
                    fileName0 = "user_7.m4a";
                    voiceUserList.add(fileName0);
                    fileName1 = "user_6.m4a";
                    voiceUserList.add(fileName1);
                    break;
            }

        }
    }

    private void loadState() {
        if (stack.isEmpty()) {
            Log.d(TAG, "stack is empty!");

            imgServeUp.setVisibility(View.INVISIBLE);
            imgServeDown.setVisibility(View.INVISIBLE);

            pointUp.setText("0");
            pointDown.setText("0");

            if (serve.equals("0")) { //you server first
                imgServeUp.setVisibility(View.INVISIBLE);
                imgServeDown.setVisibility(View.VISIBLE);
            } else {
                imgServeUp.setVisibility(View.VISIBLE);
                imgServeDown.setVisibility(View.INVISIBLE);
            }
        } else {


            byte current_set;

            State current_state = stack.peek();
            current_set = current_state.getCurrent_set();

            if (current_state.isFinish()) {
                toast(getResources().getString(R.string.point_game_is_finish));
            }

            if (current_state.isServe()) {
                imgServeUp.setVisibility(View.INVISIBLE);
                imgServeDown.setVisibility(View.VISIBLE);
            } else {
                imgServeUp.setVisibility(View.VISIBLE);
                imgServeDown.setVisibility(View.INVISIBLE);
            }

            if (!current_state.isInTiebreak()) { //not in tiebreak
                if (current_state.getSet_point_up(current_set) == 1) {
                    pointUp.setText(String.valueOf(15));
                } else if (current_state.getSet_point_up(current_set) == 2) {
                    pointUp.setText(String.valueOf(30));
                } else if (current_state.getSet_point_up(current_set) == 3) {
                    pointUp.setText(String.valueOf(40));
                } else if (current_state.getSet_point_up(current_set) == 4) {
                    String msg = "Ad";
                    pointUp.setText(msg);
                } else {
                    pointUp.setText("0");
                }
            } else { //tie break;
                pointUp.setText(String.valueOf(current_state.getSet_point_up(current_set)));
            }

            if (!current_state.isInTiebreak()) { //not in tiebreak
                if (current_state.getSet_point_down(current_set) == 1) {
                    pointDown.setText(String.valueOf(15));
                } else if (current_state.getSet_point_down(current_set) == 2) {
                    pointDown.setText(String.valueOf(30));
                } else if (current_state.getSet_point_down(current_set) == 3) {
                    pointDown.setText(String.valueOf(40));
                } else if (current_state.getSet_point_down(current_set) == 4) {
                    String msg = "Ad";
                    pointDown.setText(msg);
                } else {
                    pointDown.setText("0");
                }
            } else {
                pointDown.setText(String.valueOf(current_state.getSet_point_down(current_set)));
            }

                    /*if (back_state.getSetsUp() > 0 || back_state.getSetsDown() > 0) {
                        setLayout.setVisibility(View.VISIBLE);
                        setUp.setText(String.valueOf(back_state.getSetsUp()));
                        setDown.setText(String.valueOf(back_state.getSetsDown()));
                    } else {
                        setLayout.setVisibility(View.GONE);
                        setUp.setText("0");
                        setDown.setText("0");
                    }*/

            Log.d(TAG, "########## current state start ##########");
            Log.d(TAG, "current set : " + current_state.getCurrent_set());
            Log.d(TAG, "Serve : " + current_state.isServe());
            Log.d(TAG, "In tiebreak : " + current_state.isInTiebreak());
            Log.d(TAG, "Finish : " + current_state.isFinish());

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


            for (int i = 1; i <= set_limit; i++) {
                Log.d(TAG, "================================");
                Log.d(TAG, "[set " + i + "]");
                Log.d(TAG, "[Game : " + current_state.getSet_game_up((byte) i) + " / " + current_state.getSet_game_down((byte) i) + "]");
                Log.d(TAG, "[Point : " + current_state.getSet_point_up((byte) i) + " / " + current_state.getSet_point_down((byte) i) + "]");
                Log.d(TAG, "[tiebreak : " + current_state.getSet_tiebreak_point_up((byte) i) + " / " + current_state.getSet_tiebreak_point_down((byte) i) + "]");
            }


            Log.d(TAG, "########## back state end ##########");
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
                message = "calibrate&"+step_count+"&"+set+"&"+games+"&"+tiebreak+"&"+deuce+"&"+serve+"&"+startTime+"&"+endTime;
            } else {
                message = "calibrate&"+step_count+"&"+set+"&"+games+"&"+tiebreak+"&"+deuce+"&"+serve+"&"+startTime+"&0";
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

    public static void syncSendCommand(String cmd) {
        if (myData.is_debug)
            Log.d(TAG, "syncSendCommand start = "+step_count_start+" end = "+step_count_end);



        if(myData.mGoogleApiClient==null) {
            Log.e(TAG, "mGoogleApiClient = null");
        } else {
            if (myData.is_debug)
                Log.d(TAG, "mGoogleApiClient = " + myData.mGoogleApiClient.isConnected() + " cmd = " + cmd);
            long current_message_length = cmd.length();
            if (current_message_length != previous_message_length) {

                PutDataMapRequest putRequest = PutDataMapRequest.create("/WEAR_COMMAND");
                DataMap map = putRequest.getDataMap();
                //map.putInt("color", Color.RED);
                map.putString("cmd", cmd);
                map.putLong("count", count);
                count++;
                Wearable.DataApi.putDataItem(myData.mGoogleApiClient, putRequest.asPutDataRequest());

                previous_message_length = current_message_length;

            } else {
                if (is_step_count_change) {

                    PutDataMapRequest putRequest = PutDataMapRequest.create("/WEAR_COMMAND");
                    DataMap map = putRequest.getDataMap();
                    //map.putInt("color", Color.RED);
                    map.putString("cmd", cmd);
                    map.putLong("count", count);
                    count++;
                    Wearable.DataApi.putDataItem(myData.mGoogleApiClient, putRequest.asPutDataRequest());

                    previous_message_length = current_message_length;

                    is_step_count_change = false;
                } else {
                    if (myData.is_debug)
                        Log.d(TAG, "string length is the same, won't send this message");
                }
            }
        }
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
