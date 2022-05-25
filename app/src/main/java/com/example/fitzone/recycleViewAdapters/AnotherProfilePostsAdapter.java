package com.example.fitzone.recycleViewAdapters;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;
import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitzone.activites.R;
import com.example.fitzone.handelers.HandlePost;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.user_data.Post;
import com.example.fitzone.retrofit_requists.data_models.user_data.UserDataResponse;

import java.util.HashMap;
import java.util.List;

import cn.jzvd.JzvdStd;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnotherProfilePostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private HandlePost handlePost;
    private Activity activity;

    private HashMap<String, Integer> userLevelWithTag = new HashMap<>();
    private MutableLiveData<HashMap<String, Integer>> userLevel = new MutableLiveData();
    private Retrofit retrofit;
    private ApiInterface apiInterface;

    Integer[] colors;

    // data is passed into the constructor
    public AnotherProfilePostsAdapter(Activity activity, List<Post> arrayOfPosts) {
        if (activity == null)
            return;

        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(activity))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        this.mInflater = LayoutInflater.from(activity);
        this.mData = arrayOfPosts;
        this.activity = activity;

        this.colors = new Integer[]{
                activity.getResources().getColor(R.color.regular_post_gray),
                activity.getResources().getColor(R.color.achievement_post_yellow),
                activity.getResources().getColor(R.color.image_post),
                activity.getResources().getColor(R.color.video_post)
        };
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
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post post = mData.get(position);


        switch (getItemViewType(position)) {
            case 0://this case for basic post
                ViewHolderBasic viewHolderBasic = (ViewHolderBasic) holder;
                viewHolderBasic.username.setText(post.getUsername());


                viewHolderBasic.level.append(" " + post.getUser_level());

                //fill avatar image
                Glide.with(activity)
                        .load(getHostUrl(activity) + post.getUser_avatar())
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .into(viewHolderBasic.profileImage);

                viewHolderBasic.initDate.setText(post.getCreated_at().substring(0, post.getCreated_at().indexOf('T')));
                viewHolderBasic.caption.setText(post.getCaption());
                viewHolderBasic.content.setText(post.getContent());

                viewHolderBasic.noOfLikes.setText(String.valueOf(post.getNumber_of_likes()));
                viewHolderBasic.noOfComments.setText(String.valueOf(post.getNumber_of_comments()));

                //to handle post by it's id
                viewHolderBasic.like.setHint(String.valueOf(post.getLiked()));

                handlePost = new HandlePost(activity);

                handlePost.setLikeButtonState(viewHolderBasic.like, post.getLiked());

                viewHolderBasic.more.setVisibility(View.GONE);

                break;
            case 1://this case for rating post
                ViewHolderRating viewHolderRating = (ViewHolderRating) holder;
                viewHolderRating.username.setText(post.getUsername());

                viewHolderRating.level.append(" " + post.getUser_level());

                //fill avatar image
                Glide.with(activity)
                        .load(getHostUrl(activity) + post.getUser_avatar())
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .into(viewHolderRating.profileImage);

                viewHolderRating.initDate.setText(post.getCreated_at().substring(0, post.getCreated_at().indexOf('T')));
                viewHolderRating.caption.setText(post.getCaption());
                viewHolderRating.content.setText(post.getContent());

                viewHolderRating.noOfLikes.setText(String.valueOf(post.getNumber_of_likes()));
                viewHolderRating.noOfComments.setText(String.valueOf(post.getNumber_of_comments()));

                //to handle post by it's id
                viewHolderRating.like.setHint(String.valueOf(post.getLiked()));

                handlePost = new HandlePost(activity);

                handlePost.setLikeButtonState(viewHolderRating.like, post.getLiked());

                viewHolderRating.more.setVisibility(View.GONE);

                break;
            case 2://this case for image post
                ViewHolderImage viewHolderImage = (ViewHolderImage) holder;
                viewHolderImage.username.setText(post.getUsername());

                viewHolderImage.level.append(" " + post.getUser_level());

                //fill avatar image
                Glide.with(activity)
                        .load(getHostUrl(activity) + post.getUser_avatar())
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .into(viewHolderImage.profileImage);

                viewHolderImage.initDate.setText(post.getCreated_at().substring(0, post.getCreated_at().indexOf('T')));
                viewHolderImage.caption.setText(post.getCaption());
                viewHolderImage.content.setVisibility(View.GONE);

                //fill post image
                Glide.with(activity)
                        .load(getHostUrl(activity) + post.getContent())
                        .centerCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .into(viewHolderImage.postImage);

                viewHolderImage.noOfLikes.setText(String.valueOf(post.getNumber_of_comments()));
                viewHolderImage.noOfComments.setText(String.valueOf(post.getNumber_of_comments()));

                //to handle post by it's id
                viewHolderImage.like.setHint(String.valueOf(post.getLiked()));

                handlePost = new HandlePost(activity);

                handlePost.setLikeButtonState(viewHolderImage.like, post.getLiked());

                viewHolderImage.more.setVisibility(View.GONE);

                break;
            case 3://this case for video post
                ViewHolderVideo viewHolderVideo = (ViewHolderVideo) holder;
                viewHolderVideo.username.setText(post.getUsername());


                viewHolderVideo.level.append(" " + post.getUser_level());

                //fill avatar image
                Glide.with(activity)
                        .load(getHostUrl(activity) + post.getUser_avatar())
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.loading_spinner)
                        .into(viewHolderVideo.profileImage);

                viewHolderVideo.initDate.setText(post.getCreated_at().substring(0, post.getCreated_at().indexOf('T')));
                viewHolderVideo.caption.setText(post.getCaption());

                viewHolderVideo.content.setVisibility(View.GONE);


                viewHolderVideo.postVideo.setUp(getHostUrl(activity) + post.getContent(), post.getCaption());
                viewHolderVideo.postVideo.posterImageView.setImageDrawable(activity.getDrawable(R.drawable._1_train_hard));

                viewHolderVideo.noOfLikes.setText(String.valueOf(post.getNumber_of_likes()));
                viewHolderVideo.noOfComments.setText(String.valueOf(post.getNumber_of_comments()));


                //to handle post by it's id
                viewHolderVideo.like.setHint(String.valueOf(post.getLiked()));

                handlePost = new HandlePost(activity);

                handlePost.setLikeButtonState(viewHolderVideo.like, post.getLiked());

                //set video to video view
                viewHolderVideo.postVideo.setUp(getHostUrl(activity) + post.getContent(), "");

                viewHolderVideo.more.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // post basic holder
    public class ViewHolderBasic extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username,
                initDate,
                caption,
                content,
                noOfLikes,
                noOfComments,
                level;

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
            level = itemView.findViewById(R.id.level);

            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            more = itemView.findViewById(R.id.more);

            like.setOnClickListener(this);
            comment.setOnClickListener(this);
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
                noOfComments,
                level;

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
            level = itemView.findViewById(R.id.level);

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
                noOfComments,
                level;

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
            level = itemView.findViewById(R.id.level);

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
                noOfComments,
                level;

        ImageView profileImage;

        JzvdStd postVideo;

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
            level = itemView.findViewById(R.id.level);

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
    public Post getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    //get item type by it's position
    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}