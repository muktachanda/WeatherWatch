package com.example.weatherwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Weather extends AppCompatActivity {
    TextView result;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        result = findViewById(R.id.etResult);
        img = findViewById(R.id.image);
        String output = getIntent().getStringExtra("result");
        result.setText(output);
        String temp = getIntent().getStringExtra("temp");
        if(temp.equals("sunny")) {
            img.setImageResource(R.drawable.sun);
        }
        else if(temp.equals("cloudy")) {
            img.setImageResource(R.drawable.raining);
        }
        else if(temp.equals("chilly")) {
            img.setImageResource(R.drawable.temperature);
        }
    }

    public void goBack(View view) {
        Intent intent = new Intent(Weather.this, MainActivity.class);
        startActivity(intent);
    }
}
