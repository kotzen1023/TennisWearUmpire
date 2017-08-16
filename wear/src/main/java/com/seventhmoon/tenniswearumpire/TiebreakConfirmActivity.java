package com.seventhmoon.tenniswearumpire;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class TiebreakConfirmActivity extends WearableActivity {
    private static final String TAG = SetsConfirmActivity.class.getName();

    Context context;
    private TextView txtTitle;
    private CircledImageView btnCancel;
    private CircledImageView btnOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.dialog_layout);
        context = getBaseContext();
        final String setup_set = getIntent().getStringExtra("SETUP_SET");
        final String setup_tiebreak = getIntent().getStringExtra("SETUP_TIEBREAK");

        Log.e(TAG, "setup_set = "+setup_set);
        Log.e(TAG, "setup_tiebreak = "+setup_tiebreak);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnCancel = (CircledImageView) findViewById(R.id.btn_cancel);
        btnOk = (CircledImageView) findViewById(R.id.btn_ok);


        String selected_tiebreak;
        switch (setup_tiebreak) {
            case "0":
                selected_tiebreak = getResources().getString(R.string.setup_tiebreak);
                break;
            case "1":
                selected_tiebreak = getResources().getString(R.string.setup_deciding_game);
                break;
            default:
                selected_tiebreak = getResources().getString(R.string.setup_tiebreak);
                break;

        }

        txtTitle.setText(getResources().getString(R.string.select_rule)+"\n"+selected_tiebreak);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TiebreakConfirmActivity.this, DeuceActivity.class);
                intent.putExtra("SETUP_SET", setup_set);
                intent.putExtra("SETUP_TIEBREAK", setup_tiebreak);
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
}
