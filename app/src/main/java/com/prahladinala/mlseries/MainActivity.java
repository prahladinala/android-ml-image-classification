package com.prahladinala.mlseries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.prahladinala.mlseries.helpers.ImageHelperActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onGotoImageActivity(View view){
        // Start the image helper activity
        Intent intent = new Intent(this, ImageHelperActivity.class);
        startActivity(intent);
    }
}