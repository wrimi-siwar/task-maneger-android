package com.selmi.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    Button signupButton;

    FirebaseAuth auth;
    FirebaseUser currentUser ;
    // Récupérer le nom d'utilisateur et le mot de passe enregistrés
    SharedPreferences sharedPref;
    private static final int SPLASH_TIME_OUT = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        ImageView logo = findViewById(R.id.logo);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(anim);

        TextView appName = findViewById(R.id.app_name);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        appName.startAnimation(anim2);

        new Handler().postDelayed(() -> {
            Intent intent;

            if (currentUser != null) {
                intent = new Intent(SplashScreenActivity.this, MenuActivity.class);
                startActivity(intent);
            } else {
                intent = new Intent(SplashScreenActivity.this, loginActivity.class);
                startActivity(intent);
            }
            finish();
        }, SPLASH_TIME_OUT);
    }

}
