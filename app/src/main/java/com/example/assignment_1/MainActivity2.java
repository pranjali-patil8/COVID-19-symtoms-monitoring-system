package com.example.assignment_1;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


public class MainActivity2 extends AppCompatActivity {
    private Spinner spinner;
    private gpslocation gpsLocation;
//    private TextView tView;
    private RatingBar rBar;
    private EditText userId;
    private EditText dateId;
    private Hashtable<String, Float> symptoms;
    private Hashtable<String, Float> Ratings;
    Database db;
    private Button data_save;
    private Button server_upload;
    private Button gps_data;
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
            put("Latitude", (float) 0);
            put("Longitude", (float) 0);
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
        server_upload=(Button)findViewById(R.id.serverId);
        userId=(EditText) findViewById(R.id.userId);
        dateId=(EditText) findViewById(R.id.dateId);

        gps_data=findViewById(R.id.gps_Id);
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        server_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadTask upload=new UploadTask();
                upload.execute();
            }
        });
        gps_data.setOnClickListener(v->{
            gpsLocation = new gpslocation(MainActivity2.this);
            if(gpsLocation.canGetLocation()){
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                Toast.makeText(this, "Location:\nLatitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();
                symptoms.put("Latitude", (float) latitude);
                symptoms.put("Longitude", (float) longitude);
            }else{
                gpsLocation.showSettingsAlert();
            }
        });

    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                String url = "http://10.0.2.2:5000/android_data";
                String charset = "UTF-8";
                String accept = "1";


                File dbFile = new File(Environment.getDataDirectory().getPath()+"/user/0/com.example.assignment_1/databases/CovidSymptoms.db");

                String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
                String CRLF = "\r\n"; // Line separator required by multipart/form-data.

                URLConnection connection;

                connection = new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                try (
                        OutputStream output = connection.getOutputStream();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                ) {
                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"accept\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(accept).append(CRLF).flush();

                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"user_Id\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(userId.getText().toString()).append(CRLF).flush();

                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"date\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(dateId.getText().toString()).append(CRLF).flush();


                    // Send video file.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"upload_file\"; filename=\"" + dbFile.getName() + "\"").append(CRLF);
                    //writer.append("Content-Type: video/mp4; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
                    writer.append(CRLF).flush();
                    FileInputStream vf = new FileInputStream(dbFile);
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = vf.read(buffer, 0, buffer.length)) >= 0)
                        {
                            output.write(buffer, 0, bytesRead);

                        }
                        //   output.close();
                        //Toast.makeText(getApplicationContext(),"Read Done", Toast.LENGTH_LONG).show();
                    }catch (Exception exception)
                    {


                        //Toast.makeText(getApplicationContext(),"output exception in catch....."+ exception + "", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(exception));
                        publishProgress(String.valueOf(exception));
                        // output.close();

                    }

                    output.flush(); // Important before continuing with writer!
                    writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.


                    // End of multipart/form-data.
                    writer.append("--" + boundary + "--").append(CRLF).flush();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Request is lazily fired whenever you need to obtain information about response.
                int responseCode = ((HttpURLConnection) connection).getResponseCode();
                System.out.println(responseCode); // Should be 200

                BufferedReader br = new BufferedReader(new InputStreamReader((((HttpURLConnection) connection).getInputStream())));
                StringBuilder sb = new StringBuilder();

                String response;
                while ((response = br.readLine()) != null) {
                    sb.append(response);
                }
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Result.txt");
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(sb);

                myOutWriter.close();

                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(getApplicationContext(), "In Background Task " + text[0], Toast.LENGTH_LONG).show();
        }

    }

/*
    class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                String url = "http://10.0.2.2:5000";
                String charset = "UTF-8";
                String group_id = "40";
                String ASUid = "1200072576";
                String accept = "1";



                //File videoFile = new File(Environment.getExternalStorageDirectory()+"/heart_rate_video.db");
                //String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
                String CRLF = "\r\n"; // Line separator required by multipart/form-data.

                URLConnection connection;

                connection = new URL(url).openConnection();
                connection.setDoOutput(true);
                //connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                try (
                        OutputStream output = connection.getOutputStream();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                ) {
                    // Send normal accept.
//                    writer.append("--" + boundary).append(CRLF);
//                    writer.append("Content-Disposition: form-data; name=\"accept\"").append(CRLF);
//                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
//                    writer.append(CRLF).append(accept).append(CRLF).flush();

                    // Send normal accept.
//                    writer.append("--" + boundary).append(CRLF);
                   //writer.append("Content-Disposition: form-data; name=\"group_id\"").append(CRLF);
                    //writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(userId.getText().toString());
                    //writer.append(CRLF).append(group_id).append(CRLF).flush();
                    writer.append(CRLF);
                    // Send normal accept.
//                    writer.append("--" + boundary).append(CRLF);
                    //writer.append("Content-Disposition: form-data; name=\"ASUid\"").append(CRLF);
                    //writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(dateId.getText().toString());
                    //writer.append(CRLF).append(ASUid).append(CRLF).flush();


//                    // Send video file.
//                    writer.append("--" + boundary).append(CRLF);
//                    writer.append("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + videoFile.getName() + "\"").append(CRLF);
//                    //writer.append("Content-Type: video/mp4; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
//                    writer.append("Content-Type:", "multipart/form-data; boundary=" + boundary);

                    //writer.append(CRLF).flush();
                    //FileInputStream vf = new FileInputStream(videoFile);
//                    try {
//                        byte[] buffer = new byte[1024];
//                        int bytesRead = 0;
//                        while ((bytesRead = vf.read(buffer, 0, buffer.length)) >= 0)
//                        {
//                            output.write(buffer, 0, bytesRead);
//
//                        }
//                        //   output.close();
//                        //Toast.makeText(getApplicationContext(),"Read Done", Toast.LENGTH_LONG).show();
//                    }catch (Exception exception)
//                    {
//
//
//                        //Toast.makeText(getApplicationContext(),"output exception in catch....."+ exception + "", Toast.LENGTH_LONG).show();
//                        Log.d("Error", String.valueOf(exception));
//                        publishProgress(String.valueOf(exception));
//                        // output.close();
//
//                    }

                    //output.flush(); // Important before continuing with writer!
                    //writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.


                    // End of multipart/form-data.
//                    writer.append("--" + boundary + "--").append(CRLF).flush();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Request is lazily fired whenever you need to obtain information about response.
                int responseCode = ((HttpURLConnection) connection).getResponseCode();
                System.out.println(responseCode); // Should be 200

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


    @Override
    protected void onProgressUpdate(String... text) {
        Toast.makeText(getApplicationContext(), "In Background Task " + text[0], Toast.LENGTH_LONG).show();
    }

    }

*/


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
//                tView.setText("");
            }
        });

    }



    public void addListenerOnButton() {

        spinner = findViewById(R.id.spinner1);
//        tView = findViewById(R.id.output);
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
//                    tView.setText("");
//                    while (itr.hasNext()) {
//                        str = itr.next();
//                        tView.append(str + " : " + Ratings.get(str) + "\n");
//                    }
                    rBar.setRating(0);
                    spinner.setSelection(0);
                }
            }
        });
    }
}

