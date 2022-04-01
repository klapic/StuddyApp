package com.example.studdyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity{
    @SuppressLint("StaticFieldLeak")
    static ListView listView;
    static ArrayList<String> items;
    @SuppressLint("StaticFieldLeak")
    static ListViewAdapter adapter;
    ImageButton capture_picture;
    String text, name;
//    EditText input;
//    ImageView enter;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private static final int REQUEST_CAMERA_CODE = 100;

    // (OCR) Create an instance of TextRecognizer
    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        listView = findViewById(R.id.listview);
//        input = findViewById(R.id.input);
//        enter = findViewById(R.id.add);
        items = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                name = items.get(i);
                Intent intent = new Intent(MainActivity.this, Question.class);
                intent.putExtra("questionData", name);
                startActivity(intent);
            }
        });

        capture_picture = findViewById(R.id.camera_button);

        adapter = new ListViewAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            text = extras.getString("data");
//            addItem(text);
//
//            // Set the edit text field to display the OCR data
////            ocrErrorFix.setText(ocrTextFinal);
//
//        }

        capture_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(MainActivity.this, "The camera is not working.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap finalPhoto = (Bitmap) bundle.get("data");

                    // (OCR) Prepare the input image
                    InputImage image = InputImage.fromBitmap(finalPhoto, 0);

                    // (OCR) Process the image
                    Task<Text> processImage =
                            recognizer.process(image)
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                                        @Override
                                        public void onSuccess(Text visionText) {
                                            // Task completed successfully
                                            // ...
                                            // (OCR) Extract text from blocks of recognized text
                                            String imageText = visionText.getText();

                                            // Start and pass the recognized text to the FixData activity
                                            Intent intent = new Intent(MainActivity.this, FixData.class);
                                            intent.putExtra("imageData", imageText);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Task failed with an exception
                                                    // ...
                                                    Toast.makeText(MainActivity.this, "OCR failed with an exception.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                }
            }
        });



//        enter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String text = input.getText().toString();
//                if (text == null || text.length() == 0){
//                    makeToast("Enter item: ");
//                }else{
//                    addItem(text);
//                    input.setText("");
//                    makeToast("Added");
//                }
//            }
//        });
        loadContent();

    }

    public void loadContent(){
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, "list.txt");
        byte[] content = new byte[(int) readFrom.length()];
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(readFrom);
            stream.read(content);

            String s = new String(content);
            s = s.substring(1, s.length()-1);
            String split [] = s.split("(END)");
            items = new ArrayList<>(Arrays.asList(split));
            adapter = new ListViewAdapter(this, items);
            listView.setAdapter(adapter);


        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "list.txt"));
            writer.write(items.toString().getBytes());
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public static void addItem (String item){
//        if (item.length() > 20)
//            items.add(item.substring(0, 15)+ "...");
//        else
            items.add(item);
        listView.setAdapter(adapter);
    }

    public static void removeItem(int remove){
        items.remove(remove);
        listView.setAdapter(adapter);
    }

    Toast t;
    private void makeToast(String s){
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        t.show();
    }

}