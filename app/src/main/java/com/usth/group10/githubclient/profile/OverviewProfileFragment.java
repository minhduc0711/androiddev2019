package com.usth.group10.githubclient.profile;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class OverviewProfileFragment extends Fragment {
    private static final String KEY_USER_URL = "user_url";

    private ProfileActivity mProfileActivity;
    private Button mFollowersButton;
    private Button mFollowingButton;

    private TextView mProfileNameText;
    private TextView mProfileLoginText;
    private CircleImageView mProfileImage;
    private TextView mProfileCreatedDateText;

    public static OverviewProfileFragment newInstance(String userUrl) {
        OverviewProfileFragment overviewProfileFragment = new OverviewProfileFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USER_URL, userUrl);
        overviewProfileFragment.setArguments(args);
        return overviewProfileFragment;
    }

    public OverviewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview_profile, container, false);


        mProfileActivity = (ProfileActivity) getActivity();

        mFollowersButton = view.findViewById(R.id.button_profile_followers);
        mFollowersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileActivity.setPage(5);
            }
        });

        mFollowingButton = view.findViewById(R.id.button_profile_following);
        mFollowingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileActivity.setPage(6);
            }
        });

        // Update Profileimage, Time, Name and Id
        mProfileLoginText = (TextView)view.findViewById(R.id.text_profile_login);
        mProfileNameText = (TextView)view.findViewById(R.id.text_profile_name);
        mProfileImage = view.findViewById(R.id.image_profile);
        mProfileCreatedDateText = (TextView)view.findViewById(R.id.text_profile_date);

        String access_token = getContext().getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                .getString(MySingleton.KEY_ACCESS_TOKEN, "");
        String url = getArguments().getString(KEY_USER_URL) + "?access_token=" + access_token;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Set Name and Login
                    mProfileLoginText.setText(response.getString("login"));

                    if (!response.getString("name").equals("null")) {
                        mProfileNameText.setText(response.getString("name"));
                    } else {
                        mProfileNameText.setVisibility(View.GONE);
                    }

                    //Set Time
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.US);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                    Date date = sdf.parse(response.getString("created_at"));
                    mProfileCreatedDateText.setText(outputFormat.format(date));

                    //Set Image
                    Picasso.get().load(response.getString("avatar_url")).into(mProfileImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Loading profile failed", Toast.LENGTH_SHORT).show();
            }
        }
        );

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

        return view;
    }
}
