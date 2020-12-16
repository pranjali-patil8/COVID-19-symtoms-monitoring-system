package com.example.assignment_1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


public class MainActivity2 extends AppCompatActivity {
    private Spinner spinner;
    private TextView tView;
    private RatingBar rBar;
    private Hashtable<String, Float> symptoms;
    private Hashtable<String, Float> Ratings;
    Database db;
    private Button data_save;
    String heart_rate;
    String res_rate;
    private String str;
    public MainActivity2() {
        symptoms = new Hashtable<String, Float>() {{

            put("Heart Rate", (float) 0);
            put("Respiratory Rate", (float) 0);
            put("Fever", (float) 0);
            put("Nausea", (float) 0);
            put("Headache", (float) 0);
            put("Diarrhea", (float) 0);
            put("Soar Throat", (float) 0);
            put("Muscle Ache", (float) 0);
            put("Loss Of Smell Or Taste", (float) 0);
            put("Cough", (float) 0);
            put("Shortness Of Breath", (float) 0);
            put("Feeling Tired", (float) 0);
        }};
        Ratings = new Hashtable<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        addListenerOnButton();
        addListenerOnSpinnerItemSelection();
        db = new Database(this);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            heart_rate = extras.getString("HeartRate");
            res_rate = extras.getString("RespiratoryRate");
        }
        else{
            Log.e("exception occured", "Exception!");
        }
        symptoms.put("Heart Rate", Float.parseFloat(heart_rate));
        symptoms.put("Respiratory Rate", Float.parseFloat(res_rate));
        addListenerOnDbButton();
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    private void addListenerOnDbButton() {
        data_save= findViewById(R.id.database);
        data_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_display,
                        findViewById(R.id.toast_display_window));

                TextView text = layout.findViewById(R.id.text);
                text.setText("Saving data");
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 200);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                db.insert_Sym(symptoms);
                tView.setText("");
            }
        });

    }


    public void addListenerOnButton() {

        spinner = findViewById(R.id.spinner1);
        tView = findViewById(R.id.output);
        rBar = findViewById(R.id.ratings);

        Button btn_uploadSymptoms = findViewById(R.id.upload2);

        btn_uploadSymptoms.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                float rating = rBar.getRating();
                if (Ratings.containsKey(spinner.getSelectedItem())) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_display,
                            (ViewGroup) findViewById(R.id.toast_display_window));

                    TextView text = layout.findViewById(R.id.text);
                    text.setText("Already entered this symptom");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 150);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                } else if (rating == 0 || spinner.getSelectedItemPosition() == 0) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_display,
                            (ViewGroup) findViewById(R.id.toast_display_window));

                    TextView text = layout.findViewById(R.id.text);
                    if (spinner.getSelectedItemPosition() == 0)
                        text.setText("None Selected");
                    else
                        text.setText("Zero rating selected");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM, 0, 150);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                } else {
                    Ratings.put((String) spinner.getSelectedItem(), rating);
                    symptoms.put((String) spinner.getSelectedItem(), rating);
                    Set<String> keys = Ratings.keySet();
                    Iterator<String> itr = keys.iterator();
                    tView.setText("");
                    while (itr.hasNext()) {
                        str = itr.next();
                        tView.append(str + " : " + Ratings.get(str) + "\n");
                    }
                    rBar.setRating(0);
                    spinner.setSelection(0);
                }
            }
        });
    }
}
