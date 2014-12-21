package com.example.drig.opennotificationandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Notifications extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        updateScreen();
    }



    private void updateScreen() {

        SharedPreferences sharedPreferences = getSharedPreferences("OpenNotificationAndroid", 0);
        String serverUrl = sharedPreferences.getString("serverURL", "");
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        String url = serverUrl+"/rest/notifications";

        LinearLayout notificationsLayout = (LinearLayout)findViewById(R.id.notifications_layout);

        List<Notification> notifications = getNotificationsFromServer(username, password, url);
        notificationsLayout.removeAllViews();

        for (final Notification notification: notifications) {
            LinearLayout outerNotificationLayout = new LinearLayout(this);
            outerNotificationLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout topNotificationLayout = new LinearLayout(this);
            topNotificationLayout.setOrientation(LinearLayout.HORIZONTAL);
            //notificationLayout.setGravity(Gravity.CENTER);

            ImageView activityIcon = new ImageView(this);
            activityIcon.setScaleType(ImageView.ScaleType.CENTER);

            if ("confirmed".equalsIgnoreCase(notification.status)) {
                activityIcon.setImageResource(R.drawable.led_yellow);
            } else if ("expired".equalsIgnoreCase(notification.status)) {
                activityIcon.setImageResource(R.drawable.led_red);
            } else if ("onhold".equalsIgnoreCase(notification.status)) {
                activityIcon.setImageResource(R.drawable.led_blue);
            } else {
                activityIcon.setImageResource(R.drawable.led_green);
            }
            topNotificationLayout.addView(activityIcon);

            TextView notificationSubject = new TextView(this);
            notificationSubject.setText(notification.subject);
            topNotificationLayout.addView(notificationSubject);

            outerNotificationLayout.addView(topNotificationLayout);

            LinearLayout bottomNotificationLayout = new LinearLayout(this);
            bottomNotificationLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (final String response: notification.responses) {
                Button responseButton = new Button(this);
                responseButton.setText(response);
                responseButton.setTextSize(8.5f);

                responseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            SharedPreferences sharedPreferences = getSharedPreferences("OpenNotificationAndroid", 0);
                            String serverUrl = sharedPreferences.getString("serverURL", "");
                            String username = sharedPreferences.getString("username", "");
                            String password = sharedPreferences.getString("password", "");

                            String url = serverUrl+"/rest/respond/"+notification.uuid+"/"+response;
                            AsyncTask<String, Void, String> contentTask = new URLPoster().execute(url, username, password);
                            String content = contentTask.get();
                            updateScreen();
                        } catch (InterruptedException|ExecutionException e) {
                                e.printStackTrace();
                        }
                    }
                });

                bottomNotificationLayout.addView(responseButton);
            }

            outerNotificationLayout.addView(bottomNotificationLayout);
            notificationsLayout.addView(outerNotificationLayout);
        }

    }

    private List<Notification> getNotificationsFromServer(String username, String password, String url) {
        List<Notification> notifications = new ArrayList<>();

        try {
            AsyncTask<String, Void, String> contentTask = new URLPoster().execute(url, username, password);
            String content = contentTask.get();

            if (content == null) return notifications;

            JSONObject json = new JSONObject(content);
            Log.d("notifications", json.toString());
            Log.d("json type", json.getClass().getName());

            JSONArray notificationJsonArray = json.getJSONArray("notifications");

            for (int i = 0; i < notificationJsonArray.length(); i++) {
                Notification notification = new Notification();
                JSONObject jsonNotification = (JSONObject)notificationJsonArray.get(i);
                notification.date = jsonNotification.getString("date");

                notification.uuid = jsonNotification.getString("uuid");
                notification.type = jsonNotification.getString("type");
                notification.name = jsonNotification.getString("recipient_name");
                notification.email = jsonNotification.getString("recipient_email");
                notification.subject = jsonNotification.getString("subject");
                notification.message = jsonNotification.getString("message");
                notification.date = jsonNotification.getString("date");
                notification.status = jsonNotification.getString("status");
                JSONArray responsesArray = jsonNotification.getJSONArray("responses");
                notification.responses = new ArrayList<>();
                for (int r=0; r < responsesArray.length(); r++) {
                    //JSONObject responseJson = (JSONObject)responsesArray.get(r);
                    notification.responses.add((String)responsesArray.get(r));
                }
                notifications.add(notification);
            }
        } catch (InterruptedException|ExecutionException |JSONException e) {
            e.printStackTrace();
        }

        return notifications;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
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
        } else if (id == R.id.action_logout) {
            SharedPreferences.Editor sharedPreferences = getSharedPreferences("OpenNotificationAndroid", 0).edit();
            sharedPreferences.remove("serverURL");
            sharedPreferences.remove("username");
            sharedPreferences.remove("password");
            sharedPreferences.commit();
            startActivity(new Intent(this, LandingPageActivity.class));
        } else if (id == R.id.action_refresh) {

        }

        return super.onOptionsItemSelected(item);
    }
}


