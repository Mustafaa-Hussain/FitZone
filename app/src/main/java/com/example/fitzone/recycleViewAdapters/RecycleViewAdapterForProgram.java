package com.example.fitzone.recycleViewAdapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.fitzone.activites.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleViewAdapterForProgram extends RecyclerView.Adapter<RecycleViewAdapterForProgram.ViewHolder> {

    private JSONArray myPrograms;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    // data is passed into the constructor
    public RecycleViewAdapterForProgram(Context context, JSONArray arrayOfPrograms) {
        this.mInflater = LayoutInflater.from(context);
        this.myPrograms = arrayOfPrograms;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.training_strucure, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject training = myPrograms.getJSONObject(position);
            holder.programName.setText(training.getString("TName"));
            holder.programNo.setText(training.getString("TReps") + " X " + training.getString("TSets"));

            //check and assign image to each training
            if(training.getString("TName").equals(context.getString(R.string.squat)))
                holder.programImage.setImageResource(R.drawable.static_squat);
            else if(training.getString("TName").equals(context.getString(R.string.push_ups)))
                holder.programImage.setImageResource(R.drawable.static_pushups);
            else
                holder.programImage.setImageResource(R.drawable.blank_training);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return myPrograms.length();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView programName, programNo;
        ImageView programImage;
        Button start;

        ViewHolder(View itemView) {
            super(itemView);
            programName = itemView.findViewById(R.id.training_name);
            programNo = itemView.findViewById(R.id.no_of_times);
            programImage = itemView.findViewById(R.id.training_image);
            start = itemView.findViewById(R.id.start);

            start.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    // convenience method for getting data at click position
    public JSONObject getItem(int id) throws JSONException {
        return (JSONObject) myPrograms.get(id);
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