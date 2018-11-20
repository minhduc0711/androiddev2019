package com.usth.group10.githubclient.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingFragment extends Fragment {
    private static final String KEY_USER_URL = "user_url";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mFollowingRecycleView;
    private FollowingAdapter mFollowingAdapter;

    public static FollowingFragment newInstance(String userUrl) {
        FollowingFragment followingFragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USER_URL, userUrl);
        followingFragment.setArguments(args);
        return followingFragment;
    }

    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        mFollowingRecycleView = view.findViewById(R.id.recycler_view_feeds);
        mFollowingRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_feeds);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryColor));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFollowingList();
            }
        });

        ArrayList<Following> followingList = new ArrayList<>();
        mFollowingAdapter = new FollowingAdapter(followingList);
        mFollowingRecycleView.setAdapter(mFollowingAdapter);
        updateFollowingList();
        return  view;
    }

    private class FollowingAdapter extends RecyclerView.Adapter<FollowingViewHolder>{
        private ArrayList<FollowingFragment.Following> mFollowingList;

        private FollowingAdapter(ArrayList<Following> followingArrayList){
            mFollowingList = followingArrayList;
        }

        public ArrayList<Following> getFollowingList() {
            return mFollowingList;
        }

        @NonNull
        @Override
        public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return  new FollowingViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position) {
            holder.bind(mFollowingList.get(position));
        }

        @Override
        public int getItemCount() {
            return  mFollowingList.size();
        }
    }

    private class FollowingViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mUserAvatar;
        private TextView mTextViewName;

        private FollowingViewHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.item_user_list,parent,false));
            mUserAvatar = itemView.findViewById(R.id.image_avatar_user_list);
            mTextViewName = itemView.findViewById(R.id.text_username_user_list);

        }

        private void bind(final FollowingFragment.Following following){
            mTextViewName.setText(following.getmUserName());
            Picasso.get().load(following.getmUserAvaterURL()).into(mUserAvatar);
            mUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProfileActivity.newIntent(getActivity(),following.mUserURL);
                    startActivity(intent);
                }
            });
            mTextViewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProfileActivity.newIntent(getActivity(),following.mUserURL);
                    startActivity(intent);
                }
            });
        }

    }

    private void updateFollowingList() {
        String access_token = getContext().getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                .getString(MySingleton.KEY_ACCESS_TOKEN, "");
        String url = getArguments().getString(KEY_USER_URL) + "/following?access_token=" + access_token;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Following> followingList = processRawJson(response);
                        mFollowingAdapter.getFollowingList().clear();
                        mFollowingAdapter.getFollowingList().addAll(followingList);
                        mFollowingAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Loading following failed",Toast.LENGTH_SHORT).show();
                    }

                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }



        private ArrayList<FollowingFragment.Following> processRawJson(JSONArray response){
        JSONObject currentItem;
        ArrayList<FollowingFragment.Following> followingsList = new ArrayList<>();
        String name, userAvatarURL, userURL;
        for (int i = 0; i < response.length(); i++) {
            try {
                currentItem = response.getJSONObject(i);

                name = currentItem.getString("login");
                userAvatarURL = currentItem.getString("avatar_url");
                userURL = currentItem.getString("url");

                followingsList.add(new FollowingFragment.Following(userURL,userAvatarURL,name));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return followingsList;
    }


    private class Following{
        private String mUserAvaterURL;
        private String mUserURL;
        private String mUserName;

        private Following(String mUserURL, String mUserAvaterURL, String mUserName){
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
