package com.example.myapplication;
// second activity which allows user to calculate rssi and coordinates by just long press and send it to server to generate heatmap
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//import static com.github.karthyks.runtimepermissions.PermissionActivity.REQUEST_PERMISSION_CODE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.ContentResolver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;


//import com.github.karthyks.runtimepermissions.Permission;
//import com.github.karthyks.runtimepermissions.googleapi.LocationSettingsHelper;
//import com.google.android.gms.location.LocationRequest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RSSI_xy extends AppCompatActivity {
    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private Button permissions;
    MyGestureListener ob = new MyGestureListener();
    ImageView imageView2;
    Uri myUri;
    Uri uri2;
    Button buttonCal;
    List<ScanResult> results;
    int i = 1;
    double operating_band;
    int rssi;
    String ssid = new String();
    String bssid = new String();
    int frequency;
    int Linkspeed;
    int ChannelNumber;
    int RxLinkSpeed;
    int TxLinkSpeed;
    int count;
    int Bandwidth;
    int SLNO = 0;
    // Context context = this;
    String currentSSID = new String();
    String previousSSID = new String();
    File file;
    float x;
    float y;
    int x1;
    int y1;
    ContentResolver resolver;
    Context incontext;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    Uri HeatmapURI;
    boolean flag=true;
    private long then;
    private int longClickDuration = 5000;
    int height,width;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        Zoom zoom = new Zoom();
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        Bundle extras = getIntent().getExtras();
        myUri = Uri.parse(extras.getString("EXTRA_IMAGEVIEW_URL"));  // receive the uri from previous activity
        imageView2.setImageURI(myUri);  // display image in imageview

        // calculate the height and width of imageview according to phone's screen
        ViewTreeObserver vto = imageView2.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView2.getViewTreeObserver().removeOnPreDrawListener(this);
                height = imageView2.getMeasuredHeight();
                width = imageView2.getMeasuredWidth();
                Log.d("Width: ", String.valueOf(imageView2.getMeasuredWidth()));
                Log.d(" Height: ", String.valueOf(imageView2.getMeasuredHeight()));
                return true;
            }
        });

        String filePath = getPath(myUri);  // finds the path of image from uri
        Log.i("url path", String.valueOf(filePath));

        Log.i("url", String.valueOf(myUri));
        resolver = getContentResolver();
        incontext = getApplicationContext();

        buttonCal = (Button) findViewById(R.id.buttonheatmap);
        buttonCal.setEnabled(false);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());


        imageView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
//                zoom.onTouch(v, event);
                return true;
            }

        });
