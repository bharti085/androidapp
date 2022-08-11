package com.example.myapplication;
// Start the app

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

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Button button;
    @Override

    // Start of your activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // button to allow either upload or draw floormap
        button = (Button)findViewById(R.id.buttonLoadPicture);
        // run alertdialog function when button is clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog();
            }
        });
        }

    // Function to allow user to choose between upload or draw floormap
    private void alertdialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


        builder.setTitle("Choose");

        builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            // if user click on upload run openGallery function()
            @Override
            public void onClick(DialogInterface dialog, int which) {

                  openGallery();
//                }
            }
        });

        builder.setNegativeButton("Draw", new DialogInterface.OnClickListener() {
            // if user click on draw take them to browser to draw the floormap
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Sending an Intent to Browser to open specific URL
                String url= "https://html-fabric-canvas.herokuapp.com/";    // contains html page which allows user to draw floormap and save it.
                Intent i = new Intent(Intent.ACTION_VIEW);   // ACTION_VIEW: Display the data to the use
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        AlertDialog diag = builder.create();
        diag.show();
    }

    // open the gallery and allows user to choose image of floormap from gallery
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    // Result of this activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("hii", String.valueOf(data));
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();   // convert image to uri (Universal Resource Identifier)
            Log.d("hii", String.valueOf(imageUri));
//            imageView.setImageURI(imageUri);  (use
            Intent intent = new Intent(this, RSSI_xy.class);    // send msg to rssi_xy activity
            intent.putExtra("EXTRA_IMAGEVIEW_URL", imageUri.toString());
            startActivity(intent);
        }
    }
}