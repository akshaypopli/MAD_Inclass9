package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    Button btn_cancel;
    Button button_signUp;
    EditText et_first;
    EditText et_last;
    EditText et_email;
    EditText et_choosePwd;
    EditText et_repeatPwd;

    String fname, lname, email, pwd1, pwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        btn_cancel = findViewById(R.id.btn_cancel);
        button_signUp = findViewById(R.id.button_signUp);
        et_first = findViewById(R.id.et_first);
        et_last = findViewById(R.id.et_last);
        et_email = findViewById(R.id.et_email);
        et_choosePwd = findViewById(R.id.et_choosePwd);
        et_repeatPwd = findViewById(R.id.et_repeatPwd);



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = et_first.getText().toString();
                lname = et_last.getText().toString();
                email = et_email.getText().toString();
                pwd1 = et_choosePwd.getText().toString();
                pwd2 = et_repeatPwd.getText().toString();
                    if (fname.equals("")) {
                        et_first.setError("Invalid Input");
                    } else if (lname.equals("")) {
                        et_last.setError("Invalid Input");
                    } else if (email.equals("")) {
                        et_email.setError("Invalid Input");
                    } else if (pwd1.equals("")) {
                        et_choosePwd.setError("Invalid Input");
                    } else if (pwd2.equals("")) {
                        et_repeatPwd.setError("Invalid Input");
                    } else if (!pwd1.equals(pwd2)) {
                        Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            signUp();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

            }
        });
    }

    public void signUp() throws IOException {
        final OkHttpClient client = new OkHttpClient();

        //checks to do
        RequestBody formbody = new FormBody.Builder()
                .add("lname", et_last.getText().toString())
                .add("fname", et_first.getText().toString())
                .add("email", et_email.getText().toString())
                .add("password", et_repeatPwd.getText().toString()).build();

        Request request = new Request.Builder().url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
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
                Log.d("response", mMessage);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                JsonParser parser = new JsonParser();
                JsonElement jsonTree = parser.parse(mMessage);
                JsonObject jsonObject = jsonTree.getAsJsonObject();
                JsonElement token = jsonObject.get("token");
                editor.putString("sessionToken", token.toString());
                Log.d("token", token.toString());
                editor.apply();

                Intent i = new Intent(SignupActivity.this, InboxActivity.class);
                startActivity(i);
            }
        });
    }
}
