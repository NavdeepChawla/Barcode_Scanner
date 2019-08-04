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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.io.IOException;
import java.util.ArrayList;

public class SurfaceActivity extends AppCompatActivity {
    ArrayList<Data> a=new ArrayList<>();
    int Reg=0;
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
                    Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    //myRef.child(codes.valueAt(0).displayValue).setValue("Registered");
                    myRef.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                Data d=new Data();
                                d.Key=ds.getKey();
                                d.Value=ds.getValue().toString();
                                a.add(d);
                            }
                            Reg=0;
                            for (int i = 0; i <a.size() ; i++)
                            {
                                Data d=new Data();
                                d=a.get(i);
                                if(d.Key.equals(codes.valueAt(0).displayValue))
                                {
                                    Reg=1;
                                    d.Value="Has Registered";
                                    a.set(i,d);
                                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                                    mRef.child(d.Key).setValue("Has Registered");
                                    break;
                                }
                            }
                            if(Reg==0)
                            {
                                Toast.makeText(getApplicationContext(),codes.valueAt(0).displayValue+" has not registered.",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                tv.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        tv.append(codes.valueAt(0).displayValue+" has registered.\n");

                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {
                        }
                    });
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