package com.usth.group10.githubclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.usth.group10.githubclient.others.MySingleton;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String clientId = "ea654ab9b8b11cbb932d";
    private static final String clientSecret = "a44b767cda60c324b9c5aeff755bb8a1953b978d";
    private static final String redirectUri = "githublite://callback";

    private Button mLoginButton;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String access_token = getSharedPreferences(MySingleton.PREF_LOGIN_INFO, Context.MODE_PRIVATE)
                .getString(MySingleton.KEY_ACCESS_TOKEN, "");
        if (!access_token.equals("")) {
            finish();
            Intent intent = MainActivity.newIntent(this);
            startActivity(intent);
        }

        mLoginButton = findViewById(R.id.button_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/login/oauth/authorize" +
                                "?client_id=" + clientId +
                                "&scope=repo%20user" +
                                "&redirect_uri=" + redirectUri));
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            String code = uri.getQueryParameter("code");
            getAccessToken(code);
            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAccessToken(final String code) {
        StringRequest sr = new StringRequest(Request.Method.POST, "https://github.com/login/oauth/access_token", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String access_token = null;
                String[] params = response.split("&");
                for (String p : params) {
                    if (p.startsWith("access_token")) {
                        access_token = p.replace("access_token=", "");
                    }
                }

                if (access_token != null) {
                    SharedPreferences.Editor editor = getSharedPreferences(MySingleton.PREF_LOGIN_INFO, MODE_PRIVATE).edit();
                    editor.putString(MySingleton.KEY_ACCESS_TOKEN, access_token);
                    editor.apply();

                    Intent intent = MainActivity.newIntent(LoginActivity.this);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Null token", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Can not get access token", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);
                params.put("code", code);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(sr);
    }
}
