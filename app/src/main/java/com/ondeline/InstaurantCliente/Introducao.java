package com.ondeline.InstaurantCliente;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Introducao extends AppCompatActivity {

    VerticalViewPager viewPager;
    SlideAdapter slideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introducao);
        viewPager = findViewById(R.id.viewPager);
        slideAdapter = new SlideAdapter(this);
        viewPager.setAdapter(slideAdapter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        TextView text = findViewById(R.id.text);
        text.setText("Estou pronto!");
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(Introducao.this, MainActivity.class);
                 startActivity(intent);
            }
        });
    }
}
