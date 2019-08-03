package com.mstcvit.barcodescanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.io.IOException;

public class SurfaceActivity extends AppCompatActivity {
    SurfaceView sf;
    CameraSource cs;
    TextView tv;
    BarcodeDetector detector;
    String txtcontent="";
    private static final String TAG = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        sf = findViewById(R.id.camsurfaceview);
        tv = findViewById(R.id.testtv);
        tv.setMovementMethod(new ScrollingMovementMethod());
        detector = new BarcodeDetector.Builder(this).setBarcodeFormats(FirebaseVisionBarcode.FORMAT_CODE_128).build();
        cs = new CameraSource.Builder(getApplicationContext(), detector).setRequestedPreviewSize(1024, 768).setAutoFocusEnabled(true).setRequestedFps(30).build();
        sf.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder)
            {
                if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                try
                {
                    cs.start(surfaceHolder);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
            {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder)
            {
                cs.stop();
            }
        });
        detector.setProcessor(new Detector.Processor<Barcode>()
        {
            @Override
            public void release()
            {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                final SparseArray<Barcode> codes=detections.getDetectedItems();
                if(codes.size()!=0)
                {
                    tv.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            tv.append(codes.valueAt(0).displayValue+" has registered.\n");

                        }
                    });
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    myRef.child(codes.valueAt(0).displayValue).setValue("Registered");
                }
            }
        });
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState: in");
        super.onRestoreInstanceState(savedInstanceState);
        tv.setText(savedInstanceState.getString(txtcontent));
        Log.d(TAG, "onRestoreInstanceState: out");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState: in");
        outState.putString(txtcontent, tv.getText().toString());
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: out");
    }
}