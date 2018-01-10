package com.example.pranaykumar.sociointegrate;

/**
 * Created by PRANAYKUMAR on 1/9/2018.
 */

public class Friend {
  private String friend_name;
  private String friend_user_id;

  public Friend(){

  }
  public Friend(String friend_name,String friend_user_id){
    this.friend_name=friend_name;
    this.friend_user_id=friend_user_id;
  }
  public String getFriend_name(){
    return friend_name;
  }
  public String getFriend_user_id(){
    return friend_user_id;
  }

  public void setFriend_name(String friend_name) {
    this.friend_name = friend_name;
  }

  public void setFriend_user_id(String friend_user_id) {
    this.friend_user_id = friend_user_id;
  }
}
