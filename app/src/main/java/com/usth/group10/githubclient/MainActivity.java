package com.usth.group10.githubclient;

import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private Toolbar mMainToolbar;
    private NavigationView mNavigationView;
    private int mCurrentSelectedItemResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mMainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mMainToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);

        mNavigationView = findViewById(R.id.nav_view_main);
        mNavigationView.setCheckedItem(R.id.item_drawer_home);
        mCurrentSelectedItemResId = R.id.item_drawer_home;
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_main, new HomeFragment()).commit();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                // Swap fragments if switch to new item
                if (menuItem.getItemId() != mCurrentSelectedItemResId) {
                    mCurrentSelectedItemResId = menuItem.getItemId();
                    Fragment newFragment;
                    switch (menuItem.getItemId()) {
                        case R.id.item_drawer_home:
                            Log.d(TAG, "Home fragment created");
                            newFragment = new HomeFragment();
                            mMainToolbar.setTitle(R.string.app_name);
                            break;
                        case R.id.item_drawer_profile:
                            newFragment = new ProfileFragment();
                            mMainToolbar.setTitle(menuItem.getTitle());
                            break;
                        case R.id.item_drawer_trending:
                            newFragment = new Fragment();
                            mMainToolbar.setTitle(menuItem.getTitle());
                            break;
                        default:
                            newFragment = new Fragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_main, newFragment).commit();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationView.getCheckedItem().getItemId() != R.id.item_drawer_home) {
            mNavigationView.setCheckedItem(R.id.item_drawer_home);
            mCurrentSelectedItemResId = R.id.item_drawer_home;
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_main, new HomeFragment()).commit();
        } else {
            finish();
        }
    }
}
