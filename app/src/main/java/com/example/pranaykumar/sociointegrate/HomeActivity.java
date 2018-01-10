package com.example.pranaykumar.sociointegrate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

  private Button friendsBtn,groupChatBtn;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    friendsBtn=(Button)findViewById(R.id.friendsTextView);
    groupChatBtn=(Button)findViewById(R.id.groupChatTextView);

    friendsBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent=new Intent(HomeActivity.this,FriendsActivity.class);
        intent.putExtra("user_id",getIntent().getStringExtra("user_id"));
        startActivity(intent);
      }
    });

    groupChatBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent=new Intent(HomeActivity.this,SocketActivity.class);
        intent.putExtra("user_id",Constants.user_id);
        startActivity(intent);
      }
    });
  }
}
