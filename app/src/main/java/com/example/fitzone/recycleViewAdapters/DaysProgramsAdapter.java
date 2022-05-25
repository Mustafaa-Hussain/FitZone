package com.example.fitzone.recycleViewAdapters;

import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fitzone.activites.R;

import org.json.JSONArray;
import org.json.JSONException;

public class DaysProgramsAdapter extends RecyclerView.Adapter<DaysProgramsAdapter.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    Activity activity;
    private DaysProgramsAdapter.ItemClickListener mClickListener;


    // data is passed into the constructor
    public DaysProgramsAdapter(Activity activity, JSONArray arrayOfDays) {
        if (activity == null)
            return;

        this.mInflater = LayoutInflater.from(activity);
        this.mData = arrayOfDays;
        this.activity = activity;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.badge_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.trainingText.setText(mData.getString(position));

            if (mData.getString(position).equals("Day: 1"))
                Glide.with(activity)
                        .load(getHostUrl(activity) + "...")
                        .placeholder(R.drawable._1_train_hard)
                        .centerCrop()
                        .into(holder.trainingImage);
            else if(mData.getString(position).equals("Day: 5"))
                Glide.with(activity)
                        .load(getHostUrl(activity) + "...")
                        .placeholder(R.drawable._5_no_pain_no_gain)
                        .centerCrop()
                        .into(holder.trainingImage);
            else if(mData.getString(position).equals("Day: 3"))
                Glide.with(activity)
                        .load(getHostUrl(activity) + "...")
                        .placeholder(R.drawable._3_par_gym)
                        .centerCrop()
                        .into(holder.trainingImage);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.length();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trainingText;
        ImageView trainingImage;

        ViewHolder(View itemView) {
            super(itemView);
            trainingText = itemView.findViewById(R.id.badge_text);
            trainingImage = itemView.findViewById(R.id.badge_image);

            itemView.findViewById(R.id.badge_item).setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) throws JSONException {
        return (String) mData.get(id);
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