package com.usth.group10.githubclient.repository.codePackage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import br.tiagohm.codeview.CodeView;
import br.tiagohm.codeview.Language;
import br.tiagohm.codeview.Theme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.others.MySingleton;

import java.util.HashMap;
import java.util.Map;

public class FileContentActivity extends AppCompatActivity {
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_CONTENT_URL = "content_url";

    private Toolbar mToolbar;
    private FrameLayout mProgressBarLayout;
    private CodeView mCodeView;

    public static Intent newIntent(Context context, String fileName, String contentUrl) {
        Intent intent = new Intent(context, FileContentActivity.class);
        intent.putExtra(KEY_FILE_NAME, fileName);
        intent.putExtra(KEY_CONTENT_URL, contentUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_content);

        mToolbar = findViewById(R.id.toolbar_file_content);
        mToolbar.setTitle(getIntent().getStringExtra(KEY_FILE_NAME));
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProgressBarLayout = findViewById(R.id.progress_bar_layout_file_content);

        mCodeView = findViewById(R.id.code_view_file_content_activity);
        mCodeView.setTheme(Theme.ATOM_ONE_LIGHT).setLanguage(Language.AUTO);
        mCodeView.setOnHighlightListener(new CodeView.OnHighlightListener() {
            @Override
            public void onFinishCodeHighlight() {
                mProgressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onStartCodeHighlight() {}

            @Override
            public void onLineClicked(int i, String s) {}

            @Override
            public void onFontSizeChanged(int i) {}

            @Override
            public void onLanguageDetected(Language language, int i) {}
        });
        fetchFileContent();
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

    private void fetchFileContent() {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        final String contentUrl = getIntent().getStringExtra(KEY_CONTENT_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, contentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mCodeView.setCode(response).apply();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FileContentActivity.this, "Can't fetch file content", Toast.LENGTH_SHORT).show();
                mProgressBarLayout.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Accept", "application/vnd.github.raw.html");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
