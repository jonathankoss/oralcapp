package com.jonkoss.oralcancerdetection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.PlusOneButton;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static java.lang.Double.valueOf;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class ProcessImagesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    @BindView(R.id.process_images)Button mChoosePictureButton;
    @BindView(R.id.resultsText) TextView mResultsText;

    public ProcessImagesFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_process_images, container, false);
        ButterKnife.bind(this, view);

        mChoosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (DataHolder.au_lesion_hbd2 == null || DataHolder.au_control_hbd2 == null || DataHolder.au_lesion_hbd3 == null || DataHolder.au_control_hbd3 == null) {
                    final Dialog fbDialogue = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
                    fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                    fbDialogue.setContentView(R.layout.missing_picture_popup);
                    fbDialogue.setCancelable(true);
                    Button bt_close = (Button)fbDialogue.findViewById(R.id.ib_close);
                    bt_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fbDialogue.dismiss();
                        }
                    });

                    fbDialogue.show();
                } else {
                    Log.e(TAG,DataHolder.au_lesion_hbd2.toString());
                    Log.e(TAG,DataHolder.au_lesion_hbd3.toString());
                    Log.e(TAG,DataHolder.au_control_hbd2.toString());
                    Log.e(TAG,DataHolder.au_control_hbd3.toString());
                    double BDI = (DataHolder.au_lesion_hbd3/DataHolder.au_lesion_hbd2)/(DataHolder.au_control_hbd3/DataHolder.au_control_hbd2);
                    mResultsText.setText("BDI: " + String.format("%.2f", BDI));
                    //mResultsText.setText(String.format("%.0f", DataHolder.au_lesion_hbd3) + ", " + String.format("%.0f", DataHolder.au_lesion_hbd2) + ", " + String.format("%.0f", DataHolder.au_control_hbd3) + ", " + String.format("%.0f", DataHolder.au_control_hbd2));

                }
            }
        });
        return view;
    }
}
