package com.example.fitzone.activites;

import static com.example.fitzone.common_functions.StaticFunctions.getApiToken;
import static com.example.fitzone.common_functions.StaticFunctions.getBaseUrl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.handelers.HandleRequests;

import com.example.fitzone.recycleViewAdapters.FriendRecordAdapter;
import com.example.fitzone.retrofit_requists.ApiInterface;
import com.example.fitzone.retrofit_requists.data_models.all_usernames.AllUsernamesResponse;
import com.example.fitzone.retrofit_requists.data_models.user_data.UserDataResponse;
import com.example.fitzone.retrofit_requists.data_models.user_profile_data.UserProfileResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsPage extends AppCompatActivity implements FriendRecordAdapter.ItemClickListener {

    private List<String> allFriends;

    private FriendRecordAdapter adapter;
    private RecyclerView recyclerView;

    private FloatingActionButton searchForNewFriend;


    private List<String> myFriends;
    private List<String> allShownFriends;
    private List<String> myFriendsPermanent;

    private Button search, cancel, yes, no;

    private TabLayout tabLayout;

    private PopupWindow popupWindow;

    private Retrofit retrofit;
    private ApiInterface apiInterface;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_page);

        //inflate elements
        recyclerView = findViewById(R.id.recycleView);


        //retrofit builder
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

        //get user profile data
        getUserProfile();

        tabLayout = findViewById(R.id.tl_friends_page);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals(getString(R.string.my_friends)))
                    getUserProfile();
                else if (tab.getText().equals(getString(R.string.all_users)))
                    getAllUser();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                myFriends.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //search for new friend
        searchForNewFriend = findViewById(R.id.search_for_new_friend);
        searchForNewFriend.setOnClickListener(view -> {
            //handle search new friend
            // inflate the layout of the popup window
            View popupView = getLayoutInflater().inflate(R.layout.search_new_friend, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            popupWindow = new PopupWindow(popupView, width, height, true);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            search = popupView.findViewById(R.id.search);
            search.setOnClickListener(v -> {

                EditText username;
                username = popupView.findViewById(R.id.searchUsername);

                if (!username.getText().equals(""))
                    getUserDataWithUsername(username.getText().toString());
                else
                    Snackbar.make(view, "must fill all fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            });

            cancel = popupView.findViewById(R.id.cancelPopUp);
            cancel.setOnClickListener(v -> {
                Snackbar.make(view, "cancel", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                popupWindow.dismiss();
            });

            // dismiss the popup window when touched
            popupView.setOnTouchListener((v, event) -> {
                //popupWindow.dismiss();
                return true;
            });
        });
    }


    //get user user data by it's username
    private void getUserDataWithUsername(String username) {
        if (apiInterface == null)
            return;

        Call<UserDataResponse> call = apiInterface.getUserData("Bearer " + getApiToken(this), username);
        call.enqueue(new Callback<UserDataResponse>() {
            @Override
            public void onResponse(Call<UserDataResponse> call, Response<UserDataResponse> response) {
                if (response.body() == null)
                    return;

                allShownFriends = Collections.singletonList(response.body().getUsername());

                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsPage.this));
                adapter = new FriendRecordAdapter(FriendsPage.this,
                        Collections.singletonList(response.body().getUsername()));
                adapter.setClickListener(FriendsPage.this);
                recyclerView.setAdapter(adapter);

                if (response.body().getFriends_username().isEmpty())
                    Snackbar.make(findViewById(R.id.recycleView), "There is no username with this name"
                                    , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                popupWindow.dismiss();
            }

            @Override
            public void onFailure(Call<UserDataResponse> call, Throwable t) {
                Toast.makeText(FriendsPage.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get all users
    private void getAllUser() {
        if (apiInterface == null)
            return;

        Call<AllUsernamesResponse> call = apiInterface.getAllUsernames("Bearer " + getApiToken(this));
        call.enqueue(new Callback<AllUsernamesResponse>() {
            @Override
            public void onResponse(Call<AllUsernamesResponse> call, Response<AllUsernamesResponse> response) {
                if (response.body() == null)
                    return;

                allShownFriends = response.body().getUsers();

                allFriends = response.body().getUsers();

                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsPage.this));
                adapter = new FriendRecordAdapter(FriendsPage.this, allFriends);

                adapter.setClickListener(FriendsPage.this);

                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<AllUsernamesResponse> call, Throwable t) {
                Toast.makeText(FriendsPage.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get user profile and fill friends list
    private void getUserProfile() {
        if (apiInterface == null)
            return;

        Call<UserProfileResponse> call = apiInterface.getUserProfileData("Bearer " + getApiToken(this));
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.body() == null)
                    return;

                allShownFriends = response.body().getFriends_username();
                myFriendsPermanent = response.body().getFriends_username();

                myFriends = response.body().getFriends_username();

                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsPage.this));
                adapter = new FriendRecordAdapter(FriendsPage.this, myFriends);

                if (myFriends.isEmpty())
                    Snackbar.make(findViewById(R.id.recycleView), "There is no Friends write now", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                adapter.setClickListener(FriendsPage.this);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(FriendsPage.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        boolean isMyFriend = myFriendsPermanent.contains(allShownFriends.get(position));
        askToAddOrNot(isMyFriend, allShownFriends.get(position), view);
    }

    @SuppressLint("SetTextI18n")
    public void askToAddOrNot(boolean flag, String username, View view) {

        PopupWindow askPopup;

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.ask_if_yes_or_no, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        askPopup = new PopupWindow(popupView, width, height, true);


        //put the message
        if (flag) {
            ((TextView) popupView.findViewById(R.id.messageQ)).setText("You want to remove " + username + " ?");
        } else {
            ((TextView) popupView.findViewById(R.id.messageQ)).setText("You want to add " + username + " ?");
        }


        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        askPopup.showAtLocation(view, Gravity.CENTER, 0, 0);

        yes = popupView.findViewById(R.id.yes);
        yes.setOnClickListener(v -> {
            HandleRequests handleRequests = new HandleRequests(FriendsPage.this);

            if (flag) {

                handleRequests.removeFriend(getApiToken(this), username,
                        (status, jsonObject) -> {
                            if (status) {
                                Intent intent;
                                Snackbar.make(view, username + " Removed.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                intent = new Intent(FriendsPage.this, FriendsPage.class);
                                startActivity(intent);
                                finish();

                                askPopup.dismiss();
                            } else {
                                Snackbar.make(view, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
            } else {

                handleRequests.addFriend(getApiToken(this), username,
                        (status, jsonObject) -> {
                            if (status) {
                                Intent intent;
                                Snackbar.make(view, username + " Now is your friend.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                intent = new Intent(FriendsPage.this, FriendsPage.class);
                                startActivity(intent);
                                finish();

                                askPopup.dismiss();
                            } else {
                                Snackbar.make(view, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
            }

        });

        no = popupView.findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPopup.dismiss();
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //askPopup.dismiss();
                return true;
            }
        });
    }
}