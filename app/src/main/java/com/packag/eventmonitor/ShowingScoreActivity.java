package com.packag.eventmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowingScoreActivity extends AppCompatActivity {
    TextView tv_ass_score;
    Intent i;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_showing_score);
        initializeComponent();
        setComponents();
    }

    private void initializeComponent() {
        tv_ass_score = findViewById(R.id.tv_ass_score);
        i = getIntent();
    }

    private void setComponents() {
        tv_ass_score.setText(i.getStringExtra("score"));
    }
}
