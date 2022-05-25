package com.example.fitzone.recycleViewAdapters;


import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitzone.activites.R;
import com.example.fitzone.retrofit_requists.data_models.challenges_response.Challenge;

import java.util.List;

public class CompetitionsAdapter extends RecyclerView.Adapter<CompetitionsAdapter.ViewHolder> {
    private ItemClickListener mClickListener;
    private LayoutInflater mInflater;
    private List<Challenge> myData;
    private Activity activity;

    public CompetitionsAdapter(Activity activity, List<Challenge> challenges) {
        if (activity == null)
            return;

        this.activity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.myData = challenges;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.competition_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CompetitionsAdapter.ViewHolder holder, int position) {
        holder.username.setText(myData.get(position).getCreator_name());
        holder.exerciseName.setText(myData.get(position).getExercise_name());
        holder.training_reps.setText("" + myData.get(position).getReps());

        Glide.with(activity)
                .load(getHostUrl(activity) + myData.get(position).getUser_avatar())
                .circleCrop()
                .placeholder(R.drawable.loading_spinner)
                .into(holder.userAvatar);
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userAvatar;
        TextView username,
                exerciseName,
                training_reps;

        ViewHolder(View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.creator_avatar);
            username = itemView.findViewById(R.id.creator_name);
            exerciseName = itemView.findViewById(R.id.training_name);
            training_reps = itemView.findViewById(R.id.training_number);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
