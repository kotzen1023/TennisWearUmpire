package com.seventhmoon.tenniswearumpire;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class GamesInSetComfirmActivity extends WearableActivity {
    private static final String TAG = GamesInSetComfirmActivity.class.getName();

    Context context;
    private TextView txtTitle;
    private BoxInsetLayout mContainerView;
    private CircledImageView btnCancel;
    private CircledImageView btnOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAmbientEnabled();

        Log.d(TAG, "onCreate");

        setContentView(R.layout.dialog_layout);
        context = getBaseContext();
        final String setup_set = getIntent().getStringExtra("SETUP_SET");
        final String setup_games = getIntent().getStringExtra("SETUP_GAMES");

        Log.e(TAG, "setup_set = "+setup_set);
        Log.e(TAG, "setup_games = "+setup_games);

        mContainerView = findViewById(R.id.container);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnCancel = (CircledImageView) findViewById(R.id.btn_cancel);
        btnOk = (CircledImageView) findViewById(R.id.btn_ok);

        String selected_games;
        switch (setup_games) {
            case "0":
                selected_games = getResources().getString(R.string.setup_six_games);
                break;
            case "1":
                selected_games = getResources().getString(R.string.setup_four_game);
                break;
            default:
                selected_games = getResources().getString(R.string.setup_six_games);
                break;

        }

        txtTitle.setText(getResources().getString(R.string.select_games)+"\n"+selected_games);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamesInSetComfirmActivity.this, TiebreakActivity.class);
                intent.putExtra("SETUP_SET", setup_set);
                intent.putExtra("SETUP_GAMES",  setup_games);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

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
            txtTitle.setTextColor(Color.WHITE);

        } else {
            mContainerView.setBackground(null);
            txtTitle.setTextColor(Color.BLACK);

        }
    }
}
