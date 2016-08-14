package com.bignerdranch.android.newprojectdemo.front_ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bignerdranch.android.newprojectdemo.R;

public class FrontUIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_ui);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);

    }
}
