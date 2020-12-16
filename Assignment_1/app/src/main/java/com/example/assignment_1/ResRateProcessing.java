package com.example.assignment_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ResRateProcessing extends AppCompatActivity {

    public float csvProcessing() throws IOException {

        File csvfile = new File("/sdcard/CSVBreathe19.csv");
        CSVReader reader = null;
        int max_length=1280;
        try {
            reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] nextLine;
        ArrayList<Float> values = new ArrayList<>();
        ArrayList<Float> values_final=new ArrayList<>();

        while ((nextLine = reader.readNext()) != null) {
            values.add(Float.parseFloat(nextLine[0]));
        }
        for(int i=0;i<=max_length;i++){
            values_final.add(values.get(i));

        }
        Log.d("Array", String.valueOf(values_final));

        ArrayList<Float> rolling_mean = Rolling_Mean.rollingMean(values_final);
        Float[] simpleMovingAvgArray = new Float[rolling_mean.size()];
        simpleMovingAvgArray = rolling_mean.toArray(simpleMovingAvgArray);

        int zeroCrossings = InvokeZeroCrossing.invoke_zeroCrossing(simpleMovingAvgArray);
        zeroCrossings /= 2;
        Log.d("ZERO", String.valueOf(zeroCrossings));
        if (values_final.size() == 0){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("INVALID");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        float resp_rate = (zeroCrossings*60)/values_final.size();

        Log.d("RESPIRATORY_RATE", String.valueOf(resp_rate));
        return resp_rate;
    }
}
