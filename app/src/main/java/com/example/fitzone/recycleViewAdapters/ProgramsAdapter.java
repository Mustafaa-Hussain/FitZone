package com.example.fitzone.recycleViewAdapters;

import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

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
import com.example.fitzone.retrofit_requists.data_models.programs.ProgramsResponse;


public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ViewHolder> {

    private Activity activity;
    private ProgramsResponse myData;
    private LayoutInflater myInflater;
    private ItemClickListener myClickListener;

    public ProgramsAdapter(Activity activity, ProgramsResponse myData) {
        if (activity == null)
            return;

        this.activity = activity;
        myInflater = LayoutInflater.from(activity);
        this.myData = myData;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.myClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView programImage;
        private TextView programName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            programImage = itemView.findViewById(R.id.program_image);
            programName = itemView.findViewById(R.id.program_text);
            itemView.findViewById(R.id.program_item).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myClickListener != null)
                myClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ProgramsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = myInflater.inflate(R.layout.program_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramsAdapter.ViewHolder holder, int position) {
        holder.programName.setText(myData.get(position).getName());
        Glide.with(activity)
                .load(getHostUrl(activity) + "...")//will place image if exist later
                .placeholder(R.drawable.main_exercies)
                .centerCrop()
                .into(holder.programImage);
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }
}
