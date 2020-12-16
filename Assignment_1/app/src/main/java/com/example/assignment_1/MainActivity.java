package com.example.assignment_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int VIDEO_CAPTURE = 101;
    private Uri fileUri;
    private TextView xText, yText, zText;
    private Sensor mySensor;
    float heart_rate;
    float res_rate;
    private SensorManager SM;
    private float[] mAccelerometerData = new float[3];
    private Button bt2;
    private ProgressBar progressBar;
//    ArrayList<Integer> zc;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    static {
        System.loadLibrary("opencv_java3");
    }

//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        addListenerOnUploadRespRateButton();




        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        Button next = findViewById(R.id.symptoms);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity2.class);
                myIntent.putExtra("HeartRate", Float.toString(heart_rate));
                myIntent.putExtra("RespiratoryRate", Float.toString(res_rate));

                startActivity(myIntent);


            }

        });

        Button bt1 = (Button) findViewById(R.id.heart_rate);

        if (!hasCamera()) {
            bt1.setEnabled(false);
        }

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();

            }
        });
        Button bt3 = (Button) findViewById(R.id.res_rate);
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SM.registerListener((SensorEventListener) MainActivity.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                String[] a = Arrays.toString(mAccelerometerData).split("[\\[\\]]")[1].split(", ");
                writeToCsv(a);
            }
        });

        Button bt4 = (Button) findViewById(R.id.upload_signs);
        bt4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                AtomicInteger f = new AtomicInteger(15);
                while(f.get() >= 0){
                    HeartRateProcessing();
                    f.getAndDecrement();
                }
                heart_rate = HeartRateProcessing();
                Log.d("HEART_RATE", String.valueOf(heart_rate));

            }

        });


    }

    private void writeToCsv(String[] a) {
        File file = new File("/sdcard/");
        file.mkdirs();
        String csv = "/sdcard/RespiratoryData.csv";

        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csv, true));
            csvWriter.writeNext(a);
            csvWriter.close();
            Toast.makeText(MainActivity.this, "File Successfully Created!!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addListenerOnUploadRespRateButton();
            }
            return;
        }
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }


    private void callRead() {
    }


    public void addListenerOnUploadRespRateButton() {
        bt2 = findViewById(R.id.button_res);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResRateProcessing rsp = new ResRateProcessing();
                try {
                    res_rate = rsp.csvProcessing();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void startRecording() {
        File mediaFile = new
                File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/heart_rate_video.mp4");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        fileUri = Uri.fromFile(mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }


    @SuppressLint("MissingSuperCall")

    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video has been saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerData[0] = sensorEvent.values[0];
            mAccelerometerData[1] = sensorEvent.values[1];
            mAccelerometerData[2] = sensorEvent.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }





    public float HeartRateProcessing() {
        String uri = "/sdcard/FingertipVideo.mp4";
        ArrayList<Float> bitmapArray = new ArrayList<>();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();


        mediaMetadataRetriever.setDataSource(uri);
        int Rate = 30000000;
        int count_frame = 0;
//        int beats=8;
        float sum = (float) 0.0;
        int pix_value;
        int frameWindow = 5;
        int frameMax = 21;

        while (count_frame <= frameMax)

        {
            Bitmap bitmap_frame = mediaMetadataRetriever.getFrameAtTime(Rate, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap_frame == null)
                break;
            Rate += 1000000;

            for (int x = 0; x < bitmap_frame.getWidth(); x++) {
                for (int y = 0; y < bitmap_frame.getHeight(); y++) {
                    pix_value = bitmap_frame.getPixel(x, y);
                    int redValue = Color.red(pix_value);
                    sum += redValue;
                }
            }

            bitmapArray.add(sum / (bitmap_frame.getWidth() * bitmap_frame.getHeight()));
            sum = 0;


            count_frame++;

            Log.d("", " Heart Rate Processing");

        }

            ArrayList<Float> movingAvgArray = Rolling_Mean.rollingMean(bitmapArray);

            Float[] simpleMovingAvgArray = new Float[movingAvgArray.size()];
            simpleMovingAvgArray = movingAvgArray.toArray(simpleMovingAvgArray);

            ArrayList<Float[]> result = new ArrayList<>();

            for (int frame = 0; frame <= simpleMovingAvgArray.length - frameWindow; frame += frameWindow) {
                Float[] newArray = Arrays.copyOfRange(simpleMovingAvgArray, frame, frame + frameWindow);

                result.add(newArray);
            }


            ArrayList<Integer> zeros=new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                int zeroCrossings = InvokeZeroCrossing.invoke_zeroCrossing(result.get(i));
                zeros.add(zeroCrossings);
            }


            for (int i = 0; i < zeros.size(); i++) {
                sum += zeros.get(i);
            }
            sum /= 2;


//        }

        float heartRate = ((sum / zeros.size())/10) * 12;
//        progressBar.setVisibility(View.GONE);
        return (int) Math.floor(heartRate*60); //converting into beats/second

    }

}








