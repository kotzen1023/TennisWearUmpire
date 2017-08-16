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
    private static String tiebreak;
    private static String deuce;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deuce_layout);

        Log.d(TAG, "onCreate");

        context = getBaseContext();

        sets = getIntent().getStringExtra("SETUP_SET");
        tiebreak = getIntent().getStringExtra("SETUP_TIEBREAK");
        deuce = getIntent().getStringExtra("SETUP_DEUCE");

        Log.d(TAG, "Get set = "+sets);
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

    /*private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                //.setTitle(message)
                .setPositiveButton(getResources().getString(R.string.dialog_confirm), okListener)
                .setNegativeButton(getResources().getString(R.string.dialog_cancel), okListener)
                .create()
                .show();
    }*/

    protected void showResetlog() {

        final View promptView = View.inflate(ServeActivity.this, R.layout.dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServeActivity.this);
        alertDialogBuilder.setView(promptView);

        final TextView title = (TextView) promptView.findViewById(R.id.txtTitle);
        //final TextView msg = (TextView) promptView.findViewById(R.id.txtMsg);
        //final Button cancel = (Button) promptView.findViewById(R.id.dialog_cancel);
        //final Button confirm = (Button) promptView.findViewById(R.id.dialog_confirm);



        title.setTextColor(Color.BLACK);
        title.setText(getResources().getString(R.string.select_serve)+"\n"+myList.get(selected));

        //msg.setTextColor(Color.BLACK);
        //msg.setText(myList.get(selected).toString());
        //alertDialogBuilder.setTitle(getResources().getString(R.string.game_reset));
        //alertDialogBuilder.setMessage(getResources().getString(R.string.game_reset));
        //final ImageView imgYes = (ImageView) promptView.findViewById(R.id.imgYes);
        //final ImageView imgNo = (ImageView) promptView.findViewById(R.id.imgYes);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(ServeActivity.this, MainMenu.class);
                intent.putExtra("SETUP_SET", sets);
                //intent.putExtra("SETUP_GAME", String.valueOf(gameSpinner.getSelectedItemPosition()));
                intent.putExtra("SETUP_TIEBREAK", tiebreak);
                intent.putExtra("SETUP_DEUCE", deuce);
                intent.putExtra("SETUP_SERVE", String.valueOf(selected));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad = alertDialogBuilder.show();
    }
}
