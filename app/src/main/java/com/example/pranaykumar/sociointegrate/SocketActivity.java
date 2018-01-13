package com.example.pranaykumar.sociointegrate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SocketActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_socket);
    android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setTitle("Group Chat");
    getSupportActionBar().setSubtitle("Typing...");
  }

}
