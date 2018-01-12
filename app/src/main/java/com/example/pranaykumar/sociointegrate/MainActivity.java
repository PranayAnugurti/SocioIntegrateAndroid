package com.example.pranaykumar.sociointegrate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  private CallbackManager callbackManager;
  public Constants constants=new Constants();
  private AccessToken accessToken;
  private TextView txtView;
  String username,jsonObject;
  String email;
  Context context=this;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    FacebookSdk.sdkInitialize(getApplicationContext());
    txtView = (TextView) findViewById(R.id.textView);

    callbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance().logInWithReadPermissions(
        this,
        Arrays.asList(
            "email",
            "user_friends",
            "public_profile",
            "user_birthday"));

    LoginManager.getInstance().registerCallback(callbackManager,
        new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(final LoginResult loginResult) {
            // App code
            accessToken = loginResult.getAccessToken();
            Constants.user_id=loginResult.getAccessToken().getUserId();
            Constants.friends_url=Constants.server_url+"/"+Constants.user_id+"/friends";
            //Toast.makeText(MainActivity.this,"Login Successfull!",Toast.LENGTH_SHORT).show();
            Log.d("LOG",
                "userid=" + loginResult.getAccessToken().getUserId() + "\n" + "token=" + loginResult
                    .getAccessToken().getToken());
            Log.d("LOG", "access token=" + String.valueOf(loginResult.getAccessToken()));
            GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                  @Override
                  public void onCompleted(JSONObject object, GraphResponse response) {
                    // Insert your code here
                    //txtView.setText(object.toString());
                    try {
                      object.put("token", String.valueOf(loginResult.getAccessToken().getToken()));
                      Log.d("LOG", object.toString());
                      jsonObject=object.toString();

                      /*******************************************************/
                      //AlertDialog for URL Change
                      AlertDialog.Builder alert = new AlertDialog.Builder(context);
                      final EditText edittext = new EditText(context);
                      edittext.setText("http://192.168.0.100:5000");
                      alert.setMessage("https://sociointegrate.herokuapp.com/ is the default URL"
                          );
                      alert.setTitle("Do you want to change the server url?");

                      alert.setView(edittext);

                      alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                          //What ever you want to do with the value
                          //Editable YouEditTextValue = edittext.getText();
                          //OR
                          String urlEditField = edittext.getText().toString();
                          Constants.server_url=urlEditField;
                          Constants.server_user_details_url=urlEditField+"/user-details";
                          Constants.friends_url=Constants.server_url+"/"+Constants.user_id+"/friends";

                          new SendUserDetailsAsyncTask()
                              .execute(Constants.server_user_details_url,
                                  jsonObject);
                          Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                          intent.putExtra("user_id",Constants.user_id);
                          startActivity(intent);
                        }
                      });

                      alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                          // what ever you want to do with No option.
                          new SendUserDetailsAsyncTask()
                              .execute(Constants.server_user_details_url,
                                  jsonObject);
                          Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                          intent.putExtra("user_id",Constants.user_id);

                          startActivity(intent);
                        }
                      });
                      alert.show();


                      /**********************************/


                      username = object.getString("name");
                      email = object.getString("email");
                      JSONObject friendsObject = object.getJSONObject("friends");
                      JSONArray friendsArray = friendsObject.getJSONArray("data");
                    } catch (JSONException e) {
                      e.printStackTrace();
                    }

                  }
                });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,email,friends,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
          }

          @Override
          public void onCancel() {
            // App code
            txtView.setText("Login Cancelled!");
          }

          @Override
          public void onError(FacebookException exception) {
            // App code
            txtView.setText("Login Failure :" + exception.toString());
          }
        });
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
  }
}
