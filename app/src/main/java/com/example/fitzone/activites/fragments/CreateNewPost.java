package com.example.fitzone.activites.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.fitzone.activites.R;
import com.google.android.material.tabs.TabLayout;

public class CreateNewPost extends Fragment {
    private ViewStub includePostType;

    private FrameLayout frameLayout;
    private TabLayout tabLayout;

    public CreateNewPost() {
        super(R.layout.fragment_create_new_post);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inflate elements
        frameLayout = view.findViewById(R.id.fl_create_new_post);
        tabLayout = view.findViewById(R.id.tl_add_post);

        setFragmentContent(new AddNewTextPost());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateViewTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    //update view
    private void updateViewTab(TabLayout.Tab tab) {
        if (tab.getText().equals(getString(R.string.text_post))) {
            setFragmentContent(new AddNewTextPost());
        } else if (tab.getText().equals(getString(R.string.image_post))) {
            setFragmentContent(new AddNewImagePost());
        } else if (tab.getText().equals(getString(R.string.video_post))) {
            setFragmentContent(new AddNewVideoPost());
        }
    }

    //set new fragment position
    private void setFragmentContent(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_create_new_post, fragment)
                .commit();
    }
}