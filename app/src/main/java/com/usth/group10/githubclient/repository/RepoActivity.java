package com.usth.group10.githubclient.repository;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.repository.icons.AskForkFragment;
import com.usth.group10.githubclient.repository.icons.ShowUserList;
import com.usth.group10.githubclient.repository.icons.TransmitDataDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RepoActivity extends AppCompatActivity implements TransmitDataDialog {
    private static final String TAG = "RepoActivity";
    public static final String KEY_REPO_URL = "repo_url";


    int starredCount, subscriberCount, forkedCount;

    private String mRepoUrl;

    private ImageButton popupButton;
    private ImageButton backButton;
    private BottomNavigationView mBottomNavigationView;


    private LinearLayout subscriberButton;
    private ImageButton subscriberSymbol;
    private TextView number_of_subscribers;

    private LinearLayout starredButton;
    private ImageButton starSymbol;
    private TextView number_of_stars;

    private LinearLayout forkedButton;
    private ImageButton forkedSymbol;
    private TextView number_of_forks;

    private LinearLayout pinButton;
    private ImageButton pinSymbol;

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

//Back Button
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//SUBSCRIBER
        subscriberSymbol = findViewById(R.id.subscriber_symbol);
        number_of_subscribers = findViewById(R.id.number_of_subscribers);

        subscriberButton = findViewById(R.id.subscriber_button);
        subscriberSymbol.setImageResource(R.drawable.ic_repo_eye);

//STAR
        starSymbol = findViewById(R.id.stars_symbol);
        number_of_stars = findViewById(R.id.number_of_stars);

        starredButton = findViewById(R.id.starred_button);
        starSymbol.setImageResource(R.drawable.ic_repo_star);

//FORK
        forkedSymbol = findViewById(R.id.forked_symbol);
        number_of_forks = findViewById(R.id.number_of_forks);

        forkedButton = findViewById(R.id.forked_button);
        forkedSymbol.setImageResource(R.drawable.ic_repo_forked);

//PIN
        pinSymbol = findViewById(R.id.pin_symbol);
        pinButton = findViewById(R.id.pin_button);
        pinSymbol.setImageResource(R.drawable.ic_repo_pin);


        // Background
        FindCountTask findCountTask = new FindCountTask();
        findCountTask.execute(getIntent().getExtras().get(KEY_REPO_URL).toString());

        subscriberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());

                if (view.isSelected()){
                    subscriberSymbol.setColorFilter(Color.BLUE);
//                    subscriberCount += 1;

                } else {
                    subscriberSymbol.setColorFilter(Color.BLACK);
//                    subscriberCount -= 1;
                }
                number_of_subscribers.setText(String.valueOf(subscriberCount));
            }
        });

        subscriberButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent1 = new Intent(RepoActivity.this, ShowUserList.class);
                intent1.putExtra(ShowUserList.KEY_SUB_REPO, getIntent().getExtras().get(KEY_REPO_URL).toString()+"/subscribers");

                intent1.putExtra(ShowUserList.TITLE, "Watchers");

                startActivity(intent1);
                return true;
            }
        });

        starredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());

                if (view.isSelected()) {
                    starSymbol.setImageResource(R.drawable.ic_menu_star_full_color);
                    starSymbol.setColorFilter(Color.BLUE);
//                    starredCount += 1;
                } else {
                    starSymbol.setImageResource(R.drawable.ic_repo_star);
                    starSymbol.setColorFilter(Color.BLACK);
//                    starredCount -= 1;
                }
                number_of_stars.setText(String.valueOf(starredCount));

            }
        });

        starredButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent1 = new Intent(RepoActivity.this, ShowUserList.class);
                intent1.putExtra(ShowUserList.KEY_SUB_REPO, getIntent().getExtras().get(KEY_REPO_URL).toString()+"/stargazers");
                intent1.putExtra(ShowUserList.TITLE, "Stars");
                startActivity(intent1);
                return true;
            }
        });

        forkedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());

                AskForkFragment askForkFragment = new AskForkFragment();
                askForkFragment.show(getSupportFragmentManager(),  "dialog");
            }

        });

        forkedButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent1 = new Intent(RepoActivity.this, ShowUserList.class);
                intent1.putExtra(ShowUserList.KEY_SUB_REPO, getIntent().getExtras().get(KEY_REPO_URL).toString()+"/forks");
                intent1.putExtra(ShowUserList.TITLE, "Forks");
                startActivity(intent1);
                return true;
            }
        });

        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());

                if(view.isSelected()){
                    pinSymbol.setImageResource(R.drawable.ic_repo_pin_full_color);
                    pinSymbol.setColorFilter(Color.BLUE);
                } else {
                    pinSymbol.setColorFilter(Color.BLACK);
                    pinSymbol.setImageResource(R.drawable.ic_repo_pin);
                }
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

//For FORK
    @Override
    public void TransmitData(boolean userAnswer) {
        if (userAnswer) {
            forkedSymbol.setColorFilter(Color.BLUE);
        } else {
            forkedSymbol.setColorFilter(Color.BLACK);
        }
        number_of_forks.setText(String.valueOf(forkedCount));
    }


    private class FindCountTask extends AsyncTask<String, Void, Boolean> {
        URL url;

        String parseContent;

        protected Boolean doInBackground(String... repo) {
            try {
                url = new URL(repo[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                parseContent = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    parseContent = parseContent + line;
                }

                // Get count
                JSONObject JO = new JSONObject(parseContent);
                starredCount = JO.getInt("stargazers_count");
                forkedCount = JO.getInt("forks_count");
                subscriberCount = JO.getInt("subscribers_count");




            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(RepoActivity.this, "Error network", Toast.LENGTH_SHORT).show();
            } else {
                number_of_stars.setText(String.valueOf(starredCount));
                number_of_forks.setText(String.valueOf(forkedCount));
                number_of_subscribers.setText(String.valueOf(subscriberCount));
            }
        }
    }
}


