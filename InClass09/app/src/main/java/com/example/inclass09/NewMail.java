package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewMail extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btn_cancel;
    Spinner spinner;
    String selectedUser;
    EditText et_subject;
    EditText et_msg;
    String token;
    String msg;
    ArrayList<String> allUsers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mail);
        setTitle("Create New Mail");

        et_subject = findViewById(R.id.et_subject);
        et_msg = findViewById(R.id.et_msg);
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewMail.this);
        token = sharedPreferences.getString("sessionToken", null);
        userData(token.replaceAll("^\"|\"$", ""));



        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = et_msg.getText().toString();
                if (msg.equals("")) {
                    et_msg.setError("Empty Field!");
                } else{
                    try {
                        send();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }


    public void send() throws IOException {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formbody = new FormBody.Builder()
                .add("receiver_id", selectedUser).add("subject", et_subject.getText().toString()).add("message", et_msg.getText().toString()).build();

        Request request = new Request.Builder().url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                .addHeader("Authorization","BEARER "+ token)
                .post(formbody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.d("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String mMessage = response.body().string();
                Log.d("message", mMessage);
                finish();
            }
        });
    }

    OkHttpClient client = new OkHttpClient();

    public void userData(String token) {
        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                .addHeader("Authorization", "BEARER "+token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.d("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String  data = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray usersJson = jsonObject.getJSONArray("users");
                    if(usersJson.length()!=0){
                        final String[] users = new String[usersJson.length()];
                        for (int i=0; i<usersJson.length();i++){
                            User newUser = new User();
                            JSONObject userObj = usersJson.getJSONObject(i);
                            newUser.fname = userObj.getString("fname");
                            newUser.lname = userObj.getString("lname");
                            newUser.id = userObj.getString("id");
                            users[i]=newUser.toString();
                            allUsers.add(newUser.id);
                        }
                        Log.d("users", users.toString());

                        NewMail.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Handle UI here
                                ArrayAdapter adapter = new ArrayAdapter<>(NewMail.this, android.R.layout.simple_spinner_item, users);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                            }
                        });



                    }else {
                        //Toast.makeText(getApplicationContext(), "No users to show", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedUser = allUsers.get(i);
        Log.d("id", selectedUser);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
