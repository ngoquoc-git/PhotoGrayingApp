package com.example.photograying;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static String MA = "1";
    Button takePhoto;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private ImageView imageView;
    private BitmapGrayer grayer;
    private Bitmap bitmap;
    private SeekBar redBar, greenBar, blueBar;
    private TextView redTV, greenTV, blueTV;
    Uri uri;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity","hello");
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.picture);
        redBar = (SeekBar) findViewById(R.id.red_bar);
        greenBar = (SeekBar) findViewById(R.id.green_bar);
        blueBar = (SeekBar) findViewById(R.id.blue_bar);
        redTV = (TextView) findViewById(R.id.red_tv);
        greenTV = (TextView) findViewById(R.id.green_tv);
        blueTV = (TextView) findViewById(R.id.blue_tv);
        GrayChangeHandler gch = new GrayChangeHandler();
        redBar.setOnSeekBarChangeListener(gch);
        greenBar.setOnSeekBarChangeListener(gch);
        blueBar.setOnSeekBarChangeListener(gch);
        activityResultLauncher = registerForActivityResult(new
                ActivityResultContracts.StartActivityForResult(), new
                ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle bundle = result.getData().getExtras();
                            bitmap = (Bitmap) bundle.get("data");
                            grayer = new BitmapGrayer(bitmap,0.34f,0.33f,0.33f);
                            bitmap = grayer.grayScale();
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });
        PackageManager manager = this.getPackageManager();
        if (manager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            Toast.makeText(MainActivity.this, "There is A CAMERA",
                    Toast.LENGTH_SHORT).show();
        takePhoto = findViewById(R.id.take_Photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                } else { Toast.makeText(MainActivity.this, "There " +
                                "is no app that support this action",
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class GrayChangeHandler
            implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar,
                                      int progress, boolean fromUser) {
            if (fromUser) {
                if (seekBar == redBar) {
                    grayer.setRedCoeff(progress / 100.0f);
                    redBar.setProgress((int) (100 * grayer.getRedCoeff()));
                    redTV.setText( "" + MathRounding.keepTwoDigits(grayer.getRedCoeff( ) ) );
                } else if (seekBar == greenBar) {
                    grayer.setGreenCoeff(progress / 100.0f);
                    greenBar.setProgress((int) (100 * grayer.getGreenCoeff()));
                    greenTV.setText( "" + MathRounding.keepTwoDigits(
                            grayer.getGreenCoeff( ) ) );
                } else if (seekBar == blueBar) {
                    grayer.setBlueCoeff(progress / 100.0f);
                    blueBar.setProgress((int) (100 * grayer.getBlueCoeff()));
                    blueTV.setText( ""+ MathRounding.keepTwoDigits(
                            grayer.getBlueCoeff( ) ) );
                }
                bitmap = grayer.grayScale();
                imageView.setImageBitmap(bitmap);
            }
        }
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
    public void send(View view) {
        try {
            File file = StorageUtility.writeToExternalStorage(this, bitmap);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("image/png");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "Photo sent from my Android");
            Uri uri = FileProvider.getUriForFile(MainActivity.this,
                    "com.android.example.photo.fileprovider", file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Share your picture"));
            Toast.makeText(this, "EMAIL PICTURE",
                    Toast.LENGTH_LONG).show();
        } catch (IOException ioe) {
            Toast.makeText(this, ioe.getMessage()
                    + "; could not send it", Toast.LENGTH_LONG).show();
        }
    }
}