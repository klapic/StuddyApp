package com.example.studdyapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FixData extends AppCompatActivity {
    String ocrTextFinal;
    EditText ocrErrorFix;
    FloatingActionButton saveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_data);

        // Grab the object of the edit text field and floating action button
        ocrErrorFix=findViewById(R.id.editTextMultiLine);
        saveData=findViewById(R.id.saveData);

        // Collect the OCR data passed from MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ocrTextFinal = extras.getString("imageData");
            // Set the edit text field to display the OCR data
            ocrErrorFix.setText(ocrTextFinal);
        }

        saveData.setOnClickListener((View v) -> {
            String text = ocrErrorFix.getText().toString();
            MainActivity.addItem(text);
            finish();
        });
    }
}