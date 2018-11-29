package com.usth.group10.githubclient.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class FollowerFragment extends Fragment {
    private static final String KEY_USER_URL = "user_url";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mFollowerRecycleView;
    private FollowerAdapter mFollowerAdapter;

    public FollowerFragment() {
        // Required empty public constructor
    }

    public static FollowerFragment newInstance(String userUrl) {
        FollowerFragment followerFragment = new FollowerFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USER_URL, userUrl);
        followerFragment.setArguments(args);
        return followerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feeds, container, false);

        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout_feeds);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryColor));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFollowerList();
            }
        });

        mFollowerRecycleView = v.findViewById(R.id.recycler_view_feeds);
        mFollowerRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Follower> followerList = new ArrayList<>();
        mFollowerAdapter = new FollowerAdapter(followerList);
        mFollowerRecycleView.setAdapter(mFollowerAdapter);
        updateFollowerList();
        return v;
    }

    private void updateFollowerList() {
        String access_token = getContext().getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                .getString(MySingleton.KEY_ACCESS_TOKEN, "");

        String url = getArguments().getString(KEY_USER_URL) + "/followers?access_token=" + access_token;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Follower> followersList = processRawJson(response);
                        mFollowerAdapter.getFollowerList().clear();
                        mFollowerAdapter.getFollowerList().addAll(followersList);
                        mFollowerAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Nothing
                        Toast.makeText(getContext(), "Loading follower failed", Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private ArrayList<Follower> processRawJson(JSONArray response) {
        JSONObject currentItem;
        ArrayList<Follower> followersList = new ArrayList<>();

        String name, userAvatarURL, userURL;
        for (int i = 0; i < response.length(); i++) {
            try {
                currentItem = response.getJSONObject(i);

                name = currentItem.getString("login");
                userAvatarURL = currentItem.getString("avatar_url");
                userURL = currentItem.getString("url");

                followersList.add(new Follower(userURL, userAvatarURL, name));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return followersList;
    }

    private class FollowerAdapter extends RecyclerView.Adapter<FollowerViewHolder> {
        private ArrayList<Follower> mFollowerList;

        private FollowerAdapter(ArrayList<Follower> followerArrayList) {
            mFollowerList = followerArrayList;
        }

        public ArrayList<Follower> getFollowerList() {
            return mFollowerList;
        }

        @NonNull
        @Override
        public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FollowerViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {
            holder.bind(mFollowerList.get(position));
        }

        @Override
        public int getItemCount() {
            return mFollowerList.size();
        }
    }

    private class FollowerViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mUserAvatar;
        private TextView mTextViewName;

        private FollowerViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_user_list, parent, false));
            mUserAvatar = itemView.findViewById(R.id.image_avatar_user_list);
            mTextViewName = itemView.findViewById(R.id.text_username_user_list);

        }

        private void bind(final Follower follower) {
            mTextViewName.setText(follower.getmUserName());

            Picasso.get().load(follower.getmUserAvaterURL()).into(mUserAvatar);
            mUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProfileActivity.newIntent(getActivity(), follower.getmUserURL());
                    startActivity(intent);
                }
            });
            mTextViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProfileActivity.newIntent(getActivity(), follower.getmUserURL());
                    startActivity(intent);
                }
            });
        }

    }

    private class Follower {
        private String mUserAvaterURL;
        private String mUserURL;
        private String mUserName;

        private Follower(String mUserURL, String mUserAvaterURL, String mUserName) {
            this.mUserAvaterURL = mUserAvaterURL;
            this.mUserName = mUserName;
            this.mUserURL = mUserURL;
        }

        public String getmUserAvaterURL() {
            return mUserAvaterURL;
        }

        public String getmUserName() {
            return mUserName;
        }

        public String getmUserURL() {
            return mUserURL;
        }
    }


}
