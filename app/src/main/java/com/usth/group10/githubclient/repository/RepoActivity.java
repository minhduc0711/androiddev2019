package com.usth.group10.githubclient.repository;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.usth.group10.githubclient.R;


public class RepoActivity extends AppCompatActivity {

    private static final String TAG = "RepoActivity";

    private ImageButton popupButton;
    private ImageButton backButton;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        mBottomNavigationView = findViewById(R.id.bottom_nav_repo);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new CodeFragment()).commit();
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment newFragment;
                switch (menuItem.getItemId()) {
                    case R.id.item_bottom_nav_repo_code:
                        Log.d(TAG, "Fragment for feeds created");
                        newFragment = new CodeFragment();
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
