package com.example.pranaykumar.sociointegrate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.emitter.Emitter.Listener;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class HomeActivity extends AppCompatActivity {

  private Button friendsBtn,groupChatBtn;

  public static Socket socket;

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
    final JSONObject sendText = new JSONObject();
    String user_id = null;
    Constants constants  = new Constants();
    user_id = constants.user_id;
    try {
      sendText.put("id",user_id);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    socket.connect();

    socket.on("connect", new Listener() {
      @Override
      public void call(Object... args) {
       socket.emit("authentication",sendText);

       socket.on("authenticated", new Listener() {
         @Override
         public void call(Object... args) {
           Constants.isConnected=true;
           Log.d("LOG","isConnected:"+Constants.isConnected);
           HomeActivity.socket.on("private",new Emitter.Listener() {
             @Override
             public void call(final Object... args) {
               runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                   JSONObject data = (JSONObject) args[0];
                   String message = null;
                   String from = null;
                   String to = null;
                   String time = null;
                   try {
                     message = data.getString("message").toString();
                     from = data.getString("from").toString();
                     to = data.getString("to").toString();
                     time=data.getString("date").toString();
                     Log.d("from Priivate Chat", message + " " + from + " " + to+time);
                     Log.d("LOG", message);
                   } catch (JSONException e) {
                     e.printStackTrace();
                   }
                  //* addUser("other");
                   //addMessage(message);*//*
                 }
               });
             }
           } );

         }
       });
        socket.on("disconnect", new Listener() {
          @Override
          public void call(Object... args) {
            Constants.isConnected=false;
            disconnected();
            Log.d("LOG","isConnected:"+Constants.isConnected);
          }
        });

        socket.on("failed", new Listener() {
          @Override
          public void call(Object... args) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Constants.isConnected=false;
                Log.d("LOG","isConnected:"+Constants.isConnected);
              }
            });
          }
        });
      }
    });
    /*socket.emit("authentication",sendText);

    socket.on("authenticated", new Listener() {
      @Override
      public void call(final Object... args) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Constants.isConnected=true;
            Log.d("LOG","isConnected:"+Constants.isConnected);
          }
        });
      }
    });*/


    friendsBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if(Constants.isConnected) {
          Intent intent = new Intent(HomeActivity.this, FriendsActivity.class);
          intent.putExtra("user_id", getIntent().getStringExtra("user_id"));
          startActivity(intent);
        }

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

  private void disconnected() {

  }


}
