package com.healthyteam.android.healthylifers;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.app.Fragment;

public class SignInActivity extends AppCompatActivity {

    private Button signIn;
    private Button register;
    private Button signInSubmit;
    private Button registerSubmit;
    private Dialog signInWindow;
    private Dialog registerWindow;
    private ImageButton exitSignIn;
    private ImageButton exitRegister;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signIn = (Button) findViewById(R.id.btnSignIn);
        register = (Button) findViewById(R.id.btnRegister);
        signInWindow = new Dialog(this);
        signInWindow.setContentView(R.layout.customsignin);
        registerWindow =new Dialog(this);
        registerWindow.setContentView(R.layout.customregister);

        exitSignIn = (ImageButton) signInWindow.findViewById(R.id.closeSignInForm);
        exitRegister = (ImageButton) registerWindow.findViewById(R.id.closeRegisterForm);

        signInSubmit = (Button) signInWindow.findViewById(R.id.signInSubmit);
        registerSubmit = (Button) registerWindow.findViewById(R.id.registerSubmit);


        signInSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });



        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                signInWindow.show();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                registerWindow.show();

            }
        });


        exitSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInWindow.dismiss();
            }
        });


        exitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWindow.dismiss();
            }
        });











    }





}
