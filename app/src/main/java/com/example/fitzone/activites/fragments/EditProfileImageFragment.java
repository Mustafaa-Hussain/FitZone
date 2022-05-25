package com.example.fitzone.activites.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;
import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

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

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fitzone.activites.R;
import com.example.fitzone.activites.fragments.home_activity_fragments.ProfileFragment;
import com.example.fitzone.common_functions.RealPathUtil;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.change_image_profile.ChangeImageProfileResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileImageFragment extends Fragment {
    private static final int SELECT_PHOTO = 100;
    private ImageView imageView;
    private Button share, selectImage;
    private String userAvatarLink;

    private String picturePath = "";

    private Retrofit retrofit;
    private ApiInterface apiInterface;

    public EditProfileImageFragment(String userAvatarLink) {
        super(R.layout.fragment_edit_profile_image);
        this.userAvatarLink = userAvatarLink;

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
        imageView = view.findViewById(R.id.post_image);
        selectImage = view.findViewById(R.id.select_image);
        share = view.findViewById(R.id.share_image_button_edit_profile);

        Glide.with(getActivity())
                .load(getHostUrl(getActivity()) + userAvatarLink)
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.loading_spinner)
                .into(imageView);

        share.setVisibility(View.GONE);

        selectImage.setOnClickListener(this::selectImageFromGallery);

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(getActivity()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    private void shareSelectedImage(View view, Uri uriFile) {

        //share image
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
                MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

        Call<ChangeImageProfileResponse> call = apiInterface.changeImageProfile("Bearer " + getApiToken(getActivity()), body);
        call.enqueue(new Callback<ChangeImageProfileResponse>() {
            @Override
            public void onResponse(Call<ChangeImageProfileResponse> call, Response<ChangeImageProfileResponse> response) {

                if(response.body() == null)
                    return;

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fl_home_activity, new ProfileFragment())
                        .commit();
            }

            @Override
            public void onFailure(Call<ChangeImageProfileResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

                    imageView.setImageBitmap(inputImage);
                    share.setVisibility(View.VISIBLE);

                    share.setOnClickListener(view1 -> shareSelectedImage(view1, selectedImage));
                }
        }
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