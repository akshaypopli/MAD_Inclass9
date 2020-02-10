package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button btn_login;
    Button btn_signUp;
    EditText et_email;
    EditText et_pwd;
    String pwd, user;
    final static String TOKEN = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Mailer");
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);
        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = et_email.getText().toString();
                pwd = et_pwd.getText().toString();
                    if (user.equals("")) {
                        et_email.setError("Invalid Input");
                    } else if (pwd.equals("")) {
                        et_pwd.setError("Invalid Input");
                    } else {
                        try {
                            login();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }


    public void login() throws IOException {
        final OkHttpClient client = new OkHttpClient();
        RequestBody formbody = new FormBody.Builder()
                .add("email", et_email.getText().toString()).add("password", et_pwd.getText().toString()).build();

        Request request = new Request.Builder().url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
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
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                JsonParser parser = new JsonParser();
                JsonElement jsonTree = parser.parse(mMessage);
                JsonObject jsonObject = jsonTree.getAsJsonObject();
                JsonElement token = jsonObject.get("token");
                JsonElement fn = jsonObject.get("user_fname");
                JsonElement ln = jsonObject.get("user_lname");
                editor.putString("sessionToken", token.toString());
                editor.apply();

                Intent i = new Intent(MainActivity.this, InboxActivity.class);
                i.putExtra("first", fn.toString());
                i.putExtra("last", ln.toString());
                startActivity(i);
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
