/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.tiltspot;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
//import yang ditambahkan
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import android.widget.Toast;
import android.os.Environment;


public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    //button deklarasi save
    Button save;

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Sensor akselerometer dan magnetometer, sebagaimana diambil dari sensor manager.
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    // Data ini dari akselerometer dan magnetometer. The arrays hold values
    // for X, Y, and Z.
    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    // TextViews to display current sensor values.
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;

    // ImageView drawables untuk menampilkan spot.
    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotLeft;
    private ImageView mSpotRight;

    //sistem display. diperlukan untuk menentukan rotasi.
    private Display mDisplay;

    //menambahkan deklarasi label
    private TextView label1;
    private TextView label2;
    private TextView label3;

    //Nilai yang sangat kecil untuk akselerometer (pada ketiga sumbu) seharusnya
    // diartikan sebagai 0. Nilai ini adalah jumlah yang dapat diterima
    // drift bukan nol.
    private static final float VALUE_DRIFT = 0.05f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //yang ditambahkan
        //final TextView output = findViewById(R.id.output);
        //final EditText EnterText = findViewById(R.id.edit_text);
        save = findViewById(R.id.bsave);
        label1 = (TextView) findViewById(R.id.label_azimuth);
        mTextSensorAzimuth = (TextView) findViewById(R.id.value_azimuth);
        label2 = (TextView) findViewById(R.id.label_pitch);
        mTextSensorPitch = (TextView) findViewById(R.id.value_pitch);
        label3 = (TextView) findViewById(R.id.label_roll);
        mTextSensorRoll = (TextView) findViewById(R.id.value_roll);
        mSpotTop = (ImageView) findViewById(R.id.spot_top);
        mSpotBottom = (ImageView) findViewById(R.id.spot_bottom);
        mSpotLeft = (ImageView) findViewById(R.id.spot_left);
        mSpotRight = (ImageView) findViewById(R.id.spot_right);

        //save.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
                //if (!EnterText.getText().toString().isEmpty()) {
                    //File file = new File(MainActivity.this.getFilesDir(), "text");
                    //if (!file.exists()) {
                    //    file.mkdir();
                    //}
                    //try {
                    //    File gpxfile = new File(file, "sample");
                    //    FileWriter writer = new FileWriter(gpxfile);
                   //     writer.append(EnterText.getText().toString());
                    //    writer.flush();
                    //    writer.close();
                    //    output.setText(readFIle());
                    //    Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();

                    // } catch (Exception e) {

                    //}
                //}
            //}
        //});

        //button save dan method
        save.setOnClickListener((view)-> {
            if (!mTextSensorAzimuth.getText().toString().isEmpty()) {
                File file = new File(MainActivity.this.getFilesDir(), "text");
                if (!file.exists()) {
                   file.mkdir();
                }
                try{
                    File gpxfile = new File(file, "sample");
                        FileWriter writer = new FileWriter(gpxfile);
                         writer.append(label1.getText().toString() + "\t");
                         writer.append(mTextSensorAzimuth.getText().toString() + "\n");
                         writer.append(label2.getText().toString() +"\t ");
                         writer.append(mTextSensorPitch.getText().toString() +"\n");
                         writer.append(label3.getText().toString() + "\t");
                         writer.append(mTextSensorRoll.getText().toString()+ "\n");
                         writer.flush();
                         writer.close();

                        Toast.makeText(MainActivity.this, "menyimpan data azimuth, pitch dan roll", Toast.LENGTH_LONG).show();
                }catch (Exception e){

                }
            }
        });




        // mendapatkan sensor akselerometer dan magnetometer dari pengelola sensor.
        // Metode getDefaultSensor() mengembalikan nol jika sensor
        // tidak tersedia di perangkat.
        mSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD);


        // menampilkan tampilan dari window manager untuk rotasi layar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
    }

    //method readFile
    private String readFIle() {
        File fileEvents = new File(MainActivity.this.getFilesDir() + "/text/sample");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append(' ');
            }
            br.close();
        } catch (IOException e) { }
            String result = text.toString();
            return result;
        }








    /**
     * Listeners for the sensors are registered in this callback so that
     * they can be unregistered in onStop().
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Listeners for the sensors are registered in this callback and
        // can be unregistered in onStop().
        //
        // Check to ensure sensors are available before registering listeners.
        // Both listeners are registered with a "normal" amount of delay
        // (SENSOR_DELAY_NORMAL).
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // The sensor type (as defined in the Sensor class).
        int sensorType = sensorEvent.sensor.getType();

        // The sensorEvent object is reused across calls to onSensorChanged().
        // clone() gets a copy so the data doesn't change out from under us
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }
        // Compute the rotation matrix: merges and translates the data
        // from the accelerometer and magnetometer, in the device coordinate
        // system, into a matrix in the world's coordinate system.
        //
        // The second argument is an inclination matrix, which isn't
        // used in this example.
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);

        // Remap the matrix based on current device/activity rotation.
        float[] rotationMatrixAdjusted = new float[9];
        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                rotationMatrixAdjusted = rotationMatrix.clone();
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                        rotationMatrixAdjusted);
                break;
        }

        //meng get orientasi perangkat (azimuth, pitch, roll) berdasarkan
        // pada matriks rotasi. Satuan output adalah radian.
        float orientationValues[] = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrixAdjusted,
                    orientationValues);
        }

        // Pull out the individual values from the array.
        float azimuth = orientationValues[0];
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        // Nilai pitch and roll yang mendekati 0 tetapi tidak menyebabkan
        // animasi untuk mem-flash banyak. Sesuaikan pitch and roll ke 0 untuk very
        // nilai kecil (sebagaimana didefinisikan oleh VALUE_DRIFT).
        if (Math.abs(pitch) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(roll) < VALUE_DRIFT) {
            roll = 0;
        }

        // Fill in the string placeholders and set the textview text.
        mTextSensorAzimuth.setText(getResources().getString(
                R.string.value_format, azimuth));
        mTextSensorPitch.setText(getResources().getString(
                R.string.value_format, pitch));
        mTextSensorRoll.setText(getResources().getString(
                R.string.value_format, roll));

        // Reset all spot values to 0. Without this animation artifacts can
        // happen with fast tilts.
        mSpotTop.setAlpha(0f);
        mSpotBottom.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotRight.setAlpha(0f);

        // Set spot color (alpha/opacity) equal to pitch/roll.
        // this is not a precise grade (pitch/roll can be greater than 1)
        // but it's close enough for the animation effect.
        if (pitch > 0) {
            mSpotBottom.setAlpha(pitch);
        } else {
            mSpotTop.setAlpha(Math.abs(pitch));
        }
        if (roll > 0) {
            mSpotLeft.setAlpha(roll);
        } else {
            mSpotRight.setAlpha(Math.abs(roll));
        }
    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}