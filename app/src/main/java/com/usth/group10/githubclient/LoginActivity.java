package com.usth.group10.githubclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public static final String PREF_ACCESS_TOKEN = "pref_access_token";
    public static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String clientId = "ea654ab9b8b11cbb932d";
    private static final String clientSecret = "a44b767cda60c324b9c5aeff755bb8a1953b978d";
    private static final String redirectUri = "githublite://callback";

    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = findViewById(R.id.button_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://github.com/login/oauth/authorize" +
                                                        "?client_id=" + clientId +
                                                        "&scope=repo%20user" +
                                                        "&redirect_uri=" + redirectUri));
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
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST,"https://github.com/login/oauth/access_token", new Response.Listener<String>() {
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
                    SharedPreferences.Editor editor = getSharedPreferences(PREF_ACCESS_TOKEN, MODE_PRIVATE).edit();
                    editor.putString(KEY_ACCESS_TOKEN, access_token);
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
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);
                params.put("code", code);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }
}
