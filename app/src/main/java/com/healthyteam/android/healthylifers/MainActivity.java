package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.healthyteam.android.healthylifers.Domain.DomainController;

import org.osmdroid.config.Configuration;

//ctrl + o = open event handlers menu

public class MainActivity extends AppCompatActivity {

    private Dialog logOutDialog;
    private Dialog settingsDialog;
    private Dialog addItemDialog;
    private Dialog locationViewDialog;
    private ImageButton btnExit;
    private Fragment settingsFragment = null;
    private Fragment tabInfoFragment;
    private Fragment tabCommentsFragment;
    private SectionsPageAdapter mSectionPageAdapter;
    private FirebaseAuth mAuth;
    private Button yesLogOut;
    private Button noLogOut;

    private TabLayout tabLayout;
    private ViewPager mViewPager;

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

        mAuth = FirebaseAuth.getInstance();

        addItemDialog = new Dialog(this);
        addItemDialog.setContentView(R.layout.dialog_add_item);

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        locationViewDialog = new Dialog(this);
        locationViewDialog.setContentView(R.layout.dialog_location_view);

        tabLayout = locationViewDialog.findViewById(R.id.tabsLocation);
        mViewPager = locationViewDialog.findViewById(R.id.viewPagerLocation);

        setUpViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);



        btnExit = (ImageButton)findViewById(R.id.btnExit);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.dialog_settings);

        logOutDialog = new Dialog(this);
        logOutDialog.setContentView(R.layout.dialog_log_out);

        yesLogOut = logOutDialog.findViewById(R.id.yesLogOut);
        noLogOut = logOutDialog.findViewById(R.id.noLogOut);

        yesLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        noLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog.dismiss();
            }
        });

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
                    MapFragment.getInstance()).commit();
        }
    }


    private void signOut() {
        mAuth.signOut();
        Intent i = new Intent(getApplicationContext(),SignInActivity.class);
        startActivity(i);
      //  updateUI(null);
    }

    public void setUpViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        tabInfoFragment = new Fragment();
        tabCommentsFragment = new Fragment();
        adapter.addFragment(tabInfoFragment,"TabInfoFragment");
        adapter.addFragment(tabCommentsFragment,"TabCommentsFragment");
        mViewPager.setAdapter(adapter);
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
                locationViewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                locationViewDialog.show();

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
