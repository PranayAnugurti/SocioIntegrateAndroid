package com.example.pranaykumar.sociointegrate;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 1/10/2018.
 */

public class PrivateChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    String friend_id;
    private EditText mInputMessageView;
    private RecyclerView mMessagesView;
    private ChatFragment.OnFragmentInteractionListener mListener;
    private List<Message> mMessages = new ArrayList<Message>();
    private List<User> mUsers = new ArrayList<User>();
    private RecyclerView.Adapter mAdapter;
    String url = "http://192.168.0.102:5000";

    public PrivateChatFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_priavatechat, container, false);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        new PrivateMessagesAsyncTask().execute("");
        HomeActivity.socket.on("private", handleIncomingMessages);

    }

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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
                    addUser("other");
                    addMessage(message);
                }
            });
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MessageAdapter(mMessages, mUsers);
        mMessagesView.setAdapter(mAdapter);

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        mInputMessageView = (EditText) view.findViewById(R.id.message_input);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mAdapter.notifyDataSetChanged();

    }

    private void sendMessage() {
        String message = mInputMessageView.getText().toString().trim();
        mInputMessageView.setText("");
        addMessage(message);
        addUser("me");
        JSONObject sendText = new JSONObject();
        try {

            Constants constants = new Constants();
            PrivateSocketActivity privateSocketActivity = new PrivateSocketActivity();
            friend_id = getActivity().getIntent().getStringExtra("friend_id");
            Log.d("from Priivate Chat", message + " " + constants.user_id + " " + friend_id);
            sendText.put("message", message);
            sendText.put("fromId", Constants.user_id);
            sendText.put("to", friend_id);
            HomeActivity.socket.emit("private", sendText);
        } catch (JSONException e) {

        }

    }


    private void addUser(String user) {
        mUsers.add(new User(user));

    }

    private void addMessage(String message) {

        mMessages.add(new Message(message,Constants.user_id,friend_id,""));
        // mAdapter = new MessageAdapter(mMessages);
        Log.d("LOG", "mMessages count=" + mMessages.size() + "message=" + message);

        mAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //HomeActivity.socket.disconnect();
      HomeActivity.socket.off("private");
    }
  private class PrivateMessagesAsyncTask extends AsyncTask<String, Void, String> {

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
      Log.e("LOG", "RESULT FROM SERVER:"+result); // this is expecting a response code to be sent from your server upon receiving the POST data
      try {
        JSONArray messagesArray=new JSONArray(result);
        int i=0;
        while (i < messagesArray.length()) {

          JSONObject message = messagesArray.getJSONObject(i);
          Message currentMessage=new Message(message.getString("message"),
              message.getString("from"),
              message.getString("to"),
              message.getString("date"));
          mMessages.add(i,currentMessage);
          i++;
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }
}

