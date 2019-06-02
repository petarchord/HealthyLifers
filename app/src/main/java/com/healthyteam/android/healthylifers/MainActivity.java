package com.healthyteam.android.healthylifers;

import android.app.Dialog;
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

public class MainActivity extends AppCompatActivity {

    private Button signIn;
    private Button register;
    private Dialog signInWindow;
    private Dialog registerWindow;
    private ImageButton exitSignIn;
    private ImageButton exitRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInWindow.show();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
