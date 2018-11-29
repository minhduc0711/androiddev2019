package com.usth.group10.githubclient.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FeedProfileFragment extends androidx.fragment.app.Fragment {
    private static final String TAG = "FeedsFragment";
    private static final String KEY_USER_URL = "user_url";
    private RecyclerView mFeedRecycleView;
    private RecyclerView.Adapter mFeedAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public FeedProfileFragment() {
        // Required empty public constructor
    }

    public static FeedProfileFragment newInstance(String userUrl) {
        FeedProfileFragment feedProfileFragment = new FeedProfileFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USER_URL, userUrl);
        feedProfileFragment.setArguments(args);
        return feedProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        mFeedRecycleView = view.findViewById(R.id.recycler_view_feeds);
        mFeedRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_feeds);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryColor));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeedList();
            }
        });

        updateFeedList();
        return view;
    }

    private void updateFeedList() {
        String access_token = getContext().getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                .getString(MySingleton.KEY_ACCESS_TOKEN, "");
        String url = getArguments().getString(KEY_USER_URL) + "/events?access_token=" + access_token;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Feed> feedsList = processRawJson(response);

                        mFeedAdapter = new FeedAdapter(feedsList);
                        mFeedRecycleView.setAdapter(mFeedAdapter);

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Loading feeds failed", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private ArrayList<Feed> processRawJson(JSONArray response) {
        JSONObject currentItem;
        ArrayList<Feed> feedsList = new ArrayList<>();
        String username, action, repoName;


        String sha, message;

        String title, body, time, type;
        int size;

        for (int i = 0; i < response.length(); i++) {
            body = null;
            size = -1;
            try {
                currentItem = response.getJSONObject(i);
                username = currentItem.getJSONObject("actor").getString("login");
                repoName = currentItem.getJSONObject("repo").getString("name");
                type = currentItem.getString("type");

                switch (type) {
                    case "PushEvent":
                        action = " pushed to master at ";
                        sha = currentItem.getJSONObject("payload").getJSONArray("commits").getJSONObject(0).getString("sha");
                        message = currentItem.getJSONObject("payload").getJSONArray("commits").getJSONObject(0).getString("message");
                        size = currentItem.getJSONObject("payload").getInt("size");
                        body = sha.substring(0, 6) + " " + message;
                        break;
                    case "ForkEvent":
                        action = " forked ";
                        break;
                    case "PullRequestEvent":
                        action = " opened pull request ";
                        repoName += "#" + currentItem.getJSONObject("payload").getInt("number");
                        body = currentItem.getJSONObject("payload").getJSONObject("pull_request").getString("title");
                        break;
                    case "WatchEvent":
                        action = " starred ";
                        break;
                    default:
                        action = " is confused ";
                }

                title = username + action + repoName;
                time = currentItem.getString("created_at");

                feedsList.add(new Feed(title, body, time, size, type));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return feedsList;
    }

    private class FeedAdapter extends RecyclerView.Adapter<FeedsViewHolder> {
        private ArrayList<Feed> mFeedArrayList;

        private FeedAdapter(ArrayList<Feed> feedsList) {
            mFeedArrayList = feedsList;
        }

        @NonNull
        @Override
        public FeedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FeedsViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedsViewHolder holder, int position) {
            holder.bind(mFeedArrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return mFeedArrayList.size();
        }
    }

    private class FeedsViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewTitle;
        private TextView mTextViewSize;
        private TextView mTextViewBody;
        private TextView mTextViewTime;
        private ImageView mImageViewIcon;
        private String size;

        private FeedsViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_feed_profile_list, parent, false));
            mTextViewTitle = itemView.findViewById(R.id.text_title_feed_profile);
            mTextViewSize = itemView.findViewById(R.id.text_commit_number_feed_profile);
            mTextViewBody = itemView.findViewById(R.id.text_message_profile_feed);
            mTextViewTime = itemView.findViewById(R.id.text_time_feed_profile);
            mImageViewIcon = itemView.findViewById(R.id.img_symbol_feed_profile);
        }

        private void bind(final Feed feed) {
            size = feed.getSize() + " new commit";
            mTextViewTitle.setText(feed.getTitle());

            if (feed.getSize() == -1) {
                mTextViewSize.setVisibility(View.GONE);
            } else {
                mTextViewSize.setVisibility(View.VISIBLE);
                mTextViewSize.setText(size);
            }

            mTextViewBody.setText(feed.getBody());

            if (feed.getType().equals("PushEvent")) {
                mTextViewBody.setVisibility(View.VISIBLE);
                mImageViewIcon.setImageResource(R.drawable.ic_repo_forked);
            } else if (feed.getType().equals("PullRequestEvent")) {
                mTextViewBody.setVisibility(View.VISIBLE);
                mImageViewIcon.setImageResource(R.drawable.ic_git_pull_request);
            } else if (feed.getType().equals("ForkEvent")) {
                mTextViewBody.setVisibility(View.GONE);
                mImageViewIcon.setImageResource(R.drawable.ic_git_commit);
            } else if (feed.getType().equals("WatchEvent")) {
                mTextViewBody.setVisibility(View.GONE);
                mImageViewIcon.setImageResource(R.drawable.ic_menu_star_full_color);
            }
            mTextViewTime.setText(feed.getTime());
        }
    }

    private class Feed {
        private String mTitle;
        private String mBody;
        private String mTime;
        private int mSize;
        private String mType;

        private Feed(String title, String body, String time, int size, String type) {
            this.mBody = body;
            this.mSize = size;
            setTime(time);
            this.mTitle = title;
            this.mType = type;
        }

        public String getType() {
            return mType;
        }

        public String getTime() {
            return mTime;
        }

        public void setTime(String time) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            try {
                Date d = formatter.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                calendar.add(Calendar.HOUR, 7);

                mTime = DateUtils.getRelativeTimeSpanString(calendar.getTime().getTime(), new Date().getTime(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public String getBody() {
            return mBody;
        }

        public int getSize() {
            return mSize;
        }

        public String getTitle() {
            return mTitle;
        }
    }
}
