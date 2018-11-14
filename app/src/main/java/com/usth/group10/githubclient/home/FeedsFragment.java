package com.usth.group10.githubclient.home;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedsFragment extends Fragment {
    private static final String TAG = "FeedsFragment";

    private FrameLayout mProgressBarLayout;
    private RecyclerView mFeedsRecyclerView;
    private RecyclerView.Adapter mFeedsAdapter;

    public FeedsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);

        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout_feeds);
        mProgressBarLayout.setVisibility(View.VISIBLE);

        mFeedsRecyclerView = view.findViewById(R.id.recycler_view_feeds);
        mFeedsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateFeedsList();
        return view;
    }

    private class FeedsAdapter extends RecyclerView.Adapter<FeedsViewHolder> {
        private ArrayList<Feed> mFeedsList;

        private FeedsAdapter(ArrayList<Feed> feedsList) {
            mFeedsList = feedsList;
        }

        @NonNull
        @Override
        public FeedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FeedsViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedsViewHolder holder, int position) {
            holder.bind(mFeedsList.get(position));
        }

        @Override
        public int getItemCount() {
            return mFeedsList.size();
        }
    }

    private class FeedsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mUserAvatar;
        private TextView mTextViewTitle;
        private TextView mTextViewTime;
        private TextView mTextViewContent;

        private FeedsViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_feeds_list, parent, false));
            mUserAvatar = itemView.findViewById(R.id.image_avatar_feeds);
            mTextViewTitle = itemView.findViewById(R.id.text_title_feeds);
            mTextViewTime = itemView.findViewById(R.id.text_time_feeds);
            mTextViewContent = itemView.findViewById(R.id.text_content_feeds);
        }

        private void bind(Feed feed) {
            mTextViewTitle.setText(feed.getSpannedTitle());
            mTextViewTime.setText(feed.getTime());
            if (feed.getContent() != null) {
                mTextViewContent.setVisibility(View.VISIBLE);
                mTextViewContent.setText(feed.getContent());
            } else {
                mTextViewContent.setVisibility(View.GONE);
            }
            Picasso.get().load(feed.getUserAvatarUrl()).into(mUserAvatar);
        }
    }

    private void updateFeedsList() {
        String access_token = getContext().getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                                    .getString(MySingleton.KEY_ACCESS_TOKEN, "");
        String username = getContext().getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                            .getString(MySingleton.KEY_USERNAME, "");
        String url = "https://api.github.com/users/" + username + "/received_events?access_token=" + access_token;
        Log.d(TAG, url);
        Log.d(TAG, username);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Feed> feedsList = processRawJson(response);
                        
                        mFeedsAdapter = new FeedsAdapter(feedsList);
                        mFeedsRecyclerView.setAdapter(mFeedsAdapter);
                        mProgressBarLayout.setVisibility(View.GONE);
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

        String title, content, userAvatarUrl, userUrl, repoUrl, time;
        Spanned spannedTitle;
        String username, action, repoName;

        for (int i = 0; i < response.length(); i++) {
            content = null;
            try {
                currentItem = response.getJSONObject(i);

                username = currentItem.getJSONObject("actor").getString("login");
                repoName = currentItem.getJSONObject("repo").getString("name");

                switch (currentItem.getString("type")) {
                    case "WatchEvent":
                        action = "starred";
                        break;
                    case "ForkEvent":
                        action = "forked";
                        break;
                    case "MemberEvent":
                        action = "added";
                        break;
                    case "PullRequestEvent":
                        action = "opened pull request";
                        repoName += "#" + currentItem.getJSONObject("payload").getInt("number");
                        content = currentItem.getJSONObject("payload").getJSONObject("pull_request").getString("title");
                        break;
                    case "IssuesEvent":
                        action = "opened issue";
                        repoName += "#" + currentItem.getJSONObject("payload").getJSONObject("issue").getInt("number");
                        content = currentItem.getJSONObject("payload").getJSONObject("issue").getString("title");
                        break;
                    case "IssueCommentEvent":
                        action = "commented on issue";
                        repoName += "#" + currentItem.getJSONObject("payload").getJSONObject("issue").getInt("number");
                        break;
                    default:
                        action = "is confused";
                }


                title = username + " <b>" + action + "</b> " + repoName;
                spannedTitle = Html.fromHtml(title);
                userAvatarUrl = currentItem.getJSONObject("actor").getString("avatar_url");
                userUrl = currentItem.getJSONObject("actor").getString("url");
                repoUrl = currentItem.getJSONObject("repo").getString("url");
                time = currentItem.getString("created_at");

                feedsList.add(new Feed(title, spannedTitle, content, userAvatarUrl, userUrl, repoUrl, time));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return feedsList;
    }

    private class Feed {
        private String mTitle;
        private Spanned mSpannedTitle;
        private String mContent;
        private String mUserAvatarUrl;
        private String mUserUrl;
        private String mRepoUrl;
        private String mTime;


        private Feed(String title, Spanned spannedTitle, String content, String userAvatarUrl, String userUrl, String repoUrl, String time) {
            mTitle = title;
            mSpannedTitle = spannedTitle;
            mContent = content;
            mUserAvatarUrl = userAvatarUrl;
            mUserUrl = userUrl;
            mRepoUrl = repoUrl;
            setTime(time);
        }

        public String getTitle() {
            return mTitle;
        }

        public Spanned getSpannedTitle() {
            return mSpannedTitle;
        }

        public String getUserAvatarUrl() {
            return mUserAvatarUrl;
        }

        public String getTime() {
            return mTime;
        }

        public String getContent() {
            return mContent;
        }

        public String getUserUrl() {
            return mUserUrl;
        }

        public String getRepoUrl() {
            return mRepoUrl;
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
    }
}
