package com.usth.group10.githubclient.repository.codePackage;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.usth.group10.githubclient.others.NothingHereFragment;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class ReadmeFragment extends Fragment {
    private static final String KEY_REPO_URL = "repo_url";

    private WebView mWebView;

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
        mWebView = new WebView(getActivity());
        mWebView = view.findViewById(R.id.readmeContent);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        requestReadmeContent();

        return view;
    }

    public void requestReadmeContent() {
        String url = getArguments().get(KEY_REPO_URL) + "/readme";
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = "<!DOCTYPE html>\n" +
                                "<html lang=\"en\">\n" +
                                "<head>\n" +
                                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                                "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                                "    \n" +
                                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" +
                                "\n" +
                                "\n" +
                                "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\n" +
                                "\n" +
                                "\n" +
                                "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script> \n" +
                                "</head>\n" +
                                "<body>" + response + "</body>\n" +
                                "</html>\n";
                        mWebView.loadData(response,"text/html","UTF-8");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Loading readme failed", Toast.LENGTH_LONG).show();
                        NothingHereFragment.newInstance("Readme");
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
