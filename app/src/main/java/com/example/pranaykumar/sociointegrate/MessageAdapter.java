package com.example.pranaykumar.sociointegrate;

/**
 * Created by PRANAYKUMAR on 1/7/2018.
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

  private List<Message> mMessages;
  private List<User> mUser;

  private int[] mUsernameColors;

  public MessageAdapter(List<Message> messages, List<User> user) {
    mMessages = messages;
    mUser  = user;
    //  mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
  }




  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layout = -1;
        /*switch (viewType) {
            case Message.TYPE_MESSAGE:
                layout = R.layout.item_message;
                break;
            case Message.TYPE_LOG:
                layout = R.layout.item_log;
                break;
            case Message.TYPE_ACTION:
                layout = R.layout.item_action;
                break;
        }*/
    View v = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.layout_message, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {



      User user = mUser.get(position);
      if (user.getmUser()=="me"){
        Message message = mMessages.get(position);
        viewHolder.mMessageView.setText(message.getMessage());
        viewHolder.mTimeView.setText(message.getmTime());
          viewHolder.mMsgLayout.setBackground(viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.user));
        viewHolder.mlayout.setGravity(Gravity.RIGHT);
      }else {
        Message message = mMessages.get(position);
        viewHolder.mMessageView.setText(message.getMessage());
        viewHolder.mTimeView.setText(message.getmTime());
        viewHolder.mMsgLayout.setBackground(viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.others));
        viewHolder.mlayout.setGravity(Gravity.LEFT);
      }
   }

  @Override
  public int getItemCount() {
    return mMessages.size();
  }

 /* @Override
  public int getItemViewType(int position) {
    return mMessages.get(position).getType();
  }
*/
  public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView mImageView;
    private TextView mMessageView,mTimeView;
    private LinearLayout mlayout,mMsgLayout;
    public ViewHolder(View itemView) {
      super(itemView);
      mImageView = (ImageView) itemView.findViewById(R.id.imageView);
      mMessageView = (TextView) itemView.findViewById(R.id.message);
      mTimeView = (TextView) itemView.findViewById(R.id.timeView);
     mlayout = (LinearLayout)itemView.findViewById(R.id.layout);
     mMsgLayout = (LinearLayout)itemView.findViewById(R.id.msgLayout);
    }

    public void setMessage(String message) {
      if (null == mMessageView) return;
      if(null == message) return;
      mMessageView.setText(message);

    }

/*    public void setImage(Bitmap bmp){
      if(null == mImageView) return;
      if(null == bmp) return;
      mImageView.setImageBitmap(bmp);
    }*/
    private int getUsernameColor(String username) {
      int hash = 7;
      for (int i = 0, len = username.length(); i < len; i++) {
        hash = username.codePointAt(i) + (hash << 5) - hash;
      }
      int index = Math.abs(hash % mUsernameColors.length);
      return mUsernameColors[index];
    }

   public LinearLayout getMlayout() {
     return mlayout;
   }
 }
}
