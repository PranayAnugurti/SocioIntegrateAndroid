package com.example.pranaykumar.sociointegrate;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
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
    private String mParam1;
    private String mParam2;
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
                        message = data.getString("text").toString();
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
            String friend_id = getActivity().getIntent().getStringExtra("friend_id");
            Log.d("from Priivate Chat", message + " " + constants.user_id + " " + friend_id);
            sendText.put("text", message);
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

        mMessages.add(new Message(message));
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
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}

