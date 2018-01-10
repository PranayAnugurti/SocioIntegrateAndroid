package com.example.pranaykumar.sociointegrate;

import android.net.sip.SipSession.Listener;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pranaykumar.sociointegrate.FriendsAdapter.ViewHolder;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsActivity extends AppCompatActivity {

  private List<Friend> friendList = new ArrayList<>();
  private RecyclerView recyclerView;
  private FriendsAdapter mAdapter;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_friends);
    //new GetFriendsAsyncTask().execute(Constants.server_url+"/"+getIntent().getStringExtra("user_id")+"/friends");
    new GetFriendsAsyncTask().execute(Constants.friends_url);
    recyclerView = (RecyclerView) findViewById(R.id.friends_recycler_view);
    //mAdapter = new FriendsAdapter(friendList);
    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    //recyclerView.setAdapter(mAdapter);



  }

  private class GetFriendsAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

      String data = "";

      HttpURLConnection httpURLConnection = null;
      try {

        httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
        httpURLConnection.setRequestMethod("GET");
        //httpURLConnection.setRequestProperty("Content-type", "application/json");
        httpURLConnection.setDoInput(true);
      /*DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
      wr.writeBytes(params[1]);
      wr.flush();
      wr.close();*/
        Log.d("LOG",params[0]);
        InputStream in = httpURLConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(in);

        int inputStreamData = inputStreamReader.read();
        while (inputStreamData != -1) {
          char current = (char) inputStreamData;
          inputStreamData = inputStreamReader.read();
          data += current;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (httpURLConnection != null) {
          httpURLConnection.disconnect();
        }
      }

      return data;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      Log.e("LOG", "RESULT FROM SERVER"+result); // this is expecting a response code to be sent from your server upon receiving the POST data
      try {
        JSONObject object=new JSONObject(result);
        JSONArray friendsArray=object.getJSONArray("friends");
        int i=0;
        while (i < friendsArray.length()) {

          JSONObject friend = friendsArray.getJSONObject(i);
          Friend currentFriend=new Friend(friend.getString("name"),
              friend.getString("id"));
          friendList.add(i,currentFriend);
          i++;
        }
        mAdapter=new FriendsAdapter(friendList);
        recyclerView.setAdapter(mAdapter);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }
}
