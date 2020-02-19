package com.example.scms;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class splashActivity extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {
      // ActionBar actionBar = getSupportActionBar();
       // actionBar.hide();

        Toast.makeText(this, "Build 2", Toast.LENGTH_SHORT).show();
        //setTitle("");
        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.colorPrimaryDark); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(10); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.baseline_directions_bike_white_48); //or any other drawable
        configSplash.setAnimLogoSplashTechnique(Techniques.SlideInRight); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        configSplash.setTitleSplash("Smart Campus Mobility System");
        configSplash.setTitleTextColor(R.color.strokeColor);
        configSplash.setTitleTextSize(20f); //float value


        configSplash.setAnimTitleTechnique(Techniques.Bounce);

        configSplash.setAnimTitleDuration(500);
        configSplash.setAnimLogoSplashDuration(500); //int ms


    }

    @Override
    public void animationsFinished() {
        Intent startApplication = new Intent(splashActivity.this, MainActivity.class);
        startActivity(startApplication);
        finish();
    }

}
