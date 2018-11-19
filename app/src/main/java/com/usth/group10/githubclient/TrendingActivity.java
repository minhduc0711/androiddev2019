package com.usth.group10.githubclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.usth.group10.githubclient.others.MySingleton;
import com.usth.group10.githubclient.repository.RepoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class TrendingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FrameLayout mProgressBarLayout;
    private AppCompatSpinner mPeriodSpinner;
    private RecyclerView mRecyclerView;

    public static Intent newIntent(Context context) {
        return new Intent(context, TrendingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        mToolbar = findViewById(R.id.toolbar_trending);
        mToolbar.setTitle(R.string.title_activity_trending);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProgressBarLayout = findViewById(R.id.progress_bar_layout_trending);

        mPeriodSpinner = findViewById(R.id.spinner_period_trending);
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(this, R.array.trending_periods,
                android.R.layout.simple_spinner_item);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPeriodSpinner.setAdapter(periodAdapter);
        mPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String period = parent.getItemAtPosition(position).toString().toLowerCase();
                fetchRepos(period);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TrendingActivity.this, "No period selected", Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view_trending);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchRepos(final String period) {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        String url = "https://github-trending-api.now.sh/repositories?since=" + period;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Repo> repoList = processJsonArray(response, period);
                        RecyclerView.Adapter adapter = new RepoAdapter(repoList);
                        mRecyclerView.setAdapter(adapter);
                        mProgressBarLayout.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TrendingActivity.this, "Loading branches failed", Toast.LENGTH_SHORT).show();
                        mProgressBarLayout.setVisibility(View.GONE);
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private ArrayList<Repo> processJsonArray(JSONArray jsonArray, String period) {
        ArrayList<Repo> repoList = new ArrayList<>();
        JSONObject currentItem;

        String name;
        String description;
        int stars;
        int forks;
        int currentPeriodStars;
        String language;
        String languageColor;
        String htmlUrl;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                currentItem = jsonArray.getJSONObject(i);
                name = currentItem.getString("author") + "/" + currentItem.getString("name");
                description = currentItem.getString("description");
                stars = currentItem.getInt("stars");
                forks = currentItem.getInt("forks");
                currentPeriodStars = currentItem.getInt("currentPeriodStars");
                language = currentItem.getString("language");
                languageColor = currentItem.getString("languageColor");
                htmlUrl = currentItem.getString("url");
                repoList.add(new Repo(name, description, stars, forks, currentPeriodStars, language, languageColor, htmlUrl, period));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return repoList;
    }

    private class RepoViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewRepoName;
        private TextView mTextViewRepoDescription;
        private TextView mTextViewRepoInfo;

        private RepoViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_trending_list, parent, false));
            mTextViewRepoName = itemView.findViewById(R.id.text_repo_name_trending);
            mTextViewRepoDescription = itemView.findViewById(R.id.text_repo_description_trending);
            mTextViewRepoInfo = itemView.findViewById(R.id.text_repo_info_trending);
        }

        private void bind(final Repo repo) {
            mTextViewRepoName.setText(repo.getName());
            mTextViewRepoDescription.setText(repo.getDescription());
            mTextViewRepoInfo.setText(repo.getSpannableInfo());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = RepoActivity.newIntent(TrendingActivity.this, repo.getUrl());
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
            LayoutInflater layoutInflater = LayoutInflater.from(TrendingActivity.this);
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
        private String mName;
        private String mDescription;
        private Spannable mSpannableInfo;
        private String mUrl;

        private Repo(String name, String description, int stars, int forks, int currentPeriodStars, String language, String languageColor, String htmlUrl, String period) {
            mName = name;
            mDescription = description;
            setSpannableInfo(stars, forks, currentPeriodStars, language, languageColor, period);
            mUrl = "https://api.github.com/repos/" + mName;
        }

        private void setSpannableInfo(int stars, int forks, int currentPeriodStars, String language, String languageColor, String period) {
            int[] iconIds = {R.drawable.ic_star_border_black_24dp, R.drawable.ic_repo_forked,
                    R.drawable.ic_star_border_black_24dp, R.drawable.ic_language};
            int iconIndex = 0;

            switch (period) {
                case "daily":
                    period = " stars today ";
                    break;
                case "weekly":
                    period = " stars this week ";
                    break;
                case "monthly":
                    period = " stars this month ";
                    break;
            }

            int languageColorId = Color.parseColor(languageColor);
            String text = " * " + stars + " * " + forks + " * " + currentPeriodStars + period + "* " + language;
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
            builder.setSpan(new ForegroundColorSpan(languageColorId),
                    builder.length() - language.length(),
                    builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mSpannableInfo = builder;
        }

        public String getName() {
            return mName;
        }

        public String getDescription() {
            return mDescription;
        }

        public Spannable getSpannableInfo() {
            return mSpannableInfo;
        }

        public String getUrl() {
            return mUrl;
        }
    }
}
