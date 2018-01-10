package com.example.pranaykumar.sociointegrate;

/**
 * Created by PRANAYKUMAR on 1/7/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<Friend> mFriends;
    private int[] mUsernameColors;
Context context;

    public FriendsAdapter(List<Friend> friends) {
        mFriends = friends;
        //  mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.friend_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Friend friend = mFriends.get(position);
        Log.d("LOG", "position=" + position);
        viewHolder.mFriendTextView.setText(friend.getFriend_name());
        //viewHolder.setImage(message.getImage());
        Log.d("LOG", "MESSAGE=" + friend.getFriend_name());
        viewHolder.getmLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PrivateSocketActivity.class);
                intent.putExtra("friend_name",friend.getFriend_name());
                intent.putExtra("friend_id",friend.getFriend_user_id());
                Log.d("FRIEND_ID", "onClick: "+friend.getFriend_user_id()+" has been clicked");
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //private ImageView mImageView;
        private TextView mFriendTextView;
        private LinearLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //mImageView = (ImageView) itemView.findViewById(R.id.image);
            mFriendTextView = (TextView) itemView.findViewById(R.id.friend_name);
            mLayout = (LinearLayout) itemView.findViewById(R.id.friend_name_layout);
        }

        public LinearLayout getmLayout() {
            return mLayout;
        }
    }
}