package com.example.fitzone.recycleViewAdapters;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;
import static com.example.fitzone.common_functions.StaticFunctions.getHostUrl;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fitzone.activites.R;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.user_data.UserDataResponse;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendRecordAdapter extends RecyclerView.Adapter<FriendRecordAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private Activity activity;
    private HashMap<String, String> userAvatarLinkWithTag = new HashMap<>();
    private MutableLiveData<HashMap<String, String>> userAvatarLink = new MutableLiveData();
    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private ItemClickListener mClickListener;


    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // data is passed into the constructor
    public FriendRecordAdapter(Activity activity, List<String> arrayOfPosts) {
        if (activity == null)
            return;

        this.activity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.mData = arrayOfPosts;

        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(activity))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
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
        String friendData = mData.get(position);
        holder.username.setText(friendData);

        //put image in imageview
        getUserAvatar(mData.get(position));
        userAvatarLink.observe((LifecycleOwner) activity, url -> {
            Glide.with(activity)
                    .load(getHostUrl(activity) + url.get(mData.get(position)))
                    .circleCrop()
                    .placeholder(R.drawable.loading_spinner)
                    .into(holder.userAvatar);
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username;
        ImageView userAvatar;

        ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            userAvatar = itemView.findViewById(R.id.friendImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    //get user avatar link
    private void getUserAvatar(String username) {
        if (apiInterface == null)
            return;

        Call<UserDataResponse> call = apiInterface.getUserData("Bearer " + getApiToken(activity), username);
        call.enqueue(new Callback<UserDataResponse>() {
            @Override
            public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                if (response.body() == null)
                    return;
                userAvatarLinkWithTag.put(response.body().getUsername(), response.body().getAvatar());
                userAvatarLink.setValue(userAvatarLinkWithTag);
            }

            @Override
            public void onFailure(Call<UserDataResponse> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}