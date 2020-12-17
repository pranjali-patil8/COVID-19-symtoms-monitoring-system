package com.example.assignment_1;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        Toast.makeText(adapterView.getContext(),
                "Symptom Selected : " + adapterView.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
