package com.seventhmoon.tenniswearumpire;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.wearable.activity.WearableActivity;

import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


import java.util.ArrayList;

import static com.seventhmoon.tenniswearumpire.Data.InitData.mGoogleApiClient;



public class SetsActivity extends WearableActivity {
    private static final String TAG = SetsActivity.class.getName();

    Context context;

    //WearableRecyclerView wearableRecyclerView;

    WheelPicker wheelPicker;

    //SettingAdapter settingAdapter;

    ArrayList<String> myList = new ArrayList<>();
    private int selected = 0;

    AlertDialog ad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sets_layout);

        Log.d(TAG, "onCreate");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
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

        mGoogleApiClient.connect();

        context = getBaseContext();

        wheelPicker = (WheelPicker) findViewById(R.id.wheel_picker_sets);

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
        mGoogleApiClient.disconnect();
        super.onDestroy();

    }



    protected void showResetlog() {

        final View promptView = View.inflate(SetsActivity.this, R.layout.dialog_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetsActivity.this);
        alertDialogBuilder.setView(promptView);


        final TextView title = (TextView) promptView.findViewById(R.id.txtTitle);
        CircledImageView btnCancel = (CircledImageView) promptView.findViewById(R.id.btn_cancel);
        CircledImageView btnConfirm = (CircledImageView) promptView.findViewById(R.id.btn_ok);
        //final TextView msg = (TextView) promptView.findViewById(R.id.txtMsg);
        //final Button cancel = (Button) promptView.findViewById(R.id.dialog_cancel);
        //final Button confirm = (Button) promptView.findViewById(R.id.dialog_confirm);



        title.setTextColor(Color.BLACK);
        title.setText(getResources().getString(R.string.select_sets)+"\n"+myList.get(selected));

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetsActivity.this, TiebreakActivity.class);
                intent.putExtra("SETUP_SET", String.valueOf(selected));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        //msg.setTextColor(Color.BLACK);
        //msg.setText(myList.get(selected).toString());
        //alertDialogBuilder.setTitle(getResources().getString(R.string.game_reset));
        //alertDialogBuilder.setMessage(getResources().getString(R.string.game_reset));
        //final ImageView imgYes = (ImageView) promptView.findViewById(R.id.imgYes);
        //final ImageView imgNo = (ImageView) promptView.findViewById(R.id.imgYes);

        // setup a dialog window


        /*alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(SetsActivity.this, TiebreakActivity.class);
                intent.putExtra("SETUP_SET", String.valueOf(selected));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();

            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/


        ad = alertDialogBuilder.show();
    }
}
