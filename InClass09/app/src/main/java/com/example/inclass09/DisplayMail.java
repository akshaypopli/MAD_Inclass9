package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayMail extends AppCompatActivity {
    Button btn_close;
    TextView tv_subjectDisplay;
    TextView tv_createdAtDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mail);
        setTitle("Message");
        tv_subjectDisplay = findViewById(R.id.tv_subjectDisplay);
        tv_createdAtDisplay = findViewById(R.id.tv_createdAtDisplay);
        if(getIntent() !=null&& getIntent().getExtras()!=null){
            tv_subjectDisplay.setText(getIntent().getExtras().getString("subject"));
            tv_createdAtDisplay.setText(getIntent().getExtras().getString("date"));
        }

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
