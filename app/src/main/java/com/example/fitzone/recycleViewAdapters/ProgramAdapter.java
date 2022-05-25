package com.example.fitzone.recycleViewAdapters;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.fitzone.activites.R;
import com.example.fitzone.retrofit_requists.data_models.day_training_program.DayTrainingProgram;
import com.example.fitzone.retrofit_requists.data_models.day_training_program.DayTrainingProgramItem;

import org.json.JSONException;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {

    private DayTrainingProgram myData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity activity;

    // data is passed into the constructor
    public ProgramAdapter(Activity activity, DayTrainingProgram arrayOfPrograms) {
        if (activity == null)
            return;

        this.mInflater = LayoutInflater.from(activity);
        this.myData = arrayOfPrograms;
        this.activity = activity;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.training_strucure, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.programName.setText(myData.get(position).getExercise_name());
        holder.programNo.setText(myData.get(position).getReps() + " X " + myData.get(position).getSets());

        //check and assign image to each training
        if (myData.get(position).getExercise_name().equals("squat"))
            holder.programImage.setImageResource(R.drawable.static_squat);
        else if (myData.get(position).getExercise_name().equals("push up"))
            holder.programImage.setImageResource(R.drawable.static_pushups);
        else
            holder.programImage.setImageResource(R.drawable.blank_training);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return myData.size();
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
    public DayTrainingProgramItem getItem(int id) throws JSONException {
        return myData.get(id);
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