package com.jonkoss.oralcancerdetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.content.ContentValues.TAG;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GetImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GetImageFragment extends Fragment {

    @BindView(R.id.take_picture) Button mTakePictureButton;
    @BindView(R.id.choose_picture) Button mChoosePictureButton;
    @BindView(R.id.imageView) ImageView mImageView;
    @BindView(R.id.titleText) TextView mTitleText;

    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_CHOOSE_PICTURE = 2;
    private String mCurrentPhotoPath;

    public String fragmentName;

    public static GetImageFragment newInstance() {
        return new GetImageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (mCurrentPhotoPath == null && savedInstanceState.getString("photo_path") != null) {
                mCurrentPhotoPath = savedInstanceState.getString("photo_path");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_image, container, false);
        ButterKnife.bind(this, view);

        mTitleText.setText(fragmentName);

        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    File photoFile = null;
                    try {

                        photoFile = createImageFile(fragmentName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                        startActivityForResult(intent, REQUEST_TAKE_PICTURE);
                    }
                }
            }
        });

        mChoosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CHOOSE_PICTURE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap_left_cy3, bitmap_right_cy3, bitmap_left_egfp, bitmap_right_egfp;

        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:
                    try {
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        bmp = cropToSquare(bmp);
                        ImageParams imageParams = new ImageParams(fragmentName,bmp);
                        new getAuTask().execute(imageParams);
                        mImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, targetW, targetW, false));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //startDisplayImageActivity();
                    break;
                case REQUEST_CHOOSE_PICTURE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bmp = cropToSquare(BitmapFactory.decodeFile(picturePath));
                    ImageParams imageParams = new ImageParams(fragmentName,bmp);
                    new getAuTask().execute(imageParams);

                    mImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, targetW, targetW, false));
                    break;
                default:
                    break;
            }
        }
    }

    private Bitmap cropToSquare(Bitmap bitmap){

        Log.e(TAG, "WE CROPPING HERE");
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 4128 x 2322

        int cropW;
        int cropH;
        // real
        cropH = (int) Math.floor(height*0.30281008);
        cropW = (int) Math.floor(width*0.09689922);

        //Choose the size of the square to be cropped. Must represent 1mm x 1mm.
        // TODO: Should be made dynamic based on the width and height from above.
        int newHeight = (int) Math.floor(height*0.47238372093);
        int newWidth = (int) Math.floor(width*0.83979328165);

        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
        Log.e(TAG, "width: " +  String.valueOf(cropW) + " height: " + String.valueOf(cropH) + "  new width: " + String.valueOf(newWidth) + "  new height: " + String.valueOf(newHeight));
        return cropImg;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoPath != null) {
            outState.putString("photo_path", mCurrentPhotoPath);
        }
        super.onSaveInstanceState(outState);
    }

    private File createImageFile(String image_descriptor) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = image_descriptor + timeStamp + ".jpg";
        String storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File image = new File(
                storageDir,   // directory
                imageFileName // file
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.e(TAG, mCurrentPhotoPath);
        return image;
    }

    private class getAuTask extends AsyncTask<ImageParams, Void, Double> {
        @Override
        protected Double doInBackground(ImageParams... params) {
            Bitmap bmp = params[0].bmp;
            String type = params[0].type;
            Log.e(TAG,"type: " + type);
            String color_channel = "green";

            switch (type) {
                case "lesion_hbd2":
                    color_channel = "purple";
                    break;
                case "control_hbd2":
                    color_channel = "purple";
                    break;
                case "lesion_hbd3":
                    color_channel = "green";
                    break;
                case "control_hbd3":
                    color_channel = "green";
                    break;
            }

            bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
            int intArray[], greenArray[], redArray[], blueArray[], purpleArray[];
            int greenValue, blueValue, redValue, purpleValue;
            int greenMin = 10000;
            int purpleMin = 10000;
            int greenMax = 0;
            int purpleMax = 0;
            double au, intensity;

            int i;
            int x = bmp.getWidth();
            int y = bmp.getHeight();

            intensity = 0;
            redValue = 0;
            greenValue = 0;
            blueValue = 0;
            purpleValue = 0;


            intArray = new int[x*y];

            bmp.getPixels(intArray, 0, x, 0, 0, x, y);


          greenArray = new int[x*y];
          blueArray = new int[x*y];
          redArray = new int[x*y];
          purpleArray = new int[x*y];
            switch (color_channel) {

                case "green":
                    for (i = 0; i < intArray.length; i++) {
                        greenArray[i] = (intArray[i] >>  8) & 0xff;
                        //greenValue = green(intArray[i]);
                        greenMin = Math.min(greenMin, greenArray[i]);
                        greenMax = Math.max(greenMax, greenArray[i]);
                    }
                    Log.e(TAG, "min" + String.valueOf(greenMin));
                    Log.e(TAG, "max" + String.valueOf(greenMax));
                    for (i = 0; i < intArray.length; i++) {

                        intensity += (((double)greenArray[i] - (double)greenMin)/((double)greenMax - (double)greenMin));
                    }
                    Log.e(TAG, "intensity" + String.valueOf(intensity));
                    break;
                case "blue":
                    for (i = 0; i < intArray.length; i++) {
                        blueValue = blue(intArray[i]);
                        intensity += blueValue;
                    }
                    break;
                case "red":
                    for (i = 0; i < intArray.length; i++) {
                        redValue = red(intArray[i]);
                        intensity += redValue;
                    }
                    break;
                case "purple":
                    for (i = 0; i < intArray.length; i++) {
                        purpleArray[i] = blue(intArray[i]) + red(intArray[i]);
                        purpleMin = Math.min(purpleMin, purpleArray[i]);
                        purpleMax = Math.max(purpleMax, purpleArray[i]);
                    }

                    for (i = 0; i < intArray.length; i++) {

                        intensity += (((double)purpleArray[i] - (double)purpleMin)/((double)purpleMax - (double)purpleMin));
                    }
                    break;
            }

            au = intensity/intArray.length;

            switch (type){

                case "lesion_hbd2":
                    DataHolder.au_lesion_hbd2  = au;
                    Log.e(TAG,DataHolder.au_lesion_hbd2.toString());
                    break;
                case "control_hbd2":
                    Log.e(TAG,type);
                    DataHolder.au_control_hbd2 = au;
                    Log.e(TAG,DataHolder.au_control_hbd2.toString());
                    break;
                case "lesion_hbd3":
                    DataHolder.au_lesion_hbd3 = au;
                    Log.e(TAG,DataHolder.au_lesion_hbd3.toString());
                    break;
                case "control_hbd3":
                    DataHolder.au_control_hbd3 = au;
                    Log.e(TAG,DataHolder.au_control_hbd3.toString());
                    break;
            }
            return au;
        }
    }

    private static class ImageParams {
        String type;
        Bitmap bmp;

        ImageParams(String type, Bitmap bmp){
            this.type = type;
            this.bmp = bmp;
        }
    }
}
