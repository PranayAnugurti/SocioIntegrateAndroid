package com.example.pranaykumar.sociointegrate;

/**
 * Created by PRANAYKUMAR on 1/7/2018.
 */
import android.graphics.Bitmap;

/**
 * Created by sreejeshpillai on 10/05/15.
 */
public class Message {

  public static final int TYPE_MESSAGE = 0;
  public static final int TYPE_LOG = 1;
  public static final int TYPE_ACTION = 2;

  //private int mType;
  private String mMessage;



  //private Bitmap mImage;

  public Message(String mMessage) {
    this.mMessage=mMessage;

  }
  public Message(){};


 /* public int getType() {
    return mType;
  };*/

  public String getMessage() {
    return mMessage;
  };
  public void setMessage() {
    this.mMessage=mMessage;
  };

  /*public Bitmap getImage() {
    return mImage;
  };
*/

 /* public static class Builder {
    //private final int mType;
    //private Bitmap mImage;
    private String mMessage;

*//*    public Builder(int type) {
      mType = type;
    }*//*

  *//*  public Builder image(Bitmap image) {
      mImage = image;
      return this;
    }*//*

    public Builder message(String message) {
      mMessage = message;
      return this;
    }

    public Message build() {
      Message message = new Message();
      //message.mType = mType;
      //message.mImage = mImage;
      message.mMessage = mMessage;
      return message;
    }
  }*/
}
