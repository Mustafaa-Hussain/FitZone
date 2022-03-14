package com.example.fitzone.recycleViewAdapters;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.fitzone.activites.R;
import com.example.fitzone.handelers.HandlePost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleViewAdapterForPosts extends RecyclerView.Adapter<RecycleViewAdapterForPosts.ViewHolder> {

    private JSONArray mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    HandlePost handlePost;
    Context context;

    int[] colors;

    // data is passed into the constructor
    public RecycleViewAdapterForPosts(Context context, JSONArray arrayOfPosts, int [] colors) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = arrayOfPosts;
        this.context = context;
        this.colors = colors;
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
//            holder.userAvatar.setImage//;
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

            switch (post.getInt("type")){
                case 0://default post
                    holder.mainPostCV.setCardBackgroundColor(colors[0]);
                    holder.postRatingBar.setVisibility(View.GONE);
                    holder.postImage.setVisibility(View.GONE);
                    holder.postVideo.setVisibility(View.GONE);
                    break;
                case 1://post with rating bar
                    holder.mainPostCV.setCardBackgroundColor(colors[1]);
                    holder.postImage.setVisibility(View.GONE);
                    holder.postVideo.setVisibility(View.GONE);
                    break;
                case 2://post with image
                    holder.mainPostCV.setCardBackgroundColor(colors[2]);
                    holder.postRatingBar.setVisibility(View.GONE);
                    holder.postVideo.setVisibility(View.GONE);
                    break;
                case 3://post with video
                    holder.mainPostCV.setCardBackgroundColor(colors[3]);
                    holder.postRatingBar.setVisibility(View.GONE);
                    holder.postImage.setVisibility(View.GONE);
                    break;
                default://default
                    holder.postRatingBar.setVisibility(View.GONE);
                    holder.postImage.setVisibility(View.GONE);
                    holder.postVideo.setVisibility(View.GONE);
                    break;
            }

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

        ImageView profileImage,
        postImage;
        VideoView postVideo;
        RatingBar postRatingBar;

        CardView mainPostCV;

        Button comment, like, more;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            initDate = itemView.findViewById(R.id.initDate);
            caption = itemView.findViewById(R.id.caption);
            content = itemView.findViewById(R.id.postContent);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
            profileImage = itemView.findViewById(R.id.userAvatar);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            more = itemView.findViewById(R.id.more);

            postRatingBar = itemView.findViewById(R.id.rating_bar_post);
            postImage = itemView.findViewById(R.id.image_post);
            postVideo = itemView.findViewById(R.id.video_post);
            mainPostCV = itemView.findViewById(R.id.main_post_cv);

            like.setOnClickListener(this);
            comment.setOnClickListener(this);
            more.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    // convenience method for getting data at click position
    public JSONObject getItem(int id) throws JSONException {
        return (JSONObject)mData.get(id);
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