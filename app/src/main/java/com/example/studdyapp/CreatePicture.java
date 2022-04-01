package com.example.studdyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreatePicture extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton captureBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_picture);

        // Grab the object of the floating action button
        captureBtn=findViewById(R.id.floatingActionButton2);

        captureBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Do something in response to floating action button
        if (view == captureBtn) {
            Intent intent = new Intent(this, ResizePicture.class);
            startActivity(intent);
        }
    }
}