//        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        buttonCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addRecord(filePath);   // when called send request to the server

            }
        });
    }
    // gets the path of image from image URI
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    // allow gesture listener on touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
//        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//        scaleGestureDetector.onTouchEvent(motionEvent);
//        return true;
//    }
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//            mScaleFactor *= scaleGestureDetector.getScaleFactor();
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
//            imageView2.setScaleX(mScaleFactor);
//            imageView2.setScaleY(mScaleFactor);
//            return true;
//        }
//    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onLongPress(MotionEvent event) {
            // calculate the coordinates of long press touch on screen
            x = event.getX();
            y = event.getY();
            x1=(int)x;
            y1= height - (int)y;  // make bottommost left point of imageview as (0,0) which is accepted by server
            disp();  // extract wifi information
            savetofile(); // save all the information in txt file
            checkEnabled();  // check min. no. to points taken to enable generate heatmap button
            i++;
            SLNO++;
            Toast.makeText(RSSI_xy.this, "X: " + x1 + " Y: " + y1 + "  RSSI:" + rssi, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // extract wifi information using WifiManager android library
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    public void disp() {
        //  try {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        rssi = wifiManager.getConnectionInfo().getRssi();
        bssid = wifiManager.getConnectionInfo().getBSSID();
        frequency = wifiManager.getConnectionInfo().getFrequency();

        Linkspeed = wifiManager.getConnectionInfo().getLinkSpeed();
//        RxLinkSpeed = wifiManager.getConnectionInfo().getRxLinkSpeedMbps();
//        TxLinkSpeed = wifiManager.getConnectionInfo().getTxLinkSpeedMbps();

        try {


            results = wifiManager.getScanResults();
            List<ScanResult> results = wifiManager.getScanResults();

            for (ScanResult result : results) {
                if (bssid.equals(result.BSSID)) {
                    if (result.channelWidth == 0) {
                        Bandwidth = 20;
                    } else if (result.channelWidth == 1) {
                        Bandwidth = 40;
                    } else if (result.channelWidth == 2) {
                        Bandwidth = 80;
                    } else if (result.channelWidth == 3) {
                        Bandwidth = 160;
                    } else if (result.channelWidth == 4) {
                        Bandwidth = 160;
                    } else {
                        Bandwidth = 320;
                    }

                    if (frequency >= 2412 && frequency < 2484) {
                        ChannelNumber = ((frequency - 2412) / 5 + 1);
                    } else if (frequency >= 5170 && frequency <= 5825) {
                        ChannelNumber = ((frequency - 5170) / 5 + 34);
                    } else if (frequency == 2484) {
                        ChannelNumber = 14;
                    }
                }
            }
            ssid = wifiInfo.getSSID();


            Log.d("Bharti", "currentSSID" + ssid);
            currentSSID = ssid;
            Log.d("Bharti", "prevSSID" + currentSSID);
            // to obtain the previous AP information
            if (previousSSID.equals(currentSSID)) {
                count = SLNO;
            } else if (currentSSID.equals("<unknown ssid>")) {

            } else {
                if (count != 0) {
                    SLNO = 1;
                }
            }
            if (!currentSSID.equals("<unknown ssid>")) {
                previousSSID = currentSSID;
            }


            if (frequency > 2400 && frequency < 3000)
                operating_band = 2.4;
            else
                operating_band = 5.0;

            // Display on the screen
            Log.d("Number of times=", String.valueOf(SLNO));
            Log.d("\t\tSignal Strength of ", ssid);
            Log.d("\t\tRSSI (dbm) = ", String.valueOf(rssi));
            Log.d("\t\tFrequency=", String.valueOf(frequency));
            Log.d("\t\tLinkspeed=", String.valueOf(Linkspeed));
            Log.d("\t\tRxLinkSpeed=", String.valueOf(RxLinkSpeed));
            Log.d("\t\tTxLinkSpeed=", String.valueOf(TxLinkSpeed));
            Log.d("\t\tOperating band=", String.valueOf(operating_band));
            Log.d("\t\tX=", String.valueOf(x1));
            Log.d("\t\tY=", String.valueOf(y1));


        } catch (Exception e) {
            Toast.makeText(RSSI_xy.this, "Device is not connected to any network", Toast.LENGTH_LONG).show();

        }
    }

    // save all the information in txt file
    // for saving into the file user have to give manual storage and location permissions to app
    public void savetofile() {
        Log.v("Bharti", "entering save file");
        File directory = null;
        // make a directory called WSS
        directory = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "WSS");
        directory.mkdirs();
        Log.v("Bharti", "make directory file");
        // make a file wss.txt
        file = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "WSS" + java.io.File.separator + "WSS.txt");
        System.out.println(file);

        Log.v("Bharti", "flag");
        // if file already exists on system delete that file
        if(flag==true)
        {   Log.v("Bharti", String.valueOf(file));
            file.delete();
            flag=false;
        }
        if (!file.exists()) {
            try {   Log.v("Bharti", String.valueOf(file));
                file.createNewFile();
            } catch (Exception e) {
                Toast.makeText(RSSI_xy.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        uri2 = Uri.parse(String.valueOf(file));
        System.out.println(uri2);

        try {
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file, true));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            if (SLNO == 0) {
                buffered_writer.write("\nNumber of times" + "\tSSID" + "\tRSSI" + "\tX" + "\tY" + "\tFrequency" + "\tLinkSpeed" + "\tRxLinkSpeed" + "\tTxLinkSpeed" + "\toperating_band");
                buffered_writer.write("\n" + SLNO + "\t" + ssid + "\t" + rssi + "\t" + x1 + "\t" + y1 + "\t" + frequency + "\t" + Linkspeed + "\t" + RxLinkSpeed + "\t" + TxLinkSpeed + "\t" + operating_band);
            } else {
                buffered_writer.write("\n" + SLNO + "\t" + ssid + "\t" + rssi + "\t" + x1 + "\t" + y1 + "\t" + frequency + "\t" + Linkspeed + "\t" + RxLinkSpeed + "\t" + TxLinkSpeed + "\t" + operating_band);
            }
            buffered_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // check min. no. to points taken to enable generate heatmap button
    public void checkEnabled() {
        if (i > 4) {
            buttonCal.setEnabled(true);
        }
    }

    //sends request to the server to generate heatmap
    //sends txt file , image choosen , height and width of imageview of phone's screen to the server
    private void addRecord(String filePath) {

        File txtFile = new File(String.valueOf(file));
        File fileImage = new File(filePath);
        Log.d("image path",filePath);
      Log.d("image path", String.valueOf(fileImage));
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), txtFile);
        MultipartBody.Part avatar = MultipartBody.Part.createFormData("txtFile", txtFile.getName(), requestBody);

        RequestBody requestbody = RequestBody.create(MediaType.parse("image/*"), fileImage);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("base64image", fileImage.getName(), requestbody);
        RequestBody Height = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(height));
        RequestBody Width = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(width));
//                RequestBody base64image = RequestBody.create(MediaType.parse("text/plain"), base64);

        apiCall getResponse = apiCall.getRetrofit().create(apiCall.class);   // call the api which is defined in apiCall class interface
//        Call<ResponseBody> call = getResponse.addRecord(avatar,parts);
        Call<ResponseBody> call = getResponse.addRecord(avatar,parts,Height,Width);  // send request to server

        // show a progress bar till we didn't get response from server
        progressBar = new ProgressDialog(RSSI_xy.this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Loading...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(10);
        progressBar.show();
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 500) {
                    // performing operation
                    progressBarStatus += 1;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Updating the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                // performing operation if file is downloaded,
                if (progressBarStatus >= 500) {
                    // sleeping for 1 second after operation completed
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog
                    progressBar.dismiss();
                }
            }
        }).start();

        //accept the response received by server
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        Log.d("Response", "=" + response.code());
//                        Log.d("Response", "= " + response.message());
                Log.d("Response", "= " + response);
                Log.d("Response", "= " + response.body());
                Log.d("Response", "= " + response.getClass());
//                        Log.d("Response", "= " + response.raw());
                if(response.code()==200)
                {
                    if (response.body() != null) {
                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                        HeatmapURI=getImageUri(incontext, bmp);
                        Intent intent = new Intent(RSSI_xy.this,HeatMapActivity.class);   // send the generated heatmap image to HeatMap activity
                        intent.putExtra("HEATMAP_IMAGEVIEW_BITMAP", HeatmapURI.toString());
                        startActivity(intent);
//                                imageView2.setImageBitmap(bmp);
//                                imageView2.setImageURI(HeatmapURI);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("failure", "message = " + t.getMessage());
                Log.d("failure", "cause = " + t.getCause());
            }
        });
    }

    // get image URI from bitmap of image
    public Uri getImageUri(Context inContext, Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bmp, "Title", null);
        return Uri.parse(path);
    }
}




