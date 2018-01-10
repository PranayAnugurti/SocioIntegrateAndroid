package com.example.pranaykumar.sociointegrate;

/**
 * Created by PRANAYKUMAR on 1/7/2018.
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

  private List<Friend> mFriends;
  private int[] mUsernameColors;

  public FriendsAdapter(List<Friend> friends) {
    mFriends = friends;
    //  mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.friend_item, parent, false);
    return new ViewHolder(v);
  }


  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    Friend friend = mFriends.get(position);
    Log.d("LOG","position="+position);
    viewHolder.mFriendTextView.setText(friend.getFriend_name());
    //viewHolder.setImage(message.getImage());
    Log.d("LOG","MESSAGE="+friend.getFriend_name());
  }

  @Override
  public int getItemCount() {
    return mFriends.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    //private ImageView mImageView;
    private TextView mFriendTextView;
    public ViewHolder(View itemView) {
      super(itemView);
      //mImageView = (ImageView) itemView.findViewById(R.id.image);
      mFriendTextView = (TextView) itemView.findViewById(R.id.friend_name);
    }


  }
}