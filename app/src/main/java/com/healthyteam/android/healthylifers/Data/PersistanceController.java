package com.healthyteam.android.healthylifers.Data;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.healthyteam.android.healthylifers.Domain.Constants;
import com.healthyteam.android.healthylifers.Domain.User;

public class PersistanceController {
    private static DatabaseReference dbReference;
    private DatabaseReference getDBReference(){
        if(dbReference==null)
            dbReference= FirebaseDatabase.getInstance().getReference();
        return this.dbReference;
    }
    //Read
    void getUser(String uid, final User user){
        getDBReference().child(Constants.UsersNode).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user.setData(dataSnapshot.getValue(UserData.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Write

    //Update

    //Delete
}
