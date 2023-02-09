package com.miste.one.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.miste.one.R;
import com.miste.one.adapter.ImageUploadPreviewAdapter;
import com.miste.one.custom.image_compressor.Compressor;
import com.miste.one.custom.image_compressor.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class StudioFragment extends Fragment {


    private Button addimagesbutton;
    private RecyclerView changeImageImagePreviewRecyclerview;
    private EditText pccassionEdittext;
    private EditText budgetEdittext;
    private EditText chestSizeEdittext;
    private Button saveChangesButton;



    private ImageUploadPreviewAdapter image_previewAdapter;
    private File adsimages_actualImage,adsimages_compressedImage;
    private ArrayList<String> listofdownloadUri = new ArrayList<>();

    private ArrayList<Image> images_picker_images = new ArrayList<>();
    private ArrayList<Uri> selected = new ArrayList<>();


    View myView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView =  inflater.inflate(R.layout.fragment_studio, container, false);

        addimagesbutton = (Button) myView.findViewById(R.id.addimagesbutton);
        changeImageImagePreviewRecyclerview = (RecyclerView) myView.findViewById(R.id.changeImage_imagePreviewRecyclerview);
        pccassionEdittext = (EditText) myView.findViewById(R.id.pccassionEdittext);
        budgetEdittext = (EditText) myView.findViewById(R.id.budgetEdittext);
        chestSizeEdittext = (EditText) myView.findViewById(R.id.chestSizeEdittext);
        saveChangesButton = (Button) myView.findViewById(R.id.saveChangesButton);

        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addimagesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(StudioFragment.this).returnMode(ReturnMode.NONE).folderMode(true).toolbarFolderTitle("Folder").toolbarImageTitle("Tap to select").toolbarArrowColor(Color.WHITE).multi().limit(10).imageDirectory("Camera").start();

            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images_picker_images = (ArrayList<Image>) ImagePicker.getImages(data);

            for (int i = 0; i < images_picker_images.size(); i++) {
                String imagePath;
                imagePath = images_picker_images.get(i).getPath();
                Uri uri = Uri.parse(ConvertToUri2(imagePath));

                try {
                    adsimages_actualImage = FileUtil.from(getActivity(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    adsimages_compressedImage = new Compressor(getActivity())
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(30)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToFile(adsimages_actualImage);

                } catch (IOException e) {
                    e.printStackTrace();

                }
                Uri uri1 = Uri.fromFile(adsimages_compressedImage);

                selected.add(uri1);
                image_previewAdapter = new ImageUploadPreviewAdapter(selected,getActivity());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
                changeImageImagePreviewRecyclerview.setLayoutManager(gridLayoutManager);
                changeImageImagePreviewRecyclerview.setHasFixedSize(true);
                changeImageImagePreviewRecyclerview.setAdapter(image_previewAdapter);
                image_previewAdapter.notifyDataSetChanged();
                changeImageImagePreviewRecyclerview.setVisibility(View.VISIBLE);

            }

        }
    }

    private String ConvertToUri2 (String path)
    {
        File f = new File(path);
        Uri yourUri = Uri.fromFile(f);
        return yourUri.toString();

    }
}