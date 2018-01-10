package com.example.pranaykumar.sociointegrate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.emitter.Emitter.Listener;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
  String url="http://192.168.0.102:5000";

  private Socket socket;
  {
    try{
      //socket=IO.socket("https://sociointegrate.herokuapp.com/");
      socket=IO.socket(Constants.server_url);
      Log.d("LOG","server_url="+Constants.server_url);
    }catch (URISyntaxException e){
      throw new RuntimeException(e);
    }
  }

  public ChatFragment() {
    // Required empty public constructor
  }
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_chat,container,false);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    //Socket Conncetion Establishment
    socket.connect();
    
    socket.on("message",handleIncomingMessages);
  }

  private Emitter.Listener handleIncomingMessages=new Emitter.Listener(){
    @Override
    public void call(final Object... args) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          JSONObject data=(JSONObject)args[0];
          String message = null;
          try{
            message=data.getString("text").toString();
            Log.d("LOG",message);
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
    mAdapter = new MessageAdapter( mMessages,mUsers);
    mMessagesView.setAdapter(mAdapter);

    ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
    mInputMessageView = (EditText) view.findViewById(R.id.message_input);

    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });


  }
  private void sendMessage(){
    String message = mInputMessageView.getText().toString().trim();
    mInputMessageView.setText("");
    addMessage(message);
    addUser("me");
    JSONObject sendText = new JSONObject();
    try{
      sendText.put("text",message);
      socket.emit("message", sendText);
    }catch(JSONException e){

    }

  }


  private void addUser(String user){
    mUsers.add(new User(user));

  }

  private void addMessage(String message) {

    mMessages.add(new Message(message));
    // mAdapter = new MessageAdapter(mMessages);
    Log.d("LOG","mMessages count="+mMessages.size()+"message="+message);

    mAdapter.notifyDataSetChanged();
    scrollToBottom();
  }

  private void scrollToBottom() {
    mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
  }
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    socket.disconnect();
  }
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    public void onFragmentInteraction(Uri uri);
  }
}
