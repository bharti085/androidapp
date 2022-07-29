package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;


import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//import static com.github.karthyks.runtimepermissions.PermissionActivity.REQUEST_PERMISSION_CODE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.util.Base64;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {
    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private Button permissions;
    MyGestureListener ob = new MyGestureListener();
    ImageView imageView2;
    Uri myUri;
    Uri uri2;
    Button buttonM;
    Button bt;
    Button buttonCal;
    List<ScanResult> results;
    int i = 1;
    //    Button bt;
    Handler handler;
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
    int mynum;
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
    String base64;
    private ProgressDialog progress;
    Uri HeatmapURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        Bundle extras = getIntent().getExtras();
        myUri = Uri.parse(extras.getString("EXTRA_IMAGEVIEW_URL"));
        System.out.println("1");
        System.out.println("1");
        imageView2.setImageURI(myUri);
        System.out.println("1");
        System.out.println("1");
        Log.i("url", String.valueOf(myUri));
        resolver = getContentResolver();
        incontext = getApplicationContext();

        buttonCal = (Button) findViewById(R.id.buttonheatmap);
        progress = new ProgressDialog(MainActivity2.this);
        buttonCal.setEnabled(false);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
//        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        buttonCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                base64=fileUriToBase64(myUri, resolver);
                Log.d("Bharti", base64);
                addRecord();
//                file.delete();
//                SLNO = 0;
//                startActivity(new Intent(MainActivity2.this, HeatMapActivity.class));
            }
        });
    }


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
            Log.d(DEBUG_TAG, "onDown: " + event.toString());

            x = event.getX();
            y = event.getY();
            x1=(int)x;
            y1=(int)y;
            disp();

            savetofile();
            checkEnabled();
            i++;
            SLNO++;
            Toast.makeText(MainActivity2.this, "X: " + x1 + " Y: " + y1 + "  RSSI:" + rssi, Toast.LENGTH_SHORT).show();
            System.out.println("1");

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
//                st10.setText("\n\t\t" + currentSSID + "=" + SLNO);
                count = SLNO;
            } else if (currentSSID.equals("<unknown ssid>")) {
//                st10.setText("\n\t\t" + previousSSID + "=" + (SLNO));

            } else {
                if (count != 0) {
//                    st11.append("\t" + previousSSID + "=" + count);
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
            //st12.setText("result"+result);
//            Toast.makeText(MainActivity2.this,  "RSSI:"+ rssi + "  SSID:" + ssid, Toast.LENGTH_LONG).show();


        } catch (Exception e) {
            Toast.makeText(MainActivity2.this, "Device is not connected to any network", Toast.LENGTH_LONG).show();

        }
    }

    public void savetofile() {
        Log.v("Bharti", "entering save file");
        File directory = null;

        directory = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "WSS");
        directory.mkdirs();
//        if (!directory.exists())
//            Toast.makeText(this, (directory.mkdirs() ? "Directory has been created" : "Directory not created"), Toast.LENGTH_SHORT).show();

        System.out.println(directory);
        file = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "WSS" + java.io.File.separator + "WSS.txt");
        System.out.println(file);


        Date currentTime = Calendar.getInstance().getTime();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        System.out.println("1");
        uri2 = Uri.parse(String.valueOf(file));
        System.out.println(uri2);

        try {
            System.out.println("2");
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file, true));
            System.out.println("3");
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            System.out.println("4");
            if (SLNO == 0) {
                buffered_writer.write("\nNumber of times" + "\tSSID" + "\tRSSI" + "\tX" + "\tY" + "\tFrequency" + "\tLinkSpeed" + "\tRxLinkSpeed" + "\tTxLinkSpeed" + "\toperating_band");
                System.out.println("5");
            } else {
                buffered_writer.write("\n" + SLNO + "\t" + ssid + "\t" + rssi + "\t" + x1 + "\t" + y1 + "\t" + frequency + "\t" + Linkspeed + "\t" + RxLinkSpeed + "\t" + TxLinkSpeed + "\t" + operating_band);
                System.out.println("6");
            }
            System.out.println("7");
            buffered_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fileUriToBase64(Uri uri, ContentResolver resolver) {
        Log.d("bharti","1");
        String encodedBase64 = "";
        try {
            Log.d("bharti","2");
            byte[] bytes = readBytes(uri, resolver);
            Log.d("bharti","3");
            encodedBase64 = Base64.encodeToString(bytes, 0);
            Log.d("bharti","4");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return encodedBase64;
    }

    public static byte[] readBytes(Uri uri, ContentResolver resolver)
            throws IOException {
        // this dynamically extends to take the bytes you read
        Log.d("uri", String.valueOf(uri));
        InputStream inputStream = resolver.openInputStream(uri);
        Log.d("bharti","5");
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        Log.d("bharti","6");

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the
        // byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        Log.d("bharti","7");
        Log.d("bharti","7");
        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }


    public void checkEnabled() {
        if (i > 3) {
            buttonCal.setEnabled(true);
        }
    }

//    private void addTouchListener() {
        private void addRecord() {
//            Log.d("bharti","8");
//            Log.d("bharti", String.valueOf(file));
                File txtFile = new File(String.valueOf(file));
//            Log.d("bharti", String.valueOf(files));
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), txtFile);
//            Log.d("bharti", String.valueOf(requestBody));
//            Log.d("bharti","9");
                MultipartBody.Part avatar = MultipartBody.Part.createFormData("txtFile", txtFile.getName(), requestBody);
//            Log.d("bharti", String.valueOf(avatar));
//            Log.d("bharti","10");
//            String base64data= "data:image/jpeg;base64,"+base64;
                RequestBody base64image = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(base64));
//            Log.d("bharti", String.valueOf(base64image));
//            Log.d("bharti","11");
                apiCall getResponse = apiCall.getRetrofit().create(apiCall.class);
//            Log.d("bharti","12");

//            Log.d("heloo", adi);
                Call<ResponseBody> call = getResponse.addRecord(avatar, base64image);
//            Log.d("bharti","13");

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
                                // display the image data in a ImageView or save it
                                Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                                HeatmapURI=getImageUri(incontext, bmp);
                                Intent intent = new Intent(MainActivity2.this,HeatMapActivity.class);
                                intent.putExtra("HEATMAP_IMAGEVIEW_BITMAP", HeatmapURI.toString());
                                startActivity(intent);
////                                imageView2.setImageBitmap(bmp);
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

    public Uri getImageUri(Context inContext, Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bmp, "Title", null);
        return Uri.parse(path);
    }
//        });
//    }
}


//
//    ;

//    @SuppressLint("ClickableViewAccessibility")
//    private void addTouchListener() {
//
//        imageView2.setOnTouchListener(new View.OnTouchListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//
//                ob.onLongPress(event);
//                return false;
//            }
//        });
//    }
//}


//        imageView2.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
////                final int waitTime = 5000;
//                x = event.getX();
//                y = event.getY();
////                drawCross(x,y);
////                m_canvas.drawCircle(x,y,10,paint);
////                imageView2.setBitmap(bmp);
//                String msg = String.format("Coordinates : (%.2f,%.2f)", x, y);
//                Log.d("coordinates", msg);
//                Toast.makeText(MainActivity2.this, "X: " + x + " Y: " + y, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            });
//        }





