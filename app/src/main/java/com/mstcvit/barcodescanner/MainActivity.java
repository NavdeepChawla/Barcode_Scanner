package com.mstcvit.barcodescanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton camera=findViewById(R.id.camerafb);
        View.OnClickListener c=new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(getBaseContext(),SurfaceActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        };
        camera.setOnClickListener(c);
        /*Button test=findViewById(R.id.hi);
        View.OnClickListener a=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        };
        test.setOnClickListener(a);*/
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == RESULT_OK)
        {
            bitmap = (Bitmap) data.getExtras().get("data");
            FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(FirebaseVisionBarcode.FORMAT_CODE_128).build();
            FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>()
                    {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> barcodes)
                        {
                            for (FirebaseVisionBarcode barcode: barcodes)
                            {
                                String rawValue = barcode.getRawValue();
                                if(rawValue!=null)
                                {
                                    TextView tv=findViewById(R.id.textView);
                                    tv.append(rawValue+" has Registered.\n");
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Try Again",Toast.LENGTH_LONG).show();
                            // Task failed with an exception
                            // ...
                        }
                    });

        }

    }
    */
}
