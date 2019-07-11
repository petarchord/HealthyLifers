package com.healthyteam.android.healthylifers;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.SigningInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.healthyteam.android.healthylifers.Data.OnRunTaskListener;
import com.healthyteam.android.healthylifers.Data.UserData;
import com.healthyteam.android.healthylifers.Domain.User;


public class SignInActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Button signIn;
    private Button register;
    private Button signInSubmit;
    private Button registerSubmit;
    private EditText signinEmail;
    private EditText signinPassword;
    private EditText registerEmail;
    private EditText registerName;
    private EditText registerSurname;
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerPasswordRepeat;
    private Dialog signInWindow;
    private Dialog registerWindow;
    private ImageButton exitSignIn;
    private ImageButton exitRegister;
    private Context context;
    private static final String TAG = "SignInActivity";

    static final int PERMISSION_ACCESS_LOCATION_STORAGE=1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        signIn = (Button) findViewById(R.id.btnSignIn);
        register = (Button) findViewById(R.id.btnRegister);
        signInWindow = new Dialog(this);
        signInWindow.setContentView(R.layout.customsignin);
        registerWindow =new Dialog(this);
        registerWindow.setContentView(R.layout.customregister);


        signinEmail = signInWindow.findViewById(R.id.signinEmail);
        signinPassword = signInWindow.findViewById(R.id.signinPassword);

        registerEmail = registerWindow.findViewById(R.id.editTextEmailRegister);
        registerName = registerWindow.findViewById(R.id.editTextNameRegister);
        registerSurname = registerWindow.findViewById(R.id.editTextSurnameRegister);
        registerUsername = registerWindow.findViewById(R.id.editTextUsernameRegister);
        registerPassword = registerWindow.findViewById(R.id.editTextPassRedgister);
        registerPasswordRepeat = registerWindow.findViewById(R.id.editTextPassRepeatRegister);

        exitSignIn = (ImageButton) signInWindow.findViewById(R.id.closeSignInForm);
        exitRegister = (ImageButton) registerWindow.findViewById(R.id.closeRegisterForm);

        signInSubmit = (Button) signInWindow.findViewById(R.id.signInSubmit);
        registerSubmit = (Button) registerWindow.findViewById(R.id.registerSubmit);


        signInSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Sign in clicked!",Toast.LENGTH_SHORT).show();
                String email = signinEmail.getText().toString();
                String password = signinPassword.getText().toString();
                signIn(email,password);

            }
        });

        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                createAccount(email,password);

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

        if(!hasPermissions())
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_ACCESS_LOCATION_STORAGE);

    }

/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    } */





 /*   private void signOut() {
        mAuth.signOut();
        updateUI(null);
    } */


    private boolean validateRegisterForm() {
        boolean valid = true;

        String email = registerEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            registerEmail.setError("Required.");
            valid = false;
        } else {
            registerEmail.setError(null);
        }

        String password = registerPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            registerPassword.setError("Required.");
            valid = false;
        } else {
            registerPassword.setError(null);
        }

       /* String passwordRepeat = registerPasswordRepeat.getText().toString();
        if (TextUtils.isEmpty(passwordRepeat)) {
            registerPasswordRepeat.setError("Required.");
            valid = false;
        } else {
            registerPasswordRepeat.setError(null);
        }

        String username = registerUsername.getText().toString();
        if (TextUtils.isEmpty(username)) {
            registerUsername.setError("Required.");
            valid = false;
        } else {
            registerUsername.setError(null);
        }

        String name = registerName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            registerName.setError("Required.");
            valid = false;
        } else {
            registerName.setError(null);
        }

        String surname = registerSurname.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            registerSurname.setError("Required.");
            valid = false;
        } else {
            registerSurname.setError(null);
        } */

        return valid;
    }


    private boolean validateSigninForm()
    {
        boolean valid = true;

        String email = signinEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            signinEmail.setError("Required.");
            valid = false;
        } else {
            signinEmail.setError(null);
        }

        String password = signinPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            signinPassword.setError("Required.");
            valid = false;
        } else {
            signinPassword.setError(null);
        }

        return valid;




    }


    private void createAccount(final String email, final String password) {
        User.createAccount(email, password, new OnRunTaskListener() {
            @Override
            public void OnStart() {
                Log.d(TAG, "createAccount:" + email);
                if (!validateRegisterForm()) {
                    return;
                }
            }

            @Override
            public void OnComplete(Task<?> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });

    }

    private void signIn(final String email, final String password) {
        User.signIn(email, password, new OnRunTaskListener() {
            @Override
            public void OnStart() {
                Log.d(TAG, "signIn:" + email);
                if (!validateSigninForm()) {
                    return;
                }
            }

            @Override
            public void OnComplete(Task<?> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    //TODO: create new user, initilize wtih database data, activate it and call DataConttroler.SetUser(...)
                    //TODO: deactivate user when log out
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });

    }
    private void updateUI(FirebaseUser user) {
       // hideProgressDialog();
        if (user != null) {


            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
         /*   mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
             */


        } else {
            return;


            /*
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            findViewById(R.id.signedInButtons).setVisibility(View.GONE); */
        }
    }

    //TODO:check with <APi23
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode){
                case PERMISSION_ACCESS_LOCATION_STORAGE:
                    if(!(grantResults.length>1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    || !(grantResults[1] == PackageManager.PERMISSION_GRANTED) )
                        this.finish();
                    return;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
