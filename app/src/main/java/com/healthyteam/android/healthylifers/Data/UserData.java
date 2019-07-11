package com.healthyteam.android.healthylifers.Data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.healthyteam.android.healthylifers.Domain.Constants;
import com.healthyteam.android.healthylifers.Domain.User;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class UserData {
    public String ImageUrl;
    public String Email;
    public String Name;
    public String Surname;
    public String Username;
    public Integer Points;
    public Double Latitude;
    public Double Longitude;
    public String City;
    public List<String> FriendsIds;
    public List<String> PostsIds;

    public UserData (){
        FriendsIds=new ArrayList<String>();
        PostsIds = new ArrayList<String>();
    }
    @Exclude
    public String UID;


}
