package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.support.v4.app.Fragment;
import android.widget.Toast;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.healthyteam.android.healthylifers.Data.OnRunTaskListener;
import com.healthyteam.android.healthylifers.Data.OnUploadDataListener;
import com.healthyteam.android.healthylifers.Domain.Constants;
import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.OnGetObjectListener;
import com.healthyteam.android.healthylifers.Domain.TestFunctions;
import com.healthyteam.android.healthylifers.Domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//ctrl + o = open event handlers menu

public class MainActivity extends AppCompatActivity {
    //region test
    List<String> UserIds;
    int index=0;
    //endregion
    private Dialog logOutDialog;
    private Dialog settingsDialog;
    private Dialog addItemDialog;
    private Dialog locationViewDialog;
    private ImageButton btnExit;
    private Fragment searchFragment = null;
    private Fragment settingsFragment = null;
    private Fragment tabInfoFragment;
    private Fragment tabCommentsFragment;
    private SectionsPageAdapter mSectionPageAdapter;
    private FirebaseAuth mAuth;
    private Button yesLogOut;
    private Button noLogOut;
    private Button infoButton;
    private Button commentsButton;
    private Fragment activeFragment;

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //should call from SignIn Activity


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

        infoButton = locationViewDialog.findViewById(R.id.info_button);
        commentsButton = locationViewDialog.findViewById(R.id.comments_button);






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


    }


    private void signOut() {
        mAuth.signOut();
        Intent i = new Intent(getApplicationContext(),SignInActivity.class);
        //TODO update user account before log out.
        //TODO maybe update domain controler
        startActivity(i);

      //  updateUI(null);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private Fragment getActiveFragment()
    {
        for(Fragment frag : getSupportFragmentManager().getFragments())
        {
            if(frag.isVisible())
                return  frag;


        }

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {
            case R.id.app_bar_search:
                if(searchFragment == null)
                {
                    activeFragment= getActiveFragment();
                    searchFragment = new SearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_replace,searchFragment).commit();
                }
                else
                {
                    getSupportFragmentManager().beginTransaction().hide(searchFragment).commit();
                    searchFragment=null;
                }

                break;

            case R.id.app_bar_settings:
                if(settingsFragment == null)
                {
                    activeFragment = getActiveFragment();
                    settingsFragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_replace,settingsFragment).commit();

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
    void testFunction(){
            getUsersIDs(new OnGetObjectListener() {
            @Override
            public void OnSuccess(Object o) {
                final User u = TestFunctions.createUser();

                User.signIn(u.getEmail(), u.getUsername(), new OnRunTaskListener() {
                    @Override
                    public void OnStart() {

                    }

                    @Override
                    public void OnComplete(Task<?> task) {
                        if(task.isComplete()){
                            u.setUID(mAuth.getCurrentUser().getUid());
                            u.setFriendIds(getRandomIds(u.getUID()));
                            u.Save();
                            setRadnomImageUri(u);
                            DomainController.setUser(u);
                            index++;
                            if(index<50)
                                testFunction();

                        }
                        else{
                            createNewUser(u);
                            DomainController.setUser(u);
                        }
                    }
                });

            }
        });


    }
    void createNewUser(final User u){
        User.createAccount(u.getEmail(), u.getUsername(), new OnRunTaskListener() {
            @Override
            public void OnStart() {

            }

            @Override
            public void OnComplete(Task<?> task) {
                User nUser;
                if(task.isComplete()){
                    nUser = User.cloneUser(u);

                    nUser.setUID(mAuth.getCurrentUser().getUid());
                    nUser.Save();
                    DomainController.setUser(nUser);
                }

            }
        });
    }

    void setRadnomImageUri(final User u){
        int num = TestFunctions.randBetween(1,36);
        File avatarFile = new File(Environment.getExternalStorageDirectory() + "/Avatar/" + num +".png");
        Log.i("beforeScan", avatarFile.getPath());
        MediaScannerConnection.scanFile(this,
                new String[] { avatarFile.getAbsolutePath() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("onScanCompleted", uri.getPath());
                        boolean fileSelected = u.UpdatePicture(uri, new OnUploadDataListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess() {
                                Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailed(Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (!fileSelected)
                            Toast.makeText(MainActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    void getUsersIDs(final OnGetObjectListener listener) {
        if (UserIds == null) {
            UserIds = new ArrayList<>();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child(Constants.UsersNode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                        UserIds.add(ds.getKey());
                    listener.OnSuccess(UserIds);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else
            listener.OnSuccess(null);
    }
    List<String> getRandomIds(String UserId){
        List<String> list= new ArrayList<>();
        for(int i=0; i<10;i++) {
            int index= TestFunctions.randBetween(0,UserIds.size()-1);
            if(!UserId.equals(UserIds.get(index)))
                list.add(UserIds.get(index));
        }

        return list;
    }
}
