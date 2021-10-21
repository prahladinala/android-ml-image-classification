package com.prahladinala.mlseries.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.prahladinala.mlseries.R;

import java.io.IOException;
import java.util.List;

public class ImageHelperActivity extends AppCompatActivity {

    private int REQUEST_PICK_IMAGE = 1000;

    private ImageView inputImageView;
    private TextView outputTextView;

    private ImageLabeler imageLabeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_helper);

        inputImageView = findViewById(R.id.imageViewInput);
        outputTextView = findViewById(R.id.textViewOutput);

        imageLabeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder()
                                .setConfidenceThreshold(0.7f)
                                .build()
        );

        // Request Permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(ImageHelperActivity.class.getSimpleName(), "grant result for "+ permissions[0] + " is " + grantResults[0]);
    }

    public void onPickImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    public void onStartCamera(View view){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_PICK_IMAGE){
                // PROCESS IMAGE > URI to BITMAP
                Uri uri = data.getData();
                Bitmap bitmap = loadFromUri(uri);
                inputImageView.setImageBitmap(bitmap);

                runClassification(bitmap);
            }
        }
    }

    public Bitmap loadFromUri(Uri uri){
        Bitmap bitmap = null;

        try {
          if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
              // MODERN WAY
              ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
              bitmap = ImageDecoder.decodeBitmap(source);
          }  else {
              // OLDER WAY
              bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
          }
        }catch(IOException e){
            e.printStackTrace();
        }

        return bitmap;
    }

    private void runClassification(Bitmap bitmap){
        InputImage inputImage = InputImage.fromBitmap(bitmap,0);
        imageLabeler.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
            @Override
            public void onSuccess(@NonNull List<ImageLabel> imageLabels) {
               if(imageLabels.size() > 0){
                   StringBuilder builder = new StringBuilder();
                   for(ImageLabel label : imageLabels){
                       builder.append(label.getText())
                               .append(" : ")
                               .append(label.getConfidence())
                               .append("\n");
                   }

                   outputTextView.setText(builder.toString());
               } else {
                   outputTextView.setText("Could not classify");
               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}