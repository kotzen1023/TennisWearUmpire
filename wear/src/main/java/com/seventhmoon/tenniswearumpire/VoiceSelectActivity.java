package com.seventhmoon.tenniswearumpire;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.ListView;

import com.seventhmoon.tenniswearumpire.Data.ImageBuyItem;
//import com.seventhmoon.tenniswearumpire.Data.RandomString;
import com.seventhmoon.tenniswearumpire.Data.ImageBuyItemArrayAdapter;
import com.seventhmoon.tenniswearumpire.util.IabBroadcastReceiver;
import com.seventhmoon.tenniswearumpire.util.IabHelper;
import com.seventhmoon.tenniswearumpire.util.IabResult;
import com.seventhmoon.tenniswearumpire.util.Inventory;
import com.seventhmoon.tenniswearumpire.util.Purchase;

import java.util.ArrayList;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;


public class VoiceSelectActivity extends WearableActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private static final String TAG = VoiceSelectActivity.class.getName();

    public ArrayList<ImageBuyItem> imageBuyItems = new ArrayList<>();
    Context context;
    //private ImageView imageViewBack;
    //private ImageView imageViewListen;

    private ImageBuyItemArrayAdapter imageBuyItemArrayAdapter;
    private ListView listView;

    private boolean [] selected;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    private static boolean debug = false;
    //private Window window;
    ArrayList<String> additionalSkuList = new ArrayList<>();
    private int previous_select = 0;
    private int current_voice;

    //buying save
    private static boolean voice_support_gbr_woman = false;
    private static boolean voice_support_gbr_user_record = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        context = getBaseContext();

        setAmbientEnabled();

        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        current_voice = pref.getInt("VOICE_SELECT", 0);

        //load buying
        voice_support_gbr_woman = pref.getBoolean("VOICE_SUPPORT_GBR_WOMAN", false);
        voice_support_gbr_user_record = pref.getBoolean("VOICE_SUPPORT_USER_RECORD", false);

        setContentView(R.layout.voice_select_activity);
        ImageView imageViewBack = findViewById(R.id.imageViewBack);
        ImageView imageViewListen = findViewById(R.id.imageViewListen);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageViewListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VoiceSelectActivity.this, VoiceListenActivity.class);
                startActivity(intent);
            }
        });

        //in-app billing

        //1.prepare string
        String encryptedKey = "HyAqKiAPEiYrNQISAAIMFFEeYisiOSwjEikmESgyUCgoGiErEQ4oKyg0FikDCD8bLV42ElocJhMW\n" +
                "OzECYCExPhszXQ42GhgBJiZRXQsUYUcPaiUICg0kEQ5CZxFaCg4yFy0CPiomXSxVfCoZHBA2CSEd\n" +
                "ESVRAy8bKiUiMBItAS47Ej8dMV8FKDkoOV0HBSIaBw8wUSonETsoCCIhMBkXACMsBQIpXxBKGy4i\n" +
                "KxMoW1oxEhomO1kNW0YKFFgwMSwaOSgdfBxCED8lLi42PiU5JT45LjgoYUddZwwLIz4LGx8lJSEw\n" +
                "WgAjKlssPg4LOysIY1osF14tUVovOEMhIS8kOgMXFQQaEwoZD18VAhI6BCEXJUIqMD4APiZVDTMu\n" +
                "FFsnOy8sCwwMFQU+OTgyOAo8PQRQBl5IHVsDKR0FOSYyMBAhFDIIYxsmOhwGCVoxPwYRHCoAFQcF\n" +
                "YSdVOT0BAQceEDohWisSMScsHC0FMicGKzwNOw4ROgESGR4jIzEiLggkPjkgFigyKSs=";

        /*String original = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi6uKYK/2nFfED9t/BvDGa1DT0H+"+
                "Q5M5KJ62aM6mhELulZc6GmRchwLMI5bsF+PcXMm62yA5bZKTAKdY+DdpCyQ9XtL7ERR/XJtxG+8IUzM10"+
                "7ChfLC3tof1wb+l7sxTRC6DyPzqgNACMWg/EtVlRd70ky+k68Da+a9xcdy6lWczGHAjx4uUylchrIfkuM"+
                "36mhNS4oteW2QUZYf7ixArqfgtWKjrki0vmE4F0Ugl17ZyiHK+RTNO/2dUJ5aTuLxEOkI9Acu+PLE6jR8"+
                "r2EN2AHUY0t9ryBiya7n2g3T/pBxWwJXw1XkzlO/Z19Bt7WDpydRGqjxC7wucUOwgJovRKNQIDAQAB";*/

        String base64EncodedPublicKey = xorDecrypt(encryptedKey, "RichieShih");
        Log.d(TAG, "decrypted: "+base64EncodedPublicKey);

        //String encryped = xorEncrypt(original.getBytes(), "RichieShih");
        //Log.d(TAG, "encrypted: "+encryped);

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().

                mBroadcastReceiver = new IabBroadcastReceiver(VoiceSelectActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");

                //ArrayList<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.clear();
                //additionalSkuList.add("sku_voice_gbr_man_1");
                additionalSkuList.add("sku_voice_support_gbr_woman");
                //additionalSkuList.add("sku_theme_cat");
                //additionalSkuList.add("sku_theme_classic");

                //mHelper.queryInventoryAsync(mGotInventoryListener);
                mHelper.queryInventoryAsync(true, additionalSkuList, mGotInventoryListener);
            }
        });

        imageBuyItemArrayAdapter = new ImageBuyItemArrayAdapter(this, R.layout.voice_buy_item_layout, getData());

        listView = findViewById(R.id.voiceSelectListView);
        listView.setAdapter(imageBuyItemArrayAdapter);
        listView.setChoiceMode(CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(position);
                Log.i(TAG, "item " + position + " was select");

                for (int i = 0; i < listView.getCount(); i++) {
                    ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(i);

                    if (i == position) {
                        selected[position] = true;
                        item.setSelected(true);
                    } else {
                        selected[position] = false;
                        item.setSelected(false);
                    }

                }

                listView.invalidateViews();
                //gridViewVoiceAdapter.notifyDataSetChanged();

                if (!debug) {

                    if (position > 0) {
                        if (!imageBuyItems.get(position).getPurchased()) //buy items
                        {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(VoiceSelectActivity.this);
                            dialog.setTitle(getResources().getString(R.string.voice_support_buy)+" "+imageBuyItems.get(position).getTitle());
                            dialog.setIcon(R.drawable.ball_icon);
                            dialog.setCancelable(false);

                            dialog.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //buy it
                                    do_buy_theme(position);
                                }
                            });

                            dialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //don't buy it

                                    for (int i = 0; i < imageBuyItems.size(); i++) {
                                        ImageBuyItem item = imageBuyItems.get(i);

                                        if (i == previous_select) {
                                            selected[previous_select] = true;
                                            item.setSelected(true);
                                        } else {
                                            selected[previous_select] = false;
                                            item.setSelected(false);
                                        }

                                    }
                                    //save current position
                                    editor = pref.edit();
                                    editor.putInt("VOICE_SELECT", previous_select);
                                    editor.apply();

                                    listView.invalidateViews();
                                }
                            });
                            dialog.show();
                        } else { //you have buy this one
                            //save current position
                            editor = pref.edit();
                            editor.putInt("VOICE_SELECT", position);
                            editor.apply();

                            previous_select = position;
                        }
                    } else { //position == 0
                        //save current position
                        editor = pref.edit();
                        editor.putInt("VOICE_SELECT", position);
                        editor.apply();

                        previous_select = position;
                    }
                } else {

                    Log.e(TAG, "voice change to "+position);

                    //save current position
                    editor = pref.edit();
                    editor.putInt("VOICE_SELECT", position);
                    editor.apply();

                    previous_select = position;
                }
            }
        });

        selected = new boolean[listView.getCount()];
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

        if (mHelper != null) mHelper.dispose();
        mHelper = null;

        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        //updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        //updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        //updateDisplay();
        super.onExitAmbient();
    }

    public static String xorDecrypt(String input, String key) {
        byte[] inputBytes = Base64.decode(input, Base64.DEFAULT);
        //byte[] inputBytes = input.getBytes(Charset.forName("ISO-8859-1"));
        int inputSize = inputBytes.length;

        byte[] keyBytes = key.getBytes();
        int keySize = keyBytes.length - 1;

        byte[] outBytes = new byte[inputSize];
        for (int i = 0; i < inputSize; i++) {
            outBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keySize]);
        }
        return new String(outBytes);
    }

    public static String xorEncrypt(byte[] input, String key) {
        int inputSize = input.length;
        byte[] keyBytes = key.getBytes();
        int keySize = keyBytes.length - 1;

        byte[] outTempBytes = new byte[inputSize];
        for (int i = 0; i < inputSize; i++) {
            outTempBytes[i] = (byte) (input[i] ^ keyBytes[i % keySize]);
        }

        String output = Base64.encodeToString(outTempBytes, Base64.DEFAULT);

        return output;
    }

    private ArrayList<ImageBuyItem> getData() {
        //clear
        imageBuyItems.clear();
        Bitmap bitmap_simple = BitmapFactory.decodeResource(getResources(), R.drawable.uk_flag);
        imageBuyItems.add(new ImageBuyItem(bitmap_simple, getResources().getString(R.string.voice_support_gbr_man)));

        Bitmap bitmap_uk_woman = BitmapFactory.decodeResource(getResources(), R.drawable.uk_flag);
        imageBuyItems.add(new ImageBuyItem(bitmap_uk_woman, getResources().getString(R.string.voice_support_gbr_woman)));

        //Bitmap bitmap_user_record = BitmapFactory.decodeResource(getResources(), R.drawable.ic_record_voice_over_white_48dp);
        //imageBuyItems.add(new ImageBuyItem(bitmap_user_record, getResources().getString(R.string.voice_user_record)));

        for(int i=0; i<imageBuyItems.size(); i++) {
            if (i == current_voice) {
                imageBuyItems.get(i).setSelected(true);
            } else {
                imageBuyItems.get(i).setSelected(false);
            }
        }



        return imageBuyItems;
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            //gbr woman
            if (inventory != null) {
                imageBuyItems.get(1).setPurchase(inventory.getPurchase("sku_voice_support_gbr_woman"));

                if (inventory.getPurchase("sku_voice_support_gbr_woman") != null) {


                    Log.i(TAG, "sku_voice_support_gbr_woman = " + inventory.getSkuDetails("sku_voice_support_gbr_woman").getPriceCurrencyCode() + " " +
                            inventory.getSkuDetails("sku_voice_support_gbr_woman").getPrice() + "purchase " + inventory.getPurchase("sku_voice_support_gbr_woman"));


                    if (inventory.getPurchase("sku_voice_support_gbr_woman") == null) { //not buy yet
                        if (debug)
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_support_gbr_woman));
                        else
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_support_gbr_woman)+"\n" + inventory.getSkuDetails("sku_voice_support_gbr_woman").getPrice());
                        imageBuyItems.get(1).setPurchased(false);
                    } else {
                        if (debug)
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_support_gbr_woman));
                        else
                            imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_support_gbr_woman)+"\n" + "Purchased");
                        imageBuyItems.get(1).setPurchased(true);
                    }
                } else {
                    Log.e(TAG, "inventory.getPurchase = null");
                    if (debug)
                        imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_support_gbr_woman));
                    else
                        imageBuyItems.get(1).setTitle(getResources().getString(R.string.voice_support_gbr_woman)+"\n" + inventory.getSkuDetails("sku_voice_support_gbr_woman").getPrice());
                    imageBuyItems.get(1).setPurchased(false);
                }
            } else {
                Log.e(TAG, "inventory == null");
            }



            //user record
            /*if (inventory != null) {
                imageBuyItems.get(2).setPurchase(inventory.getPurchase("sku_voice_support_user_record"));
            }

            if (inventory.getPurchase("sku_voice_support_user_record") != null) {


                Log.i(TAG, "sku_voice_support_user_record = " + inventory.getSkuDetails("sku_voice_support_user_record").getPriceCurrencyCode() + " " +
                        inventory.getSkuDetails("sku_voice_support_user_record").getPrice() + "purchase " + inventory.getPurchase("sku_voice_support_user_record"));


                if (inventory.getPurchase("sku_voice_support_user_record") == null) { //not buy yet
                    if (debug)
                        imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record));
                    else
                        imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record)+"\n" + inventory.getSkuDetails("sku_voice_support_user_record").getPrice());
                    imageBuyItems.get(2).setPurchased(false);
                } else {
                    if (debug)
                        imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record));
                    else
                        imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record)+"\n" + getResources().getString(R.string.voice_change_purchased));
                    imageBuyItems.get(2).setPurchased(true);
                }
            } else {
                Log.e(TAG, "inventory.getPurchase = null");
                if (debug)
                    imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record));
                else
                    imageBuyItems.get(2).setTitle(getResources().getString(R.string.voice_user_record)+"\n" + inventory.getSkuDetails("sku_voice_support_user_record").getPrice());
                imageBuyItems.get(2).setPurchased(false);
            }*/


            imageBuyItemArrayAdapter.notifyDataSetChanged();

        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {

            int select = 0;

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);

                select = previous_select;
            }
            else if (purchase.getSku().equals("sku_voice_support_gbr_woman")) {
                imageBuyItems.get(1).setPurchased(true);

                select = 1;

            } /*else if (purchase.getSku().equals("sku_voice_support_user_record")) {
                imageBuyItems.get(2).setPurchased(true);
                select = 2;

            }*/

            for (int i = 0; i < imageBuyItems.size(); i++) {
                ImageBuyItem item = imageBuyItems.get(i);

                if (i == select) {
                    selected[select] = true;
                    item.setSelected(true);
                } else {
                    selected[select] = false;
                    item.setSelected(false);
                }

            }

            //save current position
            editor = pref.edit();
            editor.putInt("VOICE_SELECT", select);
            editor.apply();

            listView.invalidateViews();
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        //clickButton.setEnabled(true);
                        Log.d(TAG, "buy Consume "+purchase.getSku()+" success!");
                    } else {
                        // handle error
                        Log.e(TAG, "handle error");
                    }
                }
            };



    IabHelper.QueryInventoryFinishedListener mReceivedInventoryBuyGBRManListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
                Log.e(TAG, "handle error");
            } else {
                mHelper.consumeAsync(inventory.getPurchase("sku_voice_support_gbr_woman"),
                        mConsumeFinishedListener);
            }
        }
    };

    protected void do_buy_theme(int position)
    {
        //RandomString randomString = new RandomString(36);
        //Purchase purchase = imageBuyItems.get(0).getPurchase();
        switch (position)
        {
            case 1: //gbr woman
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSelectActivity.this, "sku_voice_support_gbr_woman", 10001, mPurchaseFinishedListener, null);
                //mHelper.consumeAsync(imageBuyItems.get(0).getPurchase(), mConsumeFinishedListener);
                    /*if (purchase != null) {
                        Log.d(TAG, "purchase != null");
                    } else {
                        Log.d(TAG, "purchase == null");
                    }*/

                break;
            case 2: //user record
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSelectActivity.this, "sku_voice_support_user_record", 10001, mPurchaseFinishedListener, null);
                //mHelper.consumeAsync(imageBuyItems.get(0).getPurchase(), mConsumeFinishedListener);
                    /*if (purchase != null) {
                        Log.d(TAG, "purchase != null");
                    } else {
                        Log.d(TAG, "purchase == null");
                    }*/

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (mHelper != null && mHelper.handleActivityResult(requestCode, resultCode, data))
        {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
            return;
        }

        // not handled, so handle it ourselves (here's where you'd
        // perform any handling of activity results not related to in-app
        // billing...

        super.onActivityResult(requestCode, resultCode, data);
    }
}
