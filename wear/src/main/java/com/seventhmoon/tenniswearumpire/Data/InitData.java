package com.seventhmoon.tenniswearumpire.Data;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.common.api.GoogleApiClient;

public class InitData {
    public static SensorManager mSensorManager;
    public static Sensor mAccelerometer;
    public static Sensor mGravity;
    public static Sensor mGyroscope;
    public static Sensor mGyroscope_uncalibrated;
    public static Sensor mLinearAcceration;
    public static Sensor mRotationVector;
    public static Sensor mStepCounter;
    public static boolean is_running;
    public static SensorEventListener accelerometerListener;
    //public static SensorEventListener linearaccelerometerListener;
    public static SensorEventListener rotationVectorListener;
    public static SensorEventListener stepCountListener;

    //bluetooth
    /**
     * Name of the connected device
     */
    //public static String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    //private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    //public static StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    //public static BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    //public static BluetoothService mChatService = null;

    public static boolean is_voice_enable = false;

    public static boolean is_debug = false;

    public static GoogleApiClient mGoogleApiClient;
}
