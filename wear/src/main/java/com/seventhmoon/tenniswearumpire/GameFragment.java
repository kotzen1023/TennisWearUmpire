package com.seventhmoon.tenniswearumpire;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;

import com.seventhmoon.tenniswearumpire.Data.Constants;
import com.seventhmoon.tenniswearumpire.Data.State;

import static com.seventhmoon.tenniswearumpire.MainMenu.set;
import static com.seventhmoon.tenniswearumpire.MainMenu.stack;
import static com.seventhmoon.tenniswearumpire.MainMenu.step_count_end;
import static com.seventhmoon.tenniswearumpire.MainMenu.step_count_start;


public class GameFragment extends Fragment {
    private static final String TAG = GameFragment.class.getName();

    private Context context;

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

    TextView stepCounter;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.game_fragment, container, false);

        context = getContext();

        stepCounter = (TextView) view.findViewById(R.id.stepCount);



        imgWinCheckUp = (ImageView) view.findViewById(R.id.winCheckUp);
        imgWinCheckDown = (ImageView) view.findViewById(R.id.winCheckDown);

        textViewSet1Up = (TextView) view.findViewById(R.id.set1_up);
        textViewSet1Down = (TextView) view.findViewById(R.id.set1_down);
        textViewSet2Up = (TextView) view.findViewById(R.id.set2_up);
        textViewSet2Down = (TextView) view.findViewById(R.id.set2_down);
        textViewSet3Up = (TextView) view.findViewById(R.id.set3_up);
        textViewSet3Down = (TextView) view.findViewById(R.id.set3_down);
        textViewSet4Up = (TextView) view.findViewById(R.id.set4_up);
        textViewSet4Down = (TextView) view.findViewById(R.id.set4_down);
        textViewSet5Up = (TextView) view.findViewById(R.id.set5_up);
        textViewSet5Down = (TextView) view.findViewById(R.id.set5_down);

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
        /*context = getContext();

        pref = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        account = pref.getString("ACCOUNT", "");
        device_id = pref.getString("WIFIMAC", "");



        IntentFilter filter;

        listView = (ListView) view.findViewById(R.id.listViewHistory);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryItem item = historyAdapter.getItem(position);

                if (item.isRead_sp()) {
                    Log.d(TAG, "read sp true");
                } else {
                    item.setRead_sp(true);

                    Intent intent = new Intent(context, UpdateReadStatusService.class);
                    intent.setAction(Constants.ACTION.GET_MESSAGE_LIST_ACTION);
                    intent.putExtra("ACCOUNT", account);
                    intent.putExtra("DEVICE_ID", device_id);
                    intent.putExtra("DOC_NO", item.getMsg());
                    context.startService(intent);
                }


                if (item != null) {
                    Intent intent = new Intent(context, HistoryShow.class);
                    intent.putExtra("HISTORY_TYPE", String.valueOf(item.getAction()));
                    intent.putExtra("HISTORY_TITLE", item.getTitle());
                    intent.putExtra("HISTORY_MSG", item.getMsg());
                    intent.putExtra("HISTORY_DATE", item.getDate());
                    intent.putExtra("ACCOUNT", account);
                    intent.putExtra("DEVICEID", device_id);
                    intent.putExtra("READ_SP", String.valueOf(item.isRead_sp()));
                    startActivity(intent);
                }
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_NEW_NOTIFICATION_ACTION)) {
                    Log.d(TAG, "receive brocast !");

                    //historyAdapter.notifyDataSetChanged();
                    Intent getintent = new Intent(context, GetMessageService.class);
                    getintent.setAction(Constants.ACTION.GET_MESSAGE_LIST_ACTION);
                    getintent.putExtra("ACCOUNT", account);
                    getintent.putExtra("DEVICE_ID", device_id);
                    context.startService(getintent);


                } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_MESSAGE_LIST_COMPLETE)) {
                    Log.d(TAG, "receive brocast GET_MESSAGE_LIST_COMPLETE!");
                    historyAdapter = new HistoryAdapter(context, R.layout.history_item, historyItemArrayList);
                    listView.setAdapter(historyAdapter);
                    loadDialog.dismiss();


                }

                else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.GET_HISTORY_LIST_SORT_COMPLETE)) {
                    historyAdapter = new HistoryAdapter(context, R.layout.history_item, sortedNotifyList);
                    listView.setAdapter(historyAdapter);
                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.GET_NEW_NOTIFICATION_ACTION);
            filter.addAction(Constants.ACTION.GET_HISTORY_LIST_SORT_COMPLETE);
            filter.addAction(Constants.ACTION.GET_MESSAGE_LIST_COMPLETE);
            context.registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }

        //run on create
        Intent intent = new Intent(context, GetMessageService.class);
        intent.setAction(Constants.ACTION.GET_MESSAGE_LIST_ACTION);
        intent.putExtra("ACCOUNT", account);
        intent.putExtra("DEVICE_ID", device_id);
        context.startService(intent);

        loadDialog = new ProgressDialog(context);
        loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadDialog.setTitle("Loading...");
        loadDialog.setIndeterminate(false);
        loadDialog.setCancelable(false);

        loadDialog.show();*/

        //Intent intent = new Intent(context, GetMessageService.class);
        //intent.setAction(Constants.ACTION.GET_MESSAGE_LIST_ACTION);
        //intent.putExtra("ACCOUNT", account);
        //intent.putExtra("DEVICE_ID", device_id);
        //context.startService(intent);

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

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroy");

        if (isRegister && mReceiver != null) {
            try {
                context.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
        }

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

        stepCounter.setText(String.valueOf((int)(step_count_end - step_count_start)));
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
