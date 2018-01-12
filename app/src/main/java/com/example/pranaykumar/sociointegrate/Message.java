package com.example.pranaykumar.sociointegrate;

/**
 * Created by PRANAYKUMAR on 1/7/2018.
 */


/**
 * Created by sreejeshpillai on 10/05/15.
 */
public class Message {

  private String mMessage;
  private String mFromId;
  private String mToId;
  private String mDate;
  private String mTime;

  public Message(String mMessage,String mFromId,String mToId,String mDate) {
    this.mMessage=mMessage;
    this.mFromId=mFromId;
    this.mToId=mToId;
    this.mDate=mDate;
  }
  public Message(String mMessage,String mFromId,String mToId,String mDate,String mTime) {
    this.mMessage=mMessage;
    this.mFromId=mFromId;
    this.mToId=mToId;
    this.mDate=mDate;
    this.mTime=mTime;
  }
  public Message(){};

  public String getMessage() {
    return mMessage;
  };
  public String getmTime(){
    return mTime;
  }
  public void setmTime(String mTime){
    this.mTime=mTime;
  }

  public void setmDate(String mDate) {
    this.mDate = mDate;
  }

  public String getmFromId(){return mFromId;};
  public String getmToId(){return mToId;};
  public String getmDate(){return mDate;};
}
