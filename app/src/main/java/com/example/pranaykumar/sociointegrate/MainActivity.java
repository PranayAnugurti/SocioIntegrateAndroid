package com.example.pranaykumar.sociointegrate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
  private AccessToken accessToken;
  private TextView txtView;
  String username;
  String email;
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
                      new SendUserDetails()
                          .execute("https://sociointegrate.herokuapp.com/user-details",
                              object.toString());
                      username = object.getString("name");
                      email = object.getString("email");
                      JSONObject friendsObject = object.getJSONObject("friends");
                      JSONArray friendsArray = friendsObject.getJSONArray("data");
                      Intent intent = new Intent(MainActivity.this, SocketActivity.class);
                      startActivity(intent);

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
    private class SendUserDetails extends AsyncTask<String, Void, String> {

      @Override
      protected String doInBackground(String... params) {

        String data = "";

        HttpURLConnection httpURLConnection = null;
        try {

          httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
          httpURLConnection.setRequestMethod("POST");
          httpURLConnection.setRequestProperty("Content-type", "application/json");
          httpURLConnection.setDoOutput(true);
          DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
          wr.writeBytes(params[1]);
          wr.flush();
          wr.close();

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
        Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
      }
    }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    callbackManager.onActivityResult(requestCode, resultCode, data);
    super.onActivityResult(requestCode, resultCode, data);
  }
}
