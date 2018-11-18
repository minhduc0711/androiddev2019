package com.usth.group10.githubclient.repository;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.usth.group10.githubclient.R;


public class RepoActivity extends AppCompatActivity {
    private static final String TAG = "RepoActivity";
    public static final String KEY_REPO_URL = "repo_url";

    private String mRepoUrl;

    private ImageButton popupButton;
    private ImageButton backButton;
    private BottomNavigationView mBottomNavigationView;

    public static Intent newIntent(Context context, String repoUrl) {
        Intent intent = new Intent(context, RepoActivity.class);
        intent.putExtra(KEY_REPO_URL, repoUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        mRepoUrl = getIntent().getStringExtra(KEY_REPO_URL);

        mBottomNavigationView = findViewById(R.id.bottom_nav_repo);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, CodeFragment.newInstance(mRepoUrl)).commit();
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment newFragment;
                switch (menuItem.getItemId()) {
                    case R.id.item_bottom_nav_repo_code:
                        Log.d(TAG, "Fragment for feeds created");
                        newFragment = CodeFragment.newInstance(mRepoUrl);
                        break;
                    case R.id.item_bottom_nav_repo_issues:
                        newFragment = new IssuesRepoFragment();
                        break;
                    case R.id.item_bottom_nav_repo_pull_requests:
                        newFragment = new PullRequestsFragment();
                        break;
                    case R.id.item_bottom_nav_repo_projects:
                        newFragment = new ProjectsFragment();
                        break;
                    default:
                        newFragment = new Fragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).commit();
                return true;
            }
        });
        mBottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                // Do nothing
            }
        });

//      Back Button
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//      Popop Menu
        popupButton = findViewById(R.id.popup_menu);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(RepoActivity.this, popupButton);
                popupMenu.getMenuInflater()
                        .inflate(R.menu.more_info_repo, popupMenu.getMenu());

//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Toast.makeText(
//                                RepoActivity.this,
//                                item.getTitle(),
//                                Toast.LENGTH_LONG
//                        ).show();
//                        return true;
//                    }
//                });
                popupMenu.show();
            }
        });
    }
}
