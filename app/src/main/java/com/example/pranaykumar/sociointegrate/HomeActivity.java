package com.example.pranaykumar.sociointegrate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class HomeActivity extends AppCompatActivity {

  private Button friendsBtn,groupChatBtn;

  private Socket socket;

  {
    try {
      //socket=IO.socket("https://sociointegrate.herokuapp.com/");
      socket = IO.socket(Constants.server_url);
      Log.d("LOG", "server_url=" + Constants.server_url);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    friendsBtn=(Button)findViewById(R.id.friendsTextView);
    groupChatBtn=(Button)findViewById(R.id.groupChatTextView);

    friendsBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        JSONObject sendText = new JSONObject();
        String user_id = null;
        Constants constants  = new Constants();
        user_id = constants.user_id;
        try {
          sendText.put("id",user_id);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        socket.connect();
        socket.emit("hello",sendText);

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
