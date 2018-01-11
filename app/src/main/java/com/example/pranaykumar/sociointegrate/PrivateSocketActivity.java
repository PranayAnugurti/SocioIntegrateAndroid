package com.example.pranaykumar.sociointegrate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PrivateSocketActivity extends AppCompatActivity {
    String friendsName;
    String friendsId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras==null){
            return;
        }
        friendsName = extras.getString("friend_name");
        friendsId = extras.getString("friend_id");
        setContentView(R.layout.activity_private_socket);
    }
}
