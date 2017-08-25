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

public class DeuceConfirmActivity extends WearableActivity {
    private static final String TAG = DeuceConfirmActivity.class.getName();

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
        final String setup_tiebreak = getIntent().getStringExtra("SETUP_TIEBREAK");
        final String setup_deuce = getIntent().getStringExtra("SETUP_DEUCE");

        Log.e(TAG, "setup_set = "+setup_set);
        Log.e(TAG, "setup_games = "+setup_games);
        Log.e(TAG, "setup_tiebreak = "+setup_tiebreak);
        Log.e(TAG, "setup_deuce = "+setup_deuce);

        mContainerView = findViewById(R.id.container);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnCancel = (CircledImageView) findViewById(R.id.btn_cancel);
        btnOk = (CircledImageView) findViewById(R.id.btn_ok);


        String selected_deuce;
        switch (setup_deuce) {
            case "0":
                selected_deuce = getResources().getString(R.string.setup_deuce);
                break;
            case "1":
                selected_deuce = getResources().getString(R.string.setup_deciding_point);
                break;
            default:
                selected_deuce = getResources().getString(R.string.setup_deuce);
                break;

        }

        txtTitle.setText(getResources().getString(R.string.select_rule)+"\n"+selected_deuce);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeuceConfirmActivity.this, ServeActivity.class);
                intent.putExtra("SETUP_SET", setup_set);
                intent.putExtra("SETUP_GAMES", setup_games);
                intent.putExtra("SETUP_TIEBREAK", setup_tiebreak);
                intent.putExtra("SETUP_DEUCE",  setup_deuce);
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
