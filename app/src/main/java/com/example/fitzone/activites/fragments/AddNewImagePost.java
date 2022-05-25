package com.example.fitzone.activites.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitzone.activites.R;
import com.example.fitzone.activites.fragments.home_activity_fragments.HomeFragment;
import com.example.fitzone.activites.fragments.home_activity_fragments.ProfileFragment;
import com.example.fitzone.common_functions.RealPathUtil;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.send_post_response.SendPostResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddNewImagePost extends Fragment {

    private static final int SELECT_PHOTO = 100;
    private Button share, selectImage;
    private TextView caption;
    private ImageView content;
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private String picturePath = "";
    private SwipeRefreshLayout swipeRefreshLayout;

    public AddNewImagePost() {
        super(R.layout.fragment_add_new_image_post);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isStoragePermissionGranted()) {
            Toast.makeText(getActivity(), "permission denied to access gallery", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_home_activity, new ProfileFragment())
                    .commit();
        }

        //inflate element
        caption = view.findViewById(R.id.caption);
        content = view.findViewById(R.id.post_image);
        selectImage = view.findViewById(R.id.select_image);
        share = view.findViewById(R.id.sharePost);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_Add_new_post);

        share.setVisibility(View.GONE);

        selectImage.setOnClickListener(this::selectImageFromGallery);

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

    }

    private void selectImageFromGallery(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();


                    picturePath = RealPathUtil.getRealPath(getContext(), selectedImage);

                    Bitmap inputImage = BitmapFactory.decodeFile(picturePath);

                    content.setImageBitmap(inputImage);
                    share.setVisibility(View.VISIBLE);

                    share.setOnClickListener(view1 -> sharePostImage(view1, selectedImage));

                    share.setVisibility(View.VISIBLE);
                }
        }
    }

    private void sharePostImage(View view1, Uri selectedImage) {
        if (apiInterface == null)
            return;

        File file = new File(picturePath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );


        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("content", file.getName(), requestFile);


        Call<SendPostResponse> call = apiInterface.sendFilePost("Bearer " + getApiToken(getActivity()),
                caption.getText().toString(),
                body,
                2);

        swipeRefreshLayout.setRefreshing(true);
        call.enqueue(new Callback<SendPostResponse>() {
            @Override
            public void onResponse(Call<SendPostResponse> call, Response<SendPostResponse> response) {
                Toast.makeText(getActivity(), "added successfully", Toast.LENGTH_SHORT).show();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_home_activity, new HomeFragment())
                        .commit();

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<SendPostResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    //check storage permission
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

}