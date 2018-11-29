package com.usth.group10.githubclient.search;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.profile.ProfileActivity;
import com.usth.group10.githubclient.repository.RepoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RepoResultsFragment extends Fragment {
    private static final String KEY_JSON_ARRAY = "json_array";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRepoAdapter;

    public RepoResultsFragment() {
        // Required empty public constructor
    }

    public static RepoResultsFragment newInstance(String jsonArray) {
        RepoResultsFragment repoResultsFragment = new RepoResultsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_JSON_ARRAY, jsonArray);
        repoResultsFragment.setArguments(args);
        return repoResultsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo_results, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_repo_results);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateList();

        return view;
    }

    private void updateList() {
        try {
            JSONArray jsonArray = new JSONArray(getArguments().getString(KEY_JSON_ARRAY));
            ArrayList<Repo> repoList = processJsonArray(jsonArray);
            mRepoAdapter = new RepoAdapter(repoList);
            mRecyclerView.setAdapter(mRepoAdapter);
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

    private ArrayList<Repo> processJsonArray(JSONArray jsonArray) {
        ArrayList<Repo> repoList = new ArrayList<>();
        JSONObject currentItem;

        String avatarUrl;
        String repoName;
        int starredCount;
        int forkedCount;
        String updatedTime;
        int size;
        String language;
        String userUrl;
        String repoUrl;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                currentItem = jsonArray.getJSONObject(i);
                avatarUrl = currentItem.getJSONObject("owner").getString("avatar_url");
                repoName = currentItem.getString("full_name");
                starredCount = currentItem.getInt("stargazers_count");
                forkedCount = currentItem.getInt("forks");
                updatedTime = currentItem.getString("updated_at");
                size = currentItem.getInt("size");
                language = currentItem.getString("language");
                userUrl = currentItem.getJSONObject("owner").getString("url");
                repoUrl = currentItem.getString("url");

                repoList.add(new Repo(avatarUrl, repoName, starredCount, forkedCount, updatedTime, size, language, userUrl, repoUrl));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return repoList;
    }

    private class RepoViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mImageUserAvatar;
        private TextView mTextViewRepoName;
        private TextView mTextViewRepoInfo;

        private RepoViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_repo_list, parent, false));
            mImageUserAvatar = itemView.findViewById(R.id.image_avatar_repo_list);
            mTextViewRepoName = itemView.findViewById(R.id.text_name_repo_list);
            mTextViewRepoInfo = itemView.findViewById(R.id.text_info_repo_list);
        }

        private void bind(final Repo repo) {
            Picasso.get().load(repo.getAvatarUrl()).into(mImageUserAvatar);
            mTextViewRepoName.setText(repo.getRepoName());
            mTextViewRepoInfo.setText(repo.getSpannableInfo());

            mImageUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProfileActivity.newIntent(getActivity(), repo.getUserUrl());
                    startActivity(intent);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = RepoActivity.newIntent(getActivity(), repo.getRepoUrl());
                    startActivity(intent);
                }
            });
        }
    }

    private class RepoAdapter extends RecyclerView.Adapter<RepoViewHolder> {
        private ArrayList<Repo> mRepoList;

        public RepoAdapter(ArrayList<Repo> repoList) {
            mRepoList = repoList;
        }

        @NonNull
        @Override
        public RepoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new RepoViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RepoViewHolder holder, int position) {
            holder.bind(mRepoList.get(position));
        }

        @Override
        public int getItemCount() {
            return mRepoList.size();
        }
    }

    private class Repo {
        private String mAvatarUrl;
        private String mRepoName;
        private Spannable mSpannableInfo;

        private int mStarredCount;
        private int mForkedCount;
        private String mUpdatedTime;
        private float mSize;
        private String mLanguage;

        private String mUserUrl;
        private String mRepoUrl;

        public Repo(String avatarUrl, String repoName, int starredCount, int forkedCount, String updatedTime, int size, String language, String userUrl, String repoUrl) {
            mAvatarUrl = avatarUrl;
            mRepoName = repoName;

            mStarredCount = starredCount;
            mForkedCount = forkedCount;
            setTime(updatedTime);
            mSize = size;
            mLanguage = language;

            mUserUrl = userUrl;
            mRepoUrl = repoUrl;

            mSpannableInfo = getImageSpannable(getActivity());
        }

        private Spannable getImageSpannable(Context context) {
            int[] iconIds = {R.drawable.ic_star_border_black_24dp, R.drawable.ic_repo_forked, R.drawable.ic_access_time_black_24dp,
                    R.drawable.ic_size_bulleted_black_24dp};
            int iconIndex = 0;

            String sizeUnit;
            if (mSize >= 1000000) {
                mSize = mSize / 1000000;
                sizeUnit = " GB ";
            } else if (mSize >= 1000) {
                mSize = mSize / 1000;
                sizeUnit = " MB ";
            } else sizeUnit = " KB ";

            DecimalFormat df = new DecimalFormat("#.00");
            String text = " * " + mStarredCount + " * " + mForkedCount + " * " + mUpdatedTime + " * " + df.format(mSize) + sizeUnit + mLanguage;
            Drawable drawable;
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            for (int i = 0; i < builder.length(); i++) {
                if (Character.toString(builder.charAt(i)).equals("*")) {
                    drawable = getResources().getDrawable(iconIds[iconIndex]);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    builder.setSpan(new ImageSpan(drawable), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i++;
                    iconIndex++;
                }
            }
            return builder;
        }

        public void setTime(String time) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            try {
                Date d = formatter.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(d);
                calendar.add(Calendar.HOUR, 7);

                mUpdatedTime = DateUtils.getRelativeTimeSpanString(calendar.getTime().getTime(), new Date().getTime(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public String getAvatarUrl() {
            return mAvatarUrl;
        }

        public String getRepoName() {
            return mRepoName;
        }

        public String getUserUrl() {
            return mUserUrl;
        }

        public String getRepoUrl() {
            return mRepoUrl;
        }

        public Spannable getSpannableInfo() {
            return mSpannableInfo;
        }
    }
}
