package com.usth.group10.githubclient.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.home.FeedsFragment;
import com.usth.group10.githubclient.others.MySingleton;
import com.usth.group10.githubclient.others.NothingHereFragment;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class RepositoriesFragment extends androidx.fragment.app.Fragment {
    private static final String KEY_USER_URL = "user_url";

    private RecyclerView mRepositoriesRecycleView;
    private RecyclerView.Adapter mRepositoriesAdapter;

    public static RepositoriesFragment newInstance(String userUrl) {
        RepositoriesFragment repositoriesFragment = new RepositoriesFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USER_URL, userUrl);
        repositoriesFragment.setArguments(args);
        return repositoriesFragment;
    }

    public RepositoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_feeds, container, false);
        mRepositoriesRecycleView = view.findViewById(R.id.recycler_view_feeds);
        mRepositoriesRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateRepositoriesList();
        return  view;
    }


    private class RepositoriesApdater extends RecyclerView.Adapter<RepositoriesViewHolder>{
        private ArrayList<Repositories> mRepositoriesList;

        private RepositoriesApdater(ArrayList<Repositories> repositoriesList) {
            mRepositoriesList = repositoriesList;
        }


        @NonNull
        @Override
        public RepositoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new RepositoriesViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RepositoriesViewHolder holder, int position) {
            holder.bind(mRepositoriesList.get(position));
        }

        @Override
        public int getItemCount() {
            return mRepositoriesList.size();
        }
    }

    private  class RepositoriesViewHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mFork;
        private TextView mStargazers;
        private TextView mTime;
        private TextView mForksCount;
        private TextView mSize;
        private TextView mLanguage;

        private RepositoriesViewHolder(LayoutInflater inflater,ViewGroup viewGroup){
            super(inflater.inflate(R.layout.item_repositories_list,viewGroup,false));
            mName = itemView.findViewById(R.id.text_repositories_name);
            mFork = itemView.findViewById(R.id.text_repositories_forked);
            mForksCount = itemView.findViewById(R.id.text_repositories_fork_count);
            mStargazers = itemView.findViewById(R.id.text_repositories_star);
            mTime = itemView.findViewById(R.id.text_repositories_time);
            mSize = itemView.findViewById(R.id.text_repositories_size);
            mLanguage = itemView.findViewById(R.id.text_repositories_language);
        }

        private void bind(Repositories repositories){
            mName.setText(repositories.getmName());
            mForksCount.setText(String.valueOf(repositories.getmForksCount()));
            if (!repositories.getmFork()){
                mFork.setVisibility(View.GONE);
            }else{
                mFork.setVisibility(View.VISIBLE);
            }
            mStargazers.setText(String.valueOf(repositories.getMstargazers()));
            mTime.setText(repositories.getmTime());
            mSize.setText(humanReadableByteCount(repositories.getmSize(),false));
            mLanguage.setText(repositories.getmLanguage());
        }

    }


    private  void updateRepositoriesList(){
        String access_token = getContext().getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                .getString(MySingleton.KEY_ACCESS_TOKEN, "");
        String url = getArguments().getString(KEY_USER_URL) + "/repos?access_token=" + access_token;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Repositories> repositoriesList = processRawJson(response);

                        mRepositoriesAdapter = new RepositoriesApdater(repositoriesList);
                        mRepositoriesRecycleView.setAdapter(mRepositoriesAdapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Loading repositories failed", Toast.LENGTH_SHORT).show();

                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private ArrayList<Repositories> processRawJson(JSONArray response){
        JSONObject currentItem;
        ArrayList<Repositories> repositoriesList = new ArrayList<>();
        String mName;
        boolean mFork;

        int mStargazers;
        int mForksCount;

        String mTime;
        long mSize;
        String mLanguage;

        for (int i = 0; i < response.length(); i++) {
            try {
                currentItem = response.getJSONObject(i);

                mName = currentItem.getString("name");
                mFork = currentItem.getBoolean("fork");
                mStargazers = currentItem.getInt("stargazers_count");
                mForksCount = currentItem.getInt("forks_count");
                mTime = currentItem.getString("created_at");
                mSize = currentItem.getLong("size");
                mLanguage = currentItem.getString("language");

                repositoriesList.add(new Repositories(mName,mFork,mStargazers,mTime,mForksCount,mSize,mLanguage));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return repositoriesList;
    }



    private class Repositories{

        private String mName;
        private boolean mFork;
        private int mStargazers;
        private String mTime;
        private int mForksCount;
        private long mSize;
        private String mLanguage;

        private Repositories(String mName, boolean mFork, int mStargazers,String mTime,int mForksCount,long mSize,String mLanguage){
            this.mFork = mFork;
            this.mForksCount = mForksCount;;
            this.mLanguage = mLanguage;
            this.mName = mName;
            this.mStargazers = mStargazers;
            this.mSize = mSize;
            setTime(mTime);
        }

        public boolean getmFork() {
            return mFork;
        }

        public int getmForksCount() {
            return mForksCount;
        }

        public String getmLanguage() {
            return mLanguage;
        }

        public String getmName() {
            return mName;
        }

        public long getmSize() {
            return mSize;
        }

        public int getMstargazers() {
            return mStargazers;
        }

        public String getmTime() {
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

    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


}
