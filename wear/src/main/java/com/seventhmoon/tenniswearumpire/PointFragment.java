package com.seventhmoon.tenniswearumpire;


import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.seventhmoon.tenniswearumpire.Data.State;


import static com.seventhmoon.tenniswearumpire.MainMenu.calculateScore;
import static com.seventhmoon.tenniswearumpire.MainMenu.is_finish;
import static com.seventhmoon.tenniswearumpire.MainMenu.serve;
import static com.seventhmoon.tenniswearumpire.MainMenu.set;
import static com.seventhmoon.tenniswearumpire.MainMenu.stack;
import static com.seventhmoon.tenniswearumpire.MainMenu.startTime;
import static com.seventhmoon.tenniswearumpire.MainMenu.endTime;
import static com.seventhmoon.tenniswearumpire.MainMenu.toast;


public class PointFragment extends Fragment {
    private static final String TAG = PointFragment.class.getName();

    private TextView pointUp;
    private TextView pointDown;
    private ImageView imgServeUp;
    private ImageView imgServeDown;


    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");



        context = getContext();

        View view = inflater.inflate(R.layout.point_fragment, container, false);


        pointUp = (TextView) view.findViewById(R.id.textViewPFPointUp);
        pointDown = (TextView) view.findViewById(R.id.textViewPFPointdown);


        imgServeUp = (ImageView) view.findViewById(R.id.imageViewPFServeUp);
        imgServeDown = (ImageView) view.findViewById(R.id.imageViewPFServeDown);


        //load
        loadState();



        //ImageView btnImgBack = (ImageView) view.findViewById(R.id.imageViewPFBack);
        LinearLayout layoutBack = (LinearLayout) view.findViewById(R.id.layoutPFBack);

        layoutBack.setOnClickListener(new View.OnClickListener() {
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

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroy");

        /*if (isRegister && mReceiver != null) {
            try {
                context.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
        }*/

        super.onDestroyView();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }
    @Override
    public void onResume() {

        Log.i(TAG, "onResume");

        /*if (historyAdapter != null)
            historyAdapter.notifyDataSetChanged();*/





        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

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

    /*public void toast(String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }*/
}
