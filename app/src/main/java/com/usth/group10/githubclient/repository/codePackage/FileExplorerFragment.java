package com.usth.group10.githubclient.repository.codePackage;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileExplorerFragment extends Fragment {
    private static final String KEY_REPO_URL = "repo_url";
    private static final String TAG = "FileExplorerFragment";

    private String mRepoUrl;
    private String mDefaultBranch;
    private Stack<String> mContentUrlStack = new Stack<>();
    private String mCurrentContentUrl;

    private FrameLayout mProgressBarLayout;
    private AppCompatSpinner mBranchesSpinner;
    private RecyclerView mRecyclerView;
    private ContentAdapter mContentAdapter;

    public static FileExplorerFragment newInstance(String repoUrl) {
        FileExplorerFragment fileExplorerFragment = new FileExplorerFragment();
        Bundle args = new Bundle();
        args.putString(KEY_REPO_URL, repoUrl);
        fileExplorerFragment.setArguments(args);
        return fileExplorerFragment;
    }

    public FileExplorerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRepoUrl = getArguments().getString(KEY_REPO_URL);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_explorer, container, false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mContentUrlStack.size() != 0) {
                            fetchContentList(mContentUrlStack.pop());
                        } else {
                            getActivity().finish();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout_file_explorer);

        mBranchesSpinner = view.findViewById(R.id.spinner_branches);
        mBranchesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBranch = (String) parent.getItemAtPosition(position);
                fetchContentList(mRepoUrl + "/contents?ref=" + selectedBranch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView = view.findViewById(R.id.recycler_view_file_explorer);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContentAdapter = new ContentAdapter(new ArrayList<Content>());
        mRecyclerView.setAdapter(mContentAdapter);

        fetchDefaultBranch();

        return view;
    }

    private class ContentAdapter extends RecyclerView.Adapter<ContentViewHolder> {
        private ArrayList<Content> mContentList;

        private ContentAdapter(ArrayList<Content> contentList) {
            mContentList = contentList;
        }

        private ArrayList<Content> getContentList() {
            return mContentList;
        }

        @NonNull
        @Override
        public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ContentViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
            holder.bind(mContentList.get(position));
        }

        @Override
        public int getItemCount() {
            return mContentList.size();
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageContentIcon;
        private TextView mContentNameTextView;

        private ContentViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_file_explorer, parent, false));
            mImageContentIcon = itemView.findViewById(R.id.image_file_explorer_content);
            mContentNameTextView = itemView.findViewById(R.id.text_file_explorer_content_name);
        }

        private void bind(final Content content) {
            if (content.getType().equals("file")) {
                mImageContentIcon.setImageResource(R.drawable.ic_file);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = FileContentActivity.newIntent(getActivity(), content.getName(), content.getUrl());
                        startActivity(intent);
                    }
                });
            } else {
                mImageContentIcon.setImageResource(R.drawable.ic_folder);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContentUrlStack.push(mCurrentContentUrl);
                        fetchContentList(content.getUrl());
                    }
                });
            }

            mContentNameTextView.setText(content.getName());
        }
    }

    private void fetchDefaultBranch() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, mRepoUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mDefaultBranch = response.getString("default_branch");
                            fetchBranches();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Fetching default branch failed", Toast.LENGTH_SHORT).show();
                        mProgressBarLayout.setVisibility(View.GONE);
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private void fetchBranches() {
        String branchesUrl = mRepoUrl + "/branches";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, branchesUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> branches = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                branches.add(response.getJSONObject(i).getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_dropdown_item, branches);
                        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mBranchesSpinner.setAdapter(branchAdapter);
                        mBranchesSpinner.setSelection(branchAdapter.getPosition(mDefaultBranch));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Loading branches failed", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void fetchContentList(String url) {
        Log.d(TAG, url);
        mProgressBarLayout.setVisibility(View.VISIBLE);
        mCurrentContentUrl = url;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Content> contentList = processRawJson(response);
                        mContentAdapter.getContentList().clear();
                        mContentAdapter.getContentList().addAll(contentList);
                        mContentAdapter.notifyDataSetChanged();
                        mProgressBarLayout.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Loading file explorer failed", Toast.LENGTH_SHORT).show();
                        mProgressBarLayout.setVisibility(View.GONE);
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private ArrayList<Content> processRawJson(JSONArray jsonArray) {
        ArrayList<Content> contentList = new ArrayList<>();
        JSONObject currentItem;

        String name;
        String url;
        String type;
        int size;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                currentItem = jsonArray.getJSONObject(i);
                name = currentItem.getString("name");
                url = currentItem.getString("url");
                type = currentItem.getString("type");
                size = currentItem.getInt("size");
                contentList.add(new Content(name, url, type, size));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(contentList, new Comparator<Content>() {
            @Override
            public int compare(Content o1, Content o2) {
                if (o1.getType().equals("dir") && o2.getType().equals("file")) return -1;
                else return (o1.getName().compareTo(o2.getName()));
            }
        });
        return contentList;
    }

    private class Content {
        private String mName;
        private String mUrl;
        private String mType;
        private float mSize;

        private Content(String name, String url, String type, float size) {
            mName = name;
            mUrl = url;
            mType = type;
            mSize = size;
        }

        public String getName() {
            return mName;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getType() {
            return mType;
        }

        public float getSize() {
            return mSize;
        }
    }
}
