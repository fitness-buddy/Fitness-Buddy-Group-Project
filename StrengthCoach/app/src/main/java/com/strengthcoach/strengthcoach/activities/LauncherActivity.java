package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.helpers.DeviceDimensionsHelper;

public class LauncherActivity extends ActionBarActivity {
    Button bLogin;
    ImageView ivBackground;
    TextView tvSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setupViews();
    }

    public void setupViews(){
        ivBackground = (ImageView)findViewById(R.id.ivBackground);
        ivBackground.setImageResource(R.drawable.slide_animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) ivBackground.getDrawable();
        frameAnimation.setEnterFadeDuration(3000);
        frameAnimation.setExitFadeDuration(3000);
        frameAnimation.start();

        tvSkip = (TextView) findViewById(R.id.tvSkip);
        bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BlockSlotActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);
            }
        });
    }

   public void callSkip(View v){
        Intent intent = new Intent(LauncherActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
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
}
