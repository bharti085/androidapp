package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.buttonLoadPicture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog();
            }
        });
        }
    private void alertdialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


        builder.setTitle("Choose");


//        builder.setMessage("This is an Example of Android AlertDialog with 3 Buttons!!");


        //Button One : Yes
        builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                  startActivity(new Intent(MainActivity.this, MyOtherActivity.class));
//                Toast.makeText(MainActivity.this, "Yes button Clicked!", Toast.LENGTH_LONG).show();
//                public void onClick(View v) {
                  openGallery();
//                }
            }
        });
//            public void onClick(View v) {
//                alertdialog();
//            }
//        });


        //Button Two : No
        builder.setNegativeButton("Draw", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url= "https://html-fabric-canvas.herokuapp.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
//                startActivity(new Intent(MainActivity.this, DrawActivity.class));
//                Toast.makeText(MainActivity.this, "No button Clicked!", Toast.LENGTH_LONG).show();
//                dialog.cancel();
            }
        });



        //Button Three : Neutral
//        builder.setNeutralButton("Can't Say!", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "Neutral button Clicked!", Toast.LENGTH_LONG).show();
//                dialog.cancel();
//            }
//        });


        AlertDialog diag = builder.create();
        diag.show();
    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("hii", String.valueOf(data));
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
//            imageView.setImageURI(imageUri);
            Intent intent = new Intent(this, MainActivity2.class);
            intent.putExtra("EXTRA_IMAGEVIEW_URL", imageUri.toString());
            startActivity(intent);
        }
    }
}