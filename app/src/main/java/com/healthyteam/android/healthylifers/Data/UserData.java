package com.healthyteam.android.healthylifers.Data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    @Exclude
    public String UID;
    @Exclude
    private static DatabaseReference mDatabase;

    public UserData (){
        ImageUrl=null;
        Email=null;
        FriendsIds=new ArrayList<String>();
        PostsIds = new ArrayList<String>();
    }

    @Exclude
    public void Save(){
        try {
            FirebaseDatabase.getInstance().getReference().child(Constants.UsersNode).child(UID).setValue(this).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.println(Log.WARN, "Database:", e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.println(Log.WARN, "Database: ", "Success");
                }
            });
        }
        catch(Exception e){
            Log.println(Log.ERROR, "Database:", e.getMessage());
        }
    }
    @Exclude
    public boolean uploadPhoto(final Uri ImageUri, final OnUploadDataListener listener ) {
        listener.onStart();
        if (ImageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().
                    getReference("uploads").
                    child(String.valueOf( UID));

            fileReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         //   Toast.makeText(context, "Upload successful", Toast.LENGTH_LONG).show();
                            ImageUrl= taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            Save();
                            listener.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onFailed(e);
                       //     Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return true;
        }
        else {
            return false;
        }
    }
    //TODO: testiraj funkcije za citanje iz baze. Mozda se rezultat koji vracaju menja sa referencom koju vracaju
    private static DatabaseReference getDatabase() {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase;
    }
    public static void getUser(String UID, final OnGetDataListener listener){
        listener.onStart();
        getDatabase().child(Constants.UsersNode).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }
    @Exclude
    public static void  getUsers(final OnGetDataListener listener){
        listener.onStart();
        getDatabase().child(Constants.UsersNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });

    }

    @Exclude
    public static void getWorldUsers(final OnGetDataListener listener){
        listener.onStart();
        Query query =getDatabase().child(Constants.UsersNode).orderByChild(Constants.UserPointsAtt).limitToLast(Constants.showUserCount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //send in ascending order
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }
    @Exclude
    public static void getMoreWorldUsers(List<User> worldList, final OnGetDataListener listener){
        listener.onStart();
        Query query =getDatabase().child(Constants.UsersNode).orderByChild(Constants.UserPointsAtt)
                .limitToLast(worldList.size()+Constants.showUserCount)
                .limitToFirst(Constants.showUserCount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //send in ascending order. Listener need to check last elements
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
