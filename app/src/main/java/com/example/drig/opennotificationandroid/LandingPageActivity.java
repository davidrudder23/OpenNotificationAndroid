package com.example.drig.opennotificationandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class LandingPageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        super.setTitle("OpenNotification");

        SharedPreferences sharedPreferences = getSharedPreferences("OpenNotificationAndroid", 0);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        // TODO - make a real logout menu
        //username = "";

        if ((!"".equals(username)) && (!"".equals(password))) {
            startActivity(new Intent(this, Notifications.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
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
            SharedPreferences sharedPreferences = getSharedPreferences("OpenNotificationAndroid", 0);
            sharedPreferences.edit().remove("serverURL");
            sharedPreferences.edit().remove("username");
            sharedPreferences.edit().remove("password");
            startActivity(new Intent(this, LandingPageActivity.class));
        } else if (id == R.id.action_refresh) {

        }

        return super.onOptionsItemSelected(item);
    }
}
