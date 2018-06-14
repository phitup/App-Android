package com.chatrealtime.chatrealtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class StartActivity extends AppCompatActivity {

    private Button btnRegister , btnLogin;
    RelativeLayout startScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnhXa();

        //set background
        startScreen.setBackgroundResource(R.drawable.bgstart);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext() , LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent registerIntent = new Intent(getApplicationContext() , RegisterActivity.class);
                startActivity(registerIntent);

            }
        });

    }

    private void AnhXa(){
        btnRegister = (Button)findViewById(R.id.buttonRegister);
        startScreen = (RelativeLayout)findViewById(R.id.StartScreen);
        btnLogin = (Button)findViewById(R.id.buttonLogin);
    }

}
