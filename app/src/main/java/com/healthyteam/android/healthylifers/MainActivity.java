package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.healthyteam.android.healthylifers.Domain.DomainController;

//ctrl + o = open event handlers menu

public class MainActivity extends AppCompatActivity {

    private Dialog logOutDialog;
    private Dialog settingsDialog;
    private ImageButton btnExit;
    private Fragment settingsFragment = null;
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    selectedFragment=MapFragment.getInstance();
                    break;
                case R.id.navigation_myprofile:
                    selectedFragment=MyProfileFragment.getInstance();
                    break;
                case R.id.navigation_myfriends:
                    selectedFragment=MyFriendsFragment.getInstance();
                    break;
                case R.id.navigation_worldscore:
                    selectedFragment=WorldScoreFragment.getInstance();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //should call from SignIn Activity
        DomainController.setUser("u","p");
        //configure toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        btnExit = (ImageButton)findViewById(R.id.btnExit);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.dialog_settings);

        logOutDialog = new Dialog(this);
        logOutDialog.setContentView(R.layout.dialog_log_out);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logOutDialog.show();
            }
        });
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MapFragment()).commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {
            case R.id.app_bar_search:
                Log.d("MyTag","Search clicked!");

                break;

            case R.id.app_bar_settings:
                if(settingsFragment == null)
                {
                    settingsFragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,settingsFragment).commit();

                }
                else
                {
                    getSupportFragmentManager().beginTransaction().hide(settingsFragment).commit();
                    settingsFragment=null;
                }

                break;


        }

        return super.onOptionsItemSelected(item);
    }
}
