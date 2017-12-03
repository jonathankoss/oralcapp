package com.jonkoss.oralcancerdetection;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MyActivity";
    private FragmentManager fragmentManager;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        GetImageFragment fragment_left_cy3 = (GetImageFragment) manager.findFragmentById(R.id.get_image_fragment_left_cy3);
        fragment_left_cy3.fragmentName = "lesion_hbd2";
        fragment_left_cy3.mTitleText.setText("Lesion HBD2");

        GetImageFragment fragment_left_egfp = (GetImageFragment) manager.findFragmentById(R.id.get_image_fragment_left_egfp);
        fragment_left_egfp.fragmentName = "control_hbd2";
        fragment_left_egfp.mTitleText.setText("Control HBD2");

        GetImageFragment fragment_right_cy3 = (GetImageFragment) manager.findFragmentById(R.id.get_image_fragment_right_cy3);
        fragment_right_cy3.fragmentName = "lesion_hbd3";
        fragment_right_cy3.mTitleText.setText("Lesion HBD3");

        GetImageFragment fragment_right_egfp = (GetImageFragment) manager.findFragmentById(R.id.get_image_fragment_right_egfb);
        fragment_right_egfp.fragmentName = "control_hbd3";
        fragment_right_egfp.mTitleText.setText("Control HBD3");
    }

    private File createImageFile(int imageType) throws IOException {
        // Create an image file name

        String timeStamp = "";

        if (imageType == 1) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
