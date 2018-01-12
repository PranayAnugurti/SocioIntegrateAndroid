package com.example.pranaykumar.sociointegrate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.emitter.Emitter.Listener;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PRANAYKUMAR on 1/7/2018.
 */


public class ChatFragment extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;
  private EditText mInputMessageView;
  private RecyclerView mMessagesView;
  private OnFragmentInteractionListener mListener;
  private List<Message> mMessages = new ArrayList<Message>();
  private List<User> mUsers = new ArrayList<User>();
  private RecyclerView.Adapter mAdapter;
  String url = "http://192.168.0.102:5000";

  private static final int TYPING_TIMER_LENGTH = 600;
  private boolean mTyping = false;
  private Handler mTypingHandler = new Handler();
  private String mUsername;

  public ChatFragment() {
    // Required empty public constructor
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_chat, container, false);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    new MessagesAsyncTask().execute(Constants.server_url + "/prev-messages");
    HomeActivity.socket.on("message", handleIncomingMessages);
    HomeActivity.socket.on("typing", onTyping);
    HomeActivity.socket.on("stop typing", onStopTyping);
  }





  private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          JSONObject data = (JSONObject) args[0];
          String message = null;
          try {
            message = data.getString("message").toString();
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


    mInputMessageView.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (null == Constants.user_id) return;
        if (!HomeActivity.socket.connected()) return;

        if (!mTyping) {
          mTyping = true;
          HomeActivity.socket.emit("typing");
        }

        mTypingHandler.removeCallbacks(onTypingTimeout);
        mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
  }

  private Emitter.Listener onTyping = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          JSONObject data = (JSONObject) args[0];
          String username;
          try {
            username = data.getString("username");
          } catch (JSONException e) {
            Log.e("TAG", e.getMessage());
            return;
          }
          addTyping(username);
        }
      });
    }
  };


  private Emitter.Listener onStopTyping = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          JSONObject data = (JSONObject) args[0];
          String username;
          try {
            username = data.getString("username");
          } catch (JSONException e) {
            Log.e("TAG", e.getMessage());
            return;
          }
          removeTyping(username);
        }
      });
    }
  };

  private Runnable onTypingTimeout = new Runnable() {
    @Override
    public void run() {
      if (!mTyping) return;

      mTyping = false;
      HomeActivity.socket.emit("stop typing");
    }
  };

  private void addTyping(String username) {
    mMessages.add(new Message.Builder(Message.TYPE_ACTION)
            .username(username).build());
    mAdapter.notifyItemInserted(mMessages.size() - 1);
    scrollToBottom();
  }

  private void removeTyping(String username) {
    for (int i = mMessages.size() - 1; i >= 0; i--) {
      Message message = mMessages.get(i);
      if (message.getType() == Message.TYPE_ACTION && message.getmFromId().equals(username)) {
        mMessages.remove(i);
        mAdapter.notifyItemRemoved(i);
      }
    }
  }

  private void sendMessage() {
    String message = mInputMessageView.getText().toString().trim();
    mInputMessageView.setText("");
    addMessage(message);
    addUser("me");
    JSONObject sendText = new JSONObject();
    try {
      sendText.put("message", message);
      sendText.put("from", Constants.user_id);
      HomeActivity.socket.emit("message", sendText, new Ack() {
        @Override
        public void call(Object... args) {
          Log.d("LOG", "Sent");
        }

      });
    } catch (JSONException e) {

    }

  }


  private void addUser(String user) {
    mUsers.add(new User(user));

  }

  private void addMessage(String message) {
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat tf = new SimpleDateFormat("HH:mm");
    String date = df.format(Calendar.getInstance().getTime());
    String time = tf.format(Calendar.getInstance().getTime());
    Log.d("LOG","time="+time+"::date="+date);
    Message msg=new Message(message, Constants.user_id, "",date,time);
    mMessages.add(msg);
    // mAdapter = new MessageAdapter(mMessages);
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
    HomeActivity.socket.off("message");
    HomeActivity.socket.off("typing",onTyping);
    HomeActivity.socket.off("stop typing",onStopTyping);
  }

  public interface OnFragmentInteractionListener {

    // TODO: Update argument type and name
    public void onFragmentInteraction(Uri uri);
  }

  private class MessagesAsyncTask extends AsyncTask<String, Void, String> {

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
        Log.d("LOG", params[0]);
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
      Log.e("LOG", "RESULT FROM SERVER:"
          + result); // this is expecting a response code to be sent from your server upon receiving the POST data
      try {
        JSONArray messagesArray = new JSONArray(result);
        int i = 0;
        while (i < messagesArray.length()) {

          JSONObject message = messagesArray.getJSONObject(i);
          Message currentMessage = new Message(message.getString("message"),
              //message.getString("from"),
              "",
              "",
              message.getString("date"));
          if (Constants.user_id.equals(""))
            addUser("me");
          else
            addUser("else");
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
          dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
          SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
          timeFormat.setTimeZone(Calendar.getInstance().getTimeZone());
          Date d = null;
          try {
            d = sdf.parse(message.getString("date"));
          } catch (ParseException e) {
            e.printStackTrace();
          }
          String formattedTime = timeFormat.format(d);
          String formattedDate = dateFormat.format(d);
          currentMessage.setmTime(formattedTime);
          currentMessage.setmDate(formattedDate);
          mMessages.add(i, currentMessage);
          mAdapter.notifyDataSetChanged();
          scrollToBottom();

          i++;
        }
      } catch (JSONException e) {
        e.printStackTrace();

      }
    }
  }
}
