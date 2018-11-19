package com.usth.group10.githubclient.repository.icons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.usth.group10.githubclient.R;
import com.usth.group10.githubclient.repository.RepoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ShowUserList extends AppCompatActivity {
    String[] name;
    Bitmap[] image;
    ListView listView;
    ArrayList<UserModel> userModels;
    private static CustomAdapter adapter;
    public static final String KEY_SUB_REPO = "repo_url";
    public static final String TITLE = "title";

    public String title;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_user_list);
        listView = findViewById(R.id.user_list);
        userModels = new ArrayList<>();

        adapter = new CustomAdapter(userModels, getApplicationContext());
        listView.setAdapter(adapter);
        adapter.clear();
        adapter.notifyDataSetChanged();

        GetURL getURL = new GetURL();
        getURL.execute(getIntent().getStringExtra(KEY_SUB_REPO));

        title = getIntent().getStringExtra(TITLE);
        TextView titleView = findViewById(R.id.title_icon_long_click);
        titleView.setText(title);


    }

    private class GetURL extends AsyncTask<String, Void, Boolean>{

        URL url;
        String parseContent;

        @Override
        protected Boolean doInBackground(String... strings) {
            String userURL;
            userURL = strings[0];

            try {
                url = new URL(userURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                parseContent = "";

                while (line != null){
                    line = bufferedReader.readLine();
                    parseContent = parseContent + line;
                }

                JSONArray JA = new JSONArray(parseContent);
                name = new String[JA.length()];
                image = new Bitmap[JA.length()];


                if(title != "Forks"){
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject user = JA.getJSONObject(i);
                        name[i] = user.getString("login");
                        URL imageURL = new URL(user.getString("avatar_url"));
                        image[i] = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    }
                } else if (title == "Forks"){
                    for (int i = 0; i < JA.length(); i++){
                        JSONObject user = JA.getJSONObject(i);
                        JSONObject owner = user.getJSONObject("owner");
                        name[i] = owner.getString("login");
                        URL imageURL = new URL(owner.getString("avatar_url"));
                        image[i] = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    }
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean) {
                Toast.makeText(ShowUserList.this, "Error Network", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < name.length; i++) {
                    UserModel user = new UserModel(name[i], image[i]);
                    userModels.add(user);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
    public class UserModel {
        String name;
        Bitmap image;

        public UserModel(String name, Bitmap image) {
            this.name = name;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public Bitmap getImage() {
            return image;
        }
    }

    public class CustomAdapter extends ArrayAdapter<UserModel> implements View.OnClickListener{
        private ArrayList<UserModel> dataset;
        Context mContext;

        private class ViewHolder {
            TextView userName;
            ImageView userAvatar;
            LinearLayout userRow;
        }

        public CustomAdapter(ArrayList<UserModel> data, Context context) {
            super(context, R.layout.user_list_action, data);
            this.dataset = data;
            this.mContext = context;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.user_row:
                    Toast.makeText(ShowUserList.this, "Hello", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserModel userModel = getItem(position);

            ViewHolder viewHolder;

            final View result;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(mContext);

                convertView = inflater.inflate(R.layout.user_list_action, parent, false);
                viewHolder.userName = convertView.findViewById(R.id.user_name);
                viewHolder.userAvatar = convertView.findViewById(R.id.user_avatar);
                viewHolder.userRow = convertView.findViewById(R.id.user_row);

                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            viewHolder.userName.setText(userModel.getName());
            viewHolder.userAvatar.setImageBitmap(userModel.getImage());

            viewHolder.userRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Do smth here
                }
            });

            return result;

        }
    }
}
