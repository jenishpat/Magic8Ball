package com.jenish.magic8ball;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    String[] answers;
    Vibrator vib;

    private SensorManager mSensorManager; // Variable for SensorManager, which will let device access all the sensor
    private Sensor mAccelerometer;        // Variable for Accelerometer

    private float acelVal;      // CURRENT ACCELERATION VALUE AND GRAVITY.
    private float acelLast;     // LAST ACCELERATION VALUE AND GRAVITY.
    private float shake;        // ACCELERATION VALUE differ FROM GRAVITY.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the values from String array from strings.xml, and stores it into the variable
        answers = getResources().getStringArray(R.array.answers_array);

        // Get instance of Vibrator from current Context
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Get instance of all accessible sensor available in the device
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Using ACCELEROMETER as a default sensor and storing it into the variable
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Registering the listener for ACCELEROMETER, by passing the above variable
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH; // Earth's gravity in SI units (m/s^2). Constant Value: 9.80665
        acelLast = SensorManager.GRAVITY_EARTH; // Earth's gravity in SI units (m/s^2). Constant Value: 9.80665
        shake = 0.00f;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final TextView answerText = findViewById(R.id.answerText);

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        acelLast = acelVal;
        acelVal = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = acelVal - acelLast;
        shake = shake * 0.9f + delta;

        if (shake > 12) {

            Random randomNumber = new Random();

            int number = randomNumber.nextInt(20);
            answerText.setText(answers[number]);

            // Vibrate for 100 milliseconds
            vib.vibrate(100);

            Timer t = new Timer(false);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            answerText.setText("");
                        }
                    });
                }
            }, 3000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this); // Disabling all the sensor when exiting the app to save battery
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL); // Registers back ACCELEROMETER sensor upon resuming the activity
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this); // Disabling all the sensor when exiting the app to save battery
    }
}
