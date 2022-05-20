package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fitzone.retrofit_requists.data_models.badges.DataModelBadgesResponseItem;
import com.example.fitzone.retrofit_requists.data_models.badges.Rule;

public class BadgesMoreInfo extends Fragment {

    private DataModelBadgesResponseItem badgeData;

    public BadgesMoreInfo(DataModelBadgesResponseItem dataModelBadgesResponseItem) {
        super(R.layout.fragment_badges_more_info);
        this.badgeData = dataModelBadgesResponseItem;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView badgeImage = view.findViewById(R.id.badge_more_image);
        TextView badgeName = view.findViewById(R.id.badge_more_name);
        TextView badgeDescription = view.findViewById(R.id.badge_more_description);
        TextView badgeRule = view.findViewById(R.id.badge_more_rules);

        //set image
        Glide.with(getActivity())
                .load(getHostUrl(getActivity()) + badgeData.getImage())
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .into(badgeImage);

        //set name
        badgeName.setText(badgeData.getName());

        //set description
        badgeDescription.setText(badgeData.getDescription());

        //set rules
        badgeRule.setText("Rules");
        for(Rule rule: badgeData.getRules()){
            badgeRule.append('\n' + rule.getExercise_name() + " -> " + rule.getCount());
        }

    }
}