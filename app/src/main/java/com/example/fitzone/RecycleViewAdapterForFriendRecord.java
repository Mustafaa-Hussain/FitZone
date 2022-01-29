package com.example.fitzone;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleViewAdapterForFriendRecord extends RecyclerView.Adapter<RecycleViewAdapterForFriendRecord.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    Context context;

    // data is passed into the constructor
    RecycleViewAdapterForFriendRecord(Context context, JSONArray arrayOfPosts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = arrayOfPosts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.friend_record, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            String friendData = mData.getString(position);
            holder.username.setText(friendData);
            //put image in imageview

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
        TextView username;
        ImageView userAvatar;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
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