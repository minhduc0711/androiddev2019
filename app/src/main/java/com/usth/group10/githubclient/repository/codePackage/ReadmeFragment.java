package com.usth.group10.githubclient.repository.codePackage;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class ReadmeFragment extends Fragment {
    private static final String KEY_REPO_URL = "repo_url";

    private HtmlTextView mHtmlTextView;

    public ReadmeFragment() {
        // Required empty public constructor
    }

    public static ReadmeFragment newInstance(String repoUrl) {
        ReadmeFragment ReadmeFragment = new ReadmeFragment();
        Bundle args = new Bundle();
        args.putString(KEY_REPO_URL, repoUrl);
        ReadmeFragment.setArguments(args);
        return ReadmeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_readme, container, false);

        mHtmlTextView = view.findViewById(R.id.readmeContent);

        requestReadmeContent();

        return view;
    }

    public void requestReadmeContent() {
        String url = getArguments().get(KEY_REPO_URL) + "/readme";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mHtmlTextView.setHtml(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Loading readme failed", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/vnd.github.v3.html");
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}
