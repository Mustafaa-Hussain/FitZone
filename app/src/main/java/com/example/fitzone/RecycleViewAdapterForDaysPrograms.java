package com.example.fitzone;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleViewAdapterForDaysPrograms extends RecyclerView.Adapter<RecycleViewAdapterForDaysPrograms.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    Context context;
    private RecycleViewAdapterForDaysPrograms.ItemClickListener mClickListener;

    // data is passed into the constructor
    RecycleViewAdapterForDaysPrograms(Context context, JSONArray arrayOfDays) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = arrayOfDays;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.program_structure, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.day.setText(mData.getString(position));
            //put images in imageview

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
        TextView day;
        ImageView trainingLogo;
        ImageView dayStatus;
        LinearLayout day_layout;

        ViewHolder(View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.dayText);
            trainingLogo = itemView.findViewById(R.id.day_logo);
            dayStatus = itemView.findViewById(R.id.dayStatus);
            day_layout = itemView.findViewById(R.id.day_layout);

            day_layout.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    // convenience method for getting data at click position
    JSONObject getItem(int id) throws JSONException {
        return (JSONObject)mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}