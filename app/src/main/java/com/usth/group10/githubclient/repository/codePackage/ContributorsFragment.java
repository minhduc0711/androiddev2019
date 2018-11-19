package com.usth.group10.githubclient.repository.codePackage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;
import com.usth.group10.githubclient.profile.ProfileActivity;
import com.usth.group10.githubclient.repository.RepoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import androidx.fragment.app.Fragment;

public class ContributorsFragment extends Fragment {
    private static final String KEY_REPO_URL = "repo_url";

    private FrameLayout mProgressBarLayout;
    private RecyclerView mContributorsRecyclerView;
    private RecyclerView.Adapter mContributorsApdapter;

    public static ContributorsFragment newInstance(String repoUrl) {
        ContributorsFragment ContributorsFragment = new ContributorsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_REPO_URL, repoUrl);
        ContributorsFragment.setArguments(args);
        return ContributorsFragment;
    }
    
    public ContributorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contributors, container, false);

        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout_feeds);
        mProgressBarLayout.setVisibility(View.VISIBLE);

        mContributorsRecyclerView = view.findViewById(R.id.recycler_view_contributors);
        mContributorsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateContributorsList();
        return view;
    }

    private class ContributorsAdapter extends RecyclerView.Adapter<ContributorsViewHolder>{
        private ArrayList<Contributors> mContributorsList;

        private ContributorsAdapter(ArrayList<Contributors> contributorsList){
            mContributorsList = contributorsList;
        }

        @NonNull
        @Override
        public ContributorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ContributorsViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ContributorsViewHolder holder, int position) {
            holder.bind(mContributorsList.get(position));
        }

        @Override
        public int getItemCount() {
            return mContributorsList.size();
        }
    }

    private class ContributorsViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mImageUserAvatar;
        private TextView mTextViewTitle;
        private TextView mTextViewCommits;

        private ContributorsViewHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.item_feeds_list, parent, false));

            mImageUserAvatar = itemView.findViewById(R.id.image_avatar_feeds);
            mTextViewTitle = itemView.findViewById(R.id.text_title_feeds);
            mTextViewCommits = itemView.findViewById(R.id.text_content_feeds);
        }

        private void bind(final Contributors contributors){
            mTextViewTitle.setText(contributors.getmTitle());
            mTextViewCommits.setText(contributors.getmCommits());
            Picasso.get().load(contributors.getmUserAvatarUrl()).into(mImageUserAvatar);

            mImageUserAvatar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = ProfileActivity.newIntent(getActivity(), contributors.getUserUrl());
                        startActivity(intent);
                }
            });
        }
    }

    private void updateContributorsList(){
        String url = getArguments().getString(KEY_REPO_URL) + "/contributors";
        // url = repoURL + "contributor"
        // need to changed later

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Contributors> contributorsList = processRawJson(response);

                        mContributorsApdapter = new ContributorsAdapter(contributorsList);
                        mContributorsRecyclerView.setAdapter(mContributorsApdapter);
                        mProgressBarLayout.setVisibility(View.GONE);
                    }
                },  new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Loading contributors failed", Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private ArrayList<Contributors> processRawJson(JSONArray response){
        JSONObject currentItem;
        ArrayList<Contributors> contributorsList = new ArrayList<>();
        String title, userAvatarUrl, commits, userUrl;

        for (int i = 0; i < response.length(); i++){
            try{
                currentItem = response.getJSONObject(i);
                title = currentItem.getString("login");
                userAvatarUrl = currentItem.getString("avatar_url");
                commits = "commits (" + currentItem.getString("contributions") + ")";
                userUrl = currentItem.getString("url");

                contributorsList.add(new Contributors(title, userAvatarUrl, commits, userUrl));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return contributorsList;
    }

    private class Contributors{
        private String mTitle;
        private String mCommits;
        private String mUserAvatarUrl;
        private String mUserUrl;

        private Contributors(String title, String userAvatarUrl, String commits, String userUrl){
            mTitle = title;
            mUserAvatarUrl = userAvatarUrl;
            mCommits = commits;
            mUserUrl = userUrl;
        }

        public String getmTitle() { return mTitle; }

        public String getmUserAvatarUrl() { return mUserAvatarUrl; }

        public String getmCommits() { return mCommits; }

        public String getUserUrl() { return mUserUrl; }
    }
}
