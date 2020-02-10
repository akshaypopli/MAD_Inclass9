package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InboxActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String token;
    ImageView iv_logout;
    ImageView iv_new;
    ArrayList<InboxBody> allMails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Inbox");
        TextView tv_fullname = findViewById(R.id.tv_fullname);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            String fullname = getIntent().getExtras().getString("first").replaceAll("^\"|\"$", "") + " " + getIntent().getExtras().getString("last").replaceAll("^\"|\"$", "");
            tv_fullname.setText(fullname);
        }

        iv_logout = findViewById(R.id.iv_logout);
        iv_new = findViewById(R.id.iv_new);


        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InboxActivity.this);
        String token = sharedPreferences.getString("sessionToken", null);


        if (token != null) {
            token = token.replaceAll("^\"|\"$", "");
            inboxData(token);
        }

//        mLayoutManager = new LinearLayoutManager(InboxActivity.this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new InboxAdapter(allMails);
//        mRecyclerView.setAdapter(mAdapter);

        iv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InboxActivity.this, NewMail.class);
                startActivity(intent);
            }
        });

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().remove("sessionToken").commit();
                finish();
            }
        });

    }


    OkHttpClient client = new OkHttpClient();

    public void inboxData(String token) {
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox")
                .addHeader("Authorization", "BEARER " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.d("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String data = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray messages = jsonObject.getJSONArray("messages");
                    Log.d("message", messages.toString());
                    if (messages.length() != 0) {
                        for (int i = 0; i < messages.length(); i++) {
                            InboxBody body = new InboxBody();
                            JSONObject messageeJSON = messages.getJSONObject(i);
                            body.subject = messageeJSON.getString("subject");
                            body.date = messageeJSON.getString("updated_at");
                            //body.id = messageeJSON.getString("id");
                            allMails.add(body);
                        }

                        InboxActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Handle UI here

                                mRecyclerView = findViewById(R.id.recyclerView);
                                mRecyclerView.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(InboxActivity.this);
                                mRecyclerView.setLayoutManager(mLayoutManager);
                                mAdapter = new InboxAdapter(allMails);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });


                    } else {
//                        Toast.makeText(getApplicationContext(), "No mails to show", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
