package com.example.fj.processdemo;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_second).setOnClickListener(this);
        findViewById(R.id.bt_third).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_second) {
            startActivity(new Intent(this, SecondActivity.class));
        } else {
            startActivity(new Intent(this, ThirdActivity.class));
        }
    }
}
