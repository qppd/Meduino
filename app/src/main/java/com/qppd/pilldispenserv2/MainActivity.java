package com.qppd.pilldispenserv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.daimajia.androidanimations.library.Techniques;
import com.qppd.pilldispenserv2.Libs.Firebasez.FirebaseAuthHelper;
import com.qppd.pilldispenserv2.Libs.Functionz.FunctionClass;
import com.qppd.pilldispenserv2.Libs.IntentManager.IntentManagerClass;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.util.Objects;

public class MainActivity extends AwesomeSplash {

    private final String TAG = MainActivity.class.getSimpleName();

    private FunctionClass functions = new FunctionClass(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

    }

    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(R.color.white);
        configSplash.setAnimCircularRevealDuration(1000);
        configSplash.setRevealFlagX(Flags.REVEAL_BOTTOM);
        configSplash.setRevealFlagY(Flags.REVEAL_RIGHT);
        configSplash.setTitleSplash("PILL DISPENSER");
        // configSplash.setTitleFont("font/sweetcharmy.ttf"); // Disabled to prevent crash
        configSplash.setTitleTextColor(R.color.colorMainDark);

        configSplash.setLogoSplash(R.drawable.applogo);
        configSplash.setTitleTextSize(48);
        configSplash.setAnimTitleDuration(3000);
        configSplash.setAnimLogoSplashDuration(2000);
        configSplash.setAnimLogoSplashTechnique(Techniques.ZoomInLeft);
    }

    @Override
    public void animationsFinished() {

        IntentManagerClass.intentsify(MainActivity.this, LoginActivity.class,
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();

    }
}