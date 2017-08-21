package com.seventhmoon.tenniswearumpire;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seventhmoon.tenniswearumpire.Data.Constants;
import com.seventhmoon.tenniswearumpire.Data.State;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.seventhmoon.tenniswearumpire.PointActivity.set;
import static com.seventhmoon.tenniswearumpire.PointActivity.stack;
import static com.seventhmoon.tenniswearumpire.PointActivity.step_count_end;
import static com.seventhmoon.tenniswearumpire.PointActivity.step_count_start;


public class GameActivity extends WearableActivity {
    private static final String TAG = GameActivity.class.getName();

    private Context context;

    private BoxInsetLayout gameContainer;

    LinearLayout layoutBtn;

    TextView headOppt;
    TextView headYou;


    ImageView imgWinCheckUp;
    ImageView imgWinCheckDown;

    TextView textViewSet1Up;
    TextView textViewSet1Down;
    TextView textViewSet2Up;
    TextView textViewSet2Down;
    TextView textViewSet3Up;
    TextView textViewSet3Down;
    TextView textViewSet4Up;
    TextView textViewSet4Down;
    TextView textViewSet5Up;
    TextView textViewSet5Down;

    ImageView imgStepCount;
    TextView stepCounter;
    TextView textViewTime;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.game_activity);

        setAmbientEnabled();

        context = getBaseContext();

        gameContainer = (BoxInsetLayout) findViewById(R.id.gameContainer);

        imgStepCount = (ImageView) findViewById(R.id.imgStepCount);

        stepCounter = (TextView) findViewById(R.id.stepCountGames);

        layoutBtn = (LinearLayout) findViewById(R.id.layoutBtnGame);

        textViewTime = (TextView) findViewById(R.id.textViewGameTime);

        TextView btnPoint = (TextView) findViewById(R.id.textViewGamesPoints);
        btnPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        headOppt = (TextView) findViewById(R.id.gameOppt);
        headYou = (TextView) findViewById(R.id.gameYou);

        imgWinCheckUp = (ImageView) findViewById(R.id.winCheckUp);
        imgWinCheckDown = (ImageView) findViewById(R.id.winCheckDown);

        textViewSet1Up = (TextView) findViewById(R.id.set1_up);
        textViewSet1Down = (TextView) findViewById(R.id.set1_down);
        textViewSet2Up = (TextView) findViewById(R.id.set2_up);
        textViewSet2Down = (TextView) findViewById(R.id.set2_down);
        textViewSet3Up = (TextView) findViewById(R.id.set3_up);
        textViewSet3Down = (TextView) findViewById(R.id.set3_down);
        textViewSet4Up = (TextView) findViewById(R.id.set4_up);
        textViewSet4Down = (TextView) findViewById(R.id.set4_down);
        textViewSet5Up = (TextView) findViewById(R.id.set5_up);
        textViewSet5Down = (TextView) findViewById(R.id.set5_down);

        imgWinCheckUp.setVisibility(View.INVISIBLE);
        imgWinCheckDown.setVisibility(View.INVISIBLE);

        textViewSet1Up.setText("0");
        textViewSet1Down.setText("0");

        textViewSet2Up.setText("");
        textViewSet2Down.setText("");
        textViewSet3Up.setText("");
        textViewSet3Down.setText("");
        textViewSet4Up.setText("");
        textViewSet4Down.setText("");
        textViewSet5Up.setText("");
        textViewSet5Down.setText("");

        loadState();

        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_STEP_COUNT_ACTION)) {
                    Log.d(TAG, "receive brocast !");

                    stepCounter.setText(String.valueOf((int)(step_count_end - step_count_start)));


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
        stepCounter.setText(String.valueOf((int)(step_count_end - step_count_start)));
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

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
        ImageView imgWinCheckUp;
    ImageView imgWinCheckDown;

    TextView textViewSet1Up;
    TextView textViewSet1Down;
    TextView textViewSet2Up;
    TextView textViewSet2Down;
    TextView textViewSet3Up;
    TextView textViewSet3Down;
    TextView textViewSet4Up;
    TextView textViewSet4Down;
    TextView textViewSet5Up;
    TextView textViewSet5Down;
         */

        if (isAmbient()) {

            gameContainer.setBackgroundColor(Color.BLACK);

            SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

            imgStepCount.setImageResource(R.drawable.ic_directions_run_white_48dp);

            stepCounter.setTextColor(Color.WHITE);

            headOppt.setTextColor(Color.WHITE);
            headYou.setTextColor(Color.WHITE);



            layoutBtn.setVisibility(View.GONE);
            textViewTime.setVisibility(View.VISIBLE);

            textViewTime.setText(AMBIENT_DATE_FORMAT.format(new Date()));

            imgWinCheckUp.setImageResource(R.drawable.ic_done_white_48dp);
            imgWinCheckDown.setImageResource(R.drawable.ic_done_white_48dp);

            textViewSet1Up.setTextColor(Color.WHITE);
            textViewSet1Down.setTextColor(Color.WHITE);
            textViewSet2Up.setTextColor(Color.WHITE);
            textViewSet2Down.setTextColor(Color.WHITE);
            textViewSet3Up.setTextColor(Color.WHITE);
            textViewSet3Down.setTextColor(Color.WHITE);
            textViewSet4Up.setTextColor(Color.WHITE);
            textViewSet4Down.setTextColor(Color.WHITE);
            textViewSet5Up.setTextColor(Color.WHITE);
            textViewSet5Down.setTextColor(Color.WHITE);
        } else {
            gameContainer.setBackgroundColor(Color.WHITE);

            imgStepCount.setImageResource(R.drawable.ic_directions_run_black_48dp);

            stepCounter.setTextColor(Color.BLACK);

            headOppt.setTextColor(Color.WHITE);
            headYou.setTextColor(Color.WHITE);



            textViewTime.setVisibility(View.GONE);
            layoutBtn.setVisibility(View.VISIBLE);

            imgWinCheckUp.setImageResource(R.drawable.ic_done_black_48dp);
            imgWinCheckDown.setImageResource(R.drawable.ic_done_black_48dp);

            textViewSet1Up.setTextColor(Color.BLACK);
            textViewSet1Down.setTextColor(Color.BLACK);
            textViewSet2Up.setTextColor(Color.BLACK);
            textViewSet2Down.setTextColor(Color.BLACK);
            textViewSet3Up.setTextColor(Color.BLACK);
            textViewSet3Down.setTextColor(Color.BLACK);
            textViewSet4Up.setTextColor(Color.BLACK);
            textViewSet4Down.setTextColor(Color.BLACK);
            textViewSet5Up.setTextColor(Color.BLACK);
            textViewSet5Down.setTextColor(Color.BLACK);
        }
    }

    private void loadState() {
        if (stack.isEmpty()) {
            Log.d(TAG, "stack is empty!");

            imgWinCheckUp.setVisibility(View.INVISIBLE);
            imgWinCheckDown.setVisibility(View.INVISIBLE);

            textViewSet1Up.setText("0");
            textViewSet1Down.setText("0");

            textViewSet2Up.setText("");
            textViewSet2Down.setText("");
            textViewSet3Up.setText("");
            textViewSet3Down.setText("");
            textViewSet4Up.setText("");
            textViewSet4Down.setText("");
            textViewSet5Up.setText("");
            textViewSet5Down.setText("");

        } else {
            /*if (is_finish) { //is end?
                startTime = startTime + (System.currentTimeMillis() - endTime);
            }

            is_finish = false;*/

            byte current_set;

            State current_state = stack.peek();
            if (current_state != null) {
                current_set = current_state.getCurrent_set();

                Log.d(TAG, "########## current state start ##########");
                Log.d(TAG, "current set : " + current_state.getCurrent_set());
                Log.d(TAG, "Serve : " + current_state.isServe());
                Log.d(TAG, "In tiebreak : " + current_state.isInTiebreak());
                Log.d(TAG, "Finish : " + current_state.isFinish());
                Log.d(TAG, "Set: up = "+current_state.getSetsUp()+" down = "+current_state.getSetsDown());

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


                for (int i = 1; i <= current_set; i++) {
                    if (i == 1) {
                        textViewSet1Up.setText(String.valueOf(current_state.getSet_game_up((byte) i)));
                        textViewSet1Down.setText(String.valueOf(current_state.getSet_game_down((byte) i)));
                    } else if (i == 2) {
                        textViewSet2Up.setText(String.valueOf(current_state.getSet_game_up((byte) i)));
                        textViewSet2Down.setText(String.valueOf(current_state.getSet_game_down((byte) i)));
                    } else if (i == 3) {
                        textViewSet3Up.setText(String.valueOf(current_state.getSet_game_up((byte) i)));
                        textViewSet3Down.setText(String.valueOf(current_state.getSet_game_down((byte) i)));
                    } else if (i == 4) {
                        textViewSet4Up.setText(String.valueOf(current_state.getSet_game_up((byte) i)));
                        textViewSet4Down.setText(String.valueOf(current_state.getSet_game_down((byte) i)));
                    } else if (i == 5) {
                        textViewSet5Up.setText(String.valueOf(current_state.getSet_game_up((byte) i)));
                        textViewSet5Down.setText(String.valueOf(current_state.getSet_game_down((byte) i)));
                    }
                }

                if (current_state.isFinish()) {
                    if (current_state.getSetsDown() > current_state.getSetsUp()) { //you win
                        imgWinCheckUp.setVisibility(View.INVISIBLE);
                        imgWinCheckDown.setVisibility(View.VISIBLE);
                    } else { //you lose
                        imgWinCheckUp.setVisibility(View.VISIBLE);
                        imgWinCheckDown.setVisibility(View.INVISIBLE);
                    }
                }

            } else {
                imgWinCheckUp.setVisibility(View.INVISIBLE);
                imgWinCheckDown.setVisibility(View.INVISIBLE);

                textViewSet1Up.setText("0");
                textViewSet1Down.setText("0");

                textViewSet2Up.setText("");
                textViewSet2Down.setText("");
                textViewSet3Up.setText("");
                textViewSet3Down.setText("");
                textViewSet4Up.setText("");
                textViewSet4Down.setText("");
                textViewSet5Up.setText("");
                textViewSet5Down.setText("");
            }
        }
    }
}
