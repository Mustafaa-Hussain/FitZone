package com.example.fitzone.recycleViewAdapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitzone.activites.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    Context context;

    // data is passed into the constructor
    public CommentsAdapter(Context context, JSONArray arrayOfPosts) {
        if (context == null)
            return;

        this.mInflater = LayoutInflater.from(context);
        this.mData = arrayOfPosts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comment_structure, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject comment = mData.getJSONObject(position);
            holder.username.setText(comment.getString("username"));
            holder.initDate.setText(comment.getString("created_at").substring(0, comment.getString("created_at").indexOf('T')));
            holder.content.setText(comment.getString("content"));

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
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username,
                initDate,
                content;

        ImageView userAvatar;


        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            initDate = itemView.findViewById(R.id.initDate);
            content = itemView.findViewById(R.id.commentContent);


        }
    }
    // convenience method for getting data at click position
    JSONObject getItem(int id) throws JSONException {
        return (JSONObject)mData.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}