package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class HeatMapActivity extends AppCompatActivity {
    ImageView imageView3;
    Uri HeatmapUri;
//    MainActivity2 ob = new MainActivity2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);
        Zoom zoom = new Zoom();
        imageView3 = (ImageView) findViewById(R.id.HeatmapImage);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        HeatmapUri = Uri.parse(extras.getString("HEATMAP_IMAGEVIEW_BITMAP"));
//        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("HEATMAP_IMAGEVIEW_BITMAP");
        imageView3.setImageURI(HeatmapUri);
//        ob.file.delete();
        imageView3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                zoom.onTouch(v, event);
                return true;
            }

        });
    }
}