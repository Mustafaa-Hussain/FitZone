package com.example.fitzone.activites;

import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitzone.handelers.HandleRequests;

import com.example.fitzone.recycleViewAdapters.RecycleViewAdapterForFriendRecord;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsPage extends AppCompatActivity implements RecycleViewAdapterForFriendRecord.ItemClickListener {

    RecycleViewAdapterForFriendRecord adapter;
    RecyclerView recyclerView;

    FloatingActionButton searchForNewFriend;

    String apiToken;

    JSONArray myFriends;

    Button search, cancel, yes, no;

    TabLayout tabLayout;

    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_page);

        SharedPreferences apiTokenFile = getSharedPreferences("UserData", Comments.MODE_PRIVATE);
        apiToken = apiTokenFile.getString("apiToken", null);

        HandleRequests handleRequests = new HandleRequests(FriendsPage.this);
        handleRequests.getUserProfile(apiToken, new HandleRequests.VolleyResponseListener() {
            @Override
            public void onResponse(boolean status, JSONObject jsonObject) {
                if (status) {
                    try {
                        myFriends = jsonObject.getJSONArray("friends_username");

                        recyclerView = findViewById(R.id.recycleView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(FriendsPage.this));

                        if (!myFriends.toString().equals("[]")) {
                            adapter = new RecycleViewAdapterForFriendRecord(FriendsPage.this, myFriends);
                        } else {
                            Snackbar.make(findViewById(R.id.recycleView), "There is no Friends write now", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    recyclerView.setAdapter(adapter);
                } else {
                    Snackbar.make(findViewById(R.id.recycleView), "Something wrong", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        tabLayout = findViewById(R.id.tl_friends_page);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals(getString(R.string.my_friends))) {
                    HandleRequests handleRequests = new HandleRequests(FriendsPage.this);
                    handleRequests.getUserProfile(apiToken, (status, jsonObject) -> {
                        if (status) {
                            try {
                                myFriends = jsonObject.getJSONArray("friends_username");

                                recyclerView = findViewById(R.id.recycleView);
                                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsPage.this));

                                if (!myFriends.toString().equals("[]")) {
                                    adapter = new RecycleViewAdapterForFriendRecord(FriendsPage.this, myFriends);
                                } else {
                                    //adapter = new RecycleViewAdapterForFriendRecord(FriendsPage.this, null);
                                    Snackbar.make(findViewById(R.id.recycleView), "There is no Friends write now", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            recyclerView.setAdapter(adapter);
                        } else {
                            Snackbar.make(findViewById(R.id.recycleView), "Something wrong", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                } else if (tab.getText().equals(getString(R.string.all_users))) {

                    HandleRequests handleRequests = new HandleRequests(FriendsPage.this);
                    handleRequests.getAllUsers(apiToken, (status, jsonObject) -> {
                        if (status) {
                            try {
                                myFriends = jsonObject.getJSONArray("users");

                                recyclerView = findViewById(R.id.recycleView);
                                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsPage.this));

                                if (!myFriends.toString().equals("[]")) {
                                    adapter = new RecycleViewAdapterForFriendRecord(FriendsPage.this, myFriends);
                                } else {
                                    Snackbar.make(findViewById(R.id.recycleView), "There is no Friends write now", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            recyclerView.setAdapter(adapter);
                        } else {
                            Snackbar.make(tabLayout, "There is no user with this name", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getId() == R.id.myFriends_t) {

                } else if (tab.getId() == R.id.allFriends_t) {

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //search for new friend
        searchForNewFriend = findViewById(R.id.search_for_new_friend);
        searchForNewFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //handle search new friend

                // inflate the layout of the popup window
                View popupView = getLayoutInflater().inflate(R.layout.search_new_friend, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                popupWindow = new PopupWindow(popupView, width, height, true);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window token
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                search = popupView.findViewById(R.id.search);
                search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText username;
                        username = popupView.findViewById(R.id.searchUsername);

                        if (!username.getText().equals("")) {
                            String apiToken = getSharedPreferences("UserData", MODE_PRIVATE).getString("apiToken", null);

                            HandleRequests handleRequests = new HandleRequests(FriendsPage.this);
                            handleRequests.searchUser(apiToken, username.getText().toString(),
                                    new HandleRequests.VolleyResponseListener() {
                                        @Override
                                        public void onResponse(boolean status, JSONObject jsonObject) {
                                            if (status) {
                                                boolean flag = false;
                                                try {
                                                    String username = jsonObject.getString("username");
                                                    //check if this friend or not
                                                    for (int i = 0; i < myFriends.length(); i++) {
                                                        if (myFriends.getString(i).equals(username)) {
                                                            flag = true;
                                                            break;
                                                        }
                                                    }

                                                    popupWindow.dismiss();
                                                    askToAddOrNot(flag, username, view);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                Snackbar.make(view, "There is no user with this name", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }
                                    });
                        } else {
                            Snackbar.make(view, "must fill all fields", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }
                });

                cancel = popupView.findViewById(R.id.cancelPopUp);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(view, "cancel", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        popupWindow.dismiss();
                    }
                });

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        //
    }

    public void askToAddOrNot(boolean flag, String username, View view) {

        PopupWindow askPopup;

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.ask_if_yes_or_no, null);///

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
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
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String apiToken = getSharedPreferences("UserData", MODE_PRIVATE).getString("apiToken", null);
                HandleRequests handleRequests = new HandleRequests(FriendsPage.this);

                if (flag) {

                    handleRequests.removeFriend(apiToken, username,
                            new HandleRequests.VolleyResponseListener() {
                                @Override
                                public void onResponse(boolean status, JSONObject jsonObject) {
                                    if (status) {
                                        Intent intent;
                                        Snackbar.make(view, username + " Removed.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        intent = new Intent(FriendsPage.this, FriendsPage.class);
                                        startActivity(intent);

                                        askPopup.dismiss();
                                    } else {
                                        Snackbar.make(view, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }
                            });
                } else {

                    handleRequests.addFriend(apiToken, username,
                            new HandleRequests.VolleyResponseListener() {
                                @Override
                                public void onResponse(boolean status, JSONObject jsonObject) {
                                    if (status) {
                                        Intent intent;
                                        Snackbar.make(view, username + " Now is your friend.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        intent = new Intent(FriendsPage.this, FriendsPage.class);
                                        startActivity(intent);

                                        askPopup.dismiss();
                                    } else {
                                        Snackbar.make(view, "something wrong with your internet connection", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }
                            });
                }

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