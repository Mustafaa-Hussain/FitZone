package com.example.fitzone.recycleViewAdapters;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.fitzone.activites.R;
import com.example.fitzone.handelers.HandlePost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleViewAdapterForPosts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = mInflater.inflate(R.layout.post_structure_row_basic, parent, false);
                return new ViewHolderBasic(view);
            case 1:
                view = mInflater.inflate(R.layout.post_structure_row_rating, parent, false);
                return new ViewHolderRating(view);
            case 2:
                view = mInflater.inflate(R.layout.post_structure_row_image, parent, false);
                return new ViewHolderImage(view);
            case 3:
                view = mInflater.inflate(R.layout.post_structure_row_video, parent, false);
                return new ViewHolderVideo(view);
            default:
                return new ViewHolderBasic(null);
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            JSONObject post = mData.getJSONObject(position);

            switch (getItemViewType(position)){
                case 0://this case for basic post
                    ViewHolderBasic viewHolderBasic = (ViewHolderBasic)holder;
                    viewHolderBasic.username.setText(post.getString("username"));
//            viewHolderBasic.userAvatar.setImage//;
                    viewHolderBasic.initDate.setText(post.getString("created_at").substring(0, post.getString("created_at").indexOf('T')));
                    viewHolderBasic.caption.setText(post.getString("caption"));
                    viewHolderBasic.content.setText(post.getString("content"));

                    viewHolderBasic.noOfLikes.setText(post.getString("number_of_likes"));
                    viewHolderBasic.noOfComments.setText(post.getString("number_of_comments"));

                    //to handle post by it's id
                    viewHolderBasic.like.setHint(post.getString("liked"));

                    handlePost = new HandlePost(context);
                    handlePost.setPostData(post);

                    handlePost.setLikeButtonState(viewHolderBasic.like,post.getString("id"));

                    break;
                case 1://this case for rating post
                    ViewHolderRating viewHolderRating = (ViewHolderRating) holder;
                    viewHolderRating.username.setText(post.getString("username"));
//            viewHolderRating.userAvatar.setImage//;
                    viewHolderRating.initDate.setText(post.getString("created_at").substring(0, post.getString("created_at").indexOf('T')));
                    viewHolderRating.caption.setText(post.getString("caption"));
                    viewHolderRating.content.setText(post.getString("content"));

                    viewHolderRating.noOfLikes.setText(post.getString("number_of_likes"));
                    viewHolderRating.noOfComments.setText(post.getString("number_of_comments"));

                    //to handle post by it's id
                    viewHolderRating.like.setHint(post.getString("liked"));

                    handlePost = new HandlePost(context);
                    handlePost.setPostData(post);

                    handlePost.setLikeButtonState(viewHolderRating.like,post.getString("id"));

                    break;
                case 2://this case for image post
                    ViewHolderImage viewHolderImage = (ViewHolderImage) holder;
                    viewHolderImage.username.setText(post.getString("username"));
//            viewHolderImage.userAvatar.setImage//;
                    viewHolderImage.initDate.setText(post.getString("created_at").substring(0, post.getString("created_at").indexOf('T')));
                    viewHolderImage.caption.setText(post.getString("caption"));
                    viewHolderImage.content.setText(post.getString("content"));

                    viewHolderImage.noOfLikes.setText(post.getString("number_of_likes"));
                    viewHolderImage.noOfComments.setText(post.getString("number_of_comments"));

                    //to handle post by it's id
                    viewHolderImage.like.setHint(post.getString("liked"));

                    handlePost = new HandlePost(context);
                    handlePost.setPostData(post);

                    handlePost.setLikeButtonState(viewHolderImage.like,post.getString("id"));

                    break;
                case 3://this case for video post
                    ViewHolderVideo viewHolderVideo = (ViewHolderVideo) holder;
                    viewHolderVideo.username.setText(post.getString("username"));
//            viewHolderVideo.userAvatar.setImage//;
                    viewHolderVideo.initDate.setText(post.getString("created_at").substring(0, post.getString("created_at").indexOf('T')));
                    viewHolderVideo.caption.setText(post.getString("caption"));
                    viewHolderVideo.content.setText(post.getString("content"));

                    viewHolderVideo.noOfLikes.setText(post.getString("number_of_likes"));
                    viewHolderVideo.noOfComments.setText(post.getString("number_of_comments"));

                    //to handle post by it's id
                    viewHolderVideo.like.setHint(post.getString("liked"));

                    handlePost = new HandlePost(context);
                    handlePost.setPostData(post);

                    handlePost.setLikeButtonState(viewHolderVideo.like,post.getString("id"));

                    //set video to video view
                    //this path for test only or for default video
                    String videoPath = "android.resource://"+ context.getPackageName() + "/" + R.raw.push_ups_with_music;
//                    videoPath = "http://" + "192.168.1.2" + ':' + "8000" + "";
                    Uri uri = Uri.parse(videoPath);
                    viewHolderVideo.postVideo.setVideoURI(uri);
                    MediaController mediaController = new MediaController(context);
                    viewHolderVideo.postVideo.setMediaController(mediaController);
                    mediaController.setAnchorView(viewHolderVideo.postVideo);

                    break;
                default:
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


    // post basic holder
    public class ViewHolderBasic extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username,
                initDate,
                caption,
                content,
                noOfLikes,
                noOfComments;

        ImageView profileImage;

        Button comment, like, more;

        ViewHolderBasic(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            initDate = itemView.findViewById(R.id.initDate);
            caption = itemView.findViewById(R.id.caption_basic);
            content = itemView.findViewById(R.id.postContent_basic);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
            profileImage = itemView.findViewById(R.id.userAvatar);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            more = itemView.findViewById(R.id.more);

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

    // post rating holder
    public class ViewHolderRating extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username,
                initDate,
                caption,
                content,
                noOfLikes,
                noOfComments;

        ImageView profileImage;
        RatingBar postRatingBar;

        Button comment, like, more;

        ViewHolderRating(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            initDate = itemView.findViewById(R.id.initDate);
            caption = itemView.findViewById(R.id.caption_rating);
            content = itemView.findViewById(R.id.postContent_rating);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
            profileImage = itemView.findViewById(R.id.userAvatar);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            more = itemView.findViewById(R.id.more);

            postRatingBar = itemView.findViewById(R.id.rating_bar_post_rating);

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

    // post image holder
    public class ViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username,
                initDate,
                caption,
                content,
                noOfLikes,
                noOfComments;

        ImageView profileImage,
                postImage;

        Button comment, like, more;

        ViewHolderImage(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            initDate = itemView.findViewById(R.id.initDate);
            caption = itemView.findViewById(R.id.caption_image);
            content = itemView.findViewById(R.id.postContent_image);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
            profileImage = itemView.findViewById(R.id.userAvatar);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            more = itemView.findViewById(R.id.more);

            postImage = itemView.findViewById(R.id.image_post_image);

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

    // post video holder
    public class ViewHolderVideo extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username,
                initDate,
                caption,
                content,
                noOfLikes,
                noOfComments;

        ImageView profileImage;

        VideoView postVideo;

        Button comment, like, more;

        ViewHolderVideo(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            initDate = itemView.findViewById(R.id.initDate);
            caption = itemView.findViewById(R.id.caption_video);
            content = itemView.findViewById(R.id.postContent_video);
            noOfLikes = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
            profileImage = itemView.findViewById(R.id.userAvatar);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            more = itemView.findViewById(R.id.more);

            postVideo = itemView.findViewById(R.id.video_post_video);

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

    //get item type by it's position
    @Override
    public int getItemViewType(int position) {
        try {
            return mData.getJSONObject(position).getInt("type");
        } catch (JSONException e) {
            return -1;
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}