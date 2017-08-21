package com.seventhmoon.tenniswearumpire.Data;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.common.api.GoogleApiClient;

public class InitData {
    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    public Sensor mGravity;
    public Sensor mGyroscope;
    public Sensor mGyroscope_uncalibrated;
    public Sensor mLinearAcceration;
    public Sensor mRotationVector;
    public Sensor mStepCounter;
    public boolean is_running;
    public SensorEventListener accelerometerListener;
    //public static SensorEventListener linearaccelerometerListener;
    public SensorEventListener rotationVectorListener;
    public SensorEventListener stepCountListener;

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

    public boolean is_voice_enable = false;

    public boolean is_debug = false;

    public GoogleApiClient mGoogleApiClient;
}
