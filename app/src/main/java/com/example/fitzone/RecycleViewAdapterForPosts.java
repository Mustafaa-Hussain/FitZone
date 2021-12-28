package com.example.fitzone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleViewAdapterForPosts extends RecyclerView.Adapter<RecycleViewAdapterForPosts.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    HandlePost handlePost;
    Context context;

    // data is passed into the constructor
    RecycleViewAdapterForPosts(Context context, JSONArray arrayOfPosts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = arrayOfPosts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_structure_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject post = mData.getJSONObject(position);
            holder.username.setText(post.getString("username"));
//            holder.userAvatar.setImage;);
            holder.initDate.setText(post.getString("created_at").substring(0, post.getString("created_at").indexOf('T')));
            holder.caption.setText(post.getString("caption"));
            holder.content.setText(post.getString("content"));


            holder.noOfLikes.setText(post.getString("number_of_likes"));
            holder.noOfComments.setText(post.getString("number_of_comments"));

            //to handle post by it's id
            holder.like.setHint(post.getString("liked"));

            handlePost = new HandlePost(context);
            handlePost.setPostData(post);

            handlePost.setLikeButtonState(holder.like,post.getString("id"));

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
        TextView username,
        initDate,
        caption,
        content,
        noOfLikes,
        noOfComments;

        ImageView userAvatar;

        Button comment, like;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            initDate = itemView.findViewById(R.id.initDate);
            caption = itemView.findViewById(R.id.caption);
            content = itemView.findViewById(R.id.postContent);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
            //profileImage = itemView.findViewById(R.id.userAvatar);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);

            like.setOnClickListener(this);
            comment.setOnClickListener(this);

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