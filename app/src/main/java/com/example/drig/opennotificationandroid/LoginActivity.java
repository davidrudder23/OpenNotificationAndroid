package com.example.drig.opennotificationandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_page_activitiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onLoginButtonClicked(View view) {
        Log.e("Button clicked", "login button clicked");

        EditText serverUrl = (EditText)findViewById(R.id.server_field);
        Log.d("serverUrl", serverUrl.getText().toString());

        EditText username = (EditText)findViewById(R.id.username_field);
        Log.d("username", username.getText().toString());

        EditText password = (EditText)findViewById(R.id.password_field);
        Log.d("password", password.getText().toString());

        String loginURL = serverUrl.getText().toString()+"/rest/login/"+username.getText().toString()+"/"+password.getText().toString();
        Log.d("login", "Login URL="+loginURL);

        AsyncTask<String, Void, String> contentTask = new URLPoster().execute(loginURL, username.getText().toString(), password.getText().toString());
        String content = null;
        boolean succeeded = false;
        try {
            content = contentTask.get();

            if (content == null) {
                content = "{'result':'false'}";
            }

            Log.d("content", content);
            JSONObject jObject = new JSONObject(content);

            String loginResult = jObject.getString("result");
            if ((loginResult == null) || (!"true".equals(loginResult))) {
                Log.d("login", "Login failed");

            } else {
                Log.d("login", "Login succeeded");
                succeeded = true;

                startActivity(new Intent(this, Notifications.class));

            }
        } catch (InterruptedException|ExecutionException|JSONException e) {
            e.printStackTrace();
        }

        if (!succeeded) {
            Log.d("login", "Showing dialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Login Failed").setTitle("");
            AlertDialog dialog = builder.create();
            dialog.show();
        }


        SharedPreferences.Editor sharedPreferences = getSharedPreferences("OpenNotificationAndroid", 0).edit();
        sharedPreferences.putString("serverURL", serverUrl.getText().toString());
        sharedPreferences.putString("username", username.getText().toString());
        sharedPreferences.putString("password", password.getText().toString());
        sharedPreferences.commit();
        return true;
    }
}

