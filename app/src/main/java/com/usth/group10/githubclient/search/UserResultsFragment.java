package com.usth.group10.githubclient.search;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.profile.ProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserResultsFragment extends Fragment {
    private static final String KEY_JSON_ARRAY = "json_array";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mUserAdapter;

    public UserResultsFragment() {
        // Required empty public constructor
    }

    public static UserResultsFragment newInstance(String jsonArray) {
        UserResultsFragment userResultsFragment = new UserResultsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_JSON_ARRAY, jsonArray);
        userResultsFragment.setArguments(args);
        return userResultsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_results, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_user_results);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateList();

        return view;
    }

    private void updateList() {
        try {
            JSONArray jsonArray = new JSONArray(getArguments().getString(KEY_JSON_ARRAY));
            ArrayList<User> userList = processJsonArray(jsonArray);
            mUserAdapter = new UserAdapter(userList);
            mRecyclerView.setAdapter(mUserAdapter);
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
                @Override
                protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(0);
            mRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<User> processJsonArray(JSONArray jsonArray) {
        ArrayList<User> userList = new ArrayList<>();
        JSONObject currentItem;

        String avatarUrl;
        String username;
        String userUrl;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                currentItem = jsonArray.getJSONObject(i);
                avatarUrl = currentItem.getString("avatar_url");
                username = currentItem.getString("login");
                userUrl = currentItem.getString("url");

                userList.add(new User(avatarUrl, username, userUrl));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return userList;
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mImageUserAvatar;
        private TextView mTextViewUsername;

        private UserViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_user_list, parent, false));
            mImageUserAvatar = itemView.findViewById(R.id.image_avatar_user_list);
            mTextViewUsername = itemView.findViewById(R.id.text_username_user_list);
        }

        private void bind(final User user) {
            Picasso.get().load(user.getAvatarUrl()).into(mImageUserAvatar);
            mTextViewUsername.setText(user.getUsername());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProfileActivity.newIntent(getActivity(), user.getUserUrl());
                    startActivity(intent);
                }
            });
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
        private ArrayList<User> mUserList;

        public UserAdapter(ArrayList<User> userList) {
            mUserList = userList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new UserViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            holder.bind(mUserList.get(position));
        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }
    }

    private class User {
        private String mAvatarUrl;
        private String mUsername;
        private String mUserUrl;

        public User(String avatarUrl, String username, String userUrl) {
            mAvatarUrl = avatarUrl;
            mUsername = username;
            mUserUrl = userUrl;
        }

        public String getAvatarUrl() {
            return mAvatarUrl;
        }

        public String getUsername() {
            return mUsername;
        }

        public String getUserUrl() {
            return mUserUrl;
        }
    }
}
