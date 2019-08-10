package com.healthyteam.android.healthylifers.Domain;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.healthyteam.android.healthylifers.Data.CommentData;

public  class Comment {
    private CommentData Data;
    private User Creator;

    public Comment() {}

    //region private Methodes


    //endregion

    //region Setter
    public void setData(CommentData data){
        this.Data=data;
    }
    public void setUID(String uid){
        this.Data.UID = uid;
    }
    public void setText(String text){
        Data.Text = text;
    }
    public void setCreator(User user){
        Creator = user;
        Data.AuthorID=user.getUID();
    }
    //endregion

    //region Getter
    public String getUID(){
        return Data.UID;
    }

    //endregion

    //region dbMethodes
    public void Save(final OnSuccessListener listener){
        try {
            FirebaseDatabase.getInstance().getReference().child(Constants.CommentsNode).child(getUID()).setValue(Data).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.println(Log.WARN, "Database:", e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.println(Log.WARN, "Database: ", "Success");
                    if(listener!=null)
                        listener.onSuccess(aVoid);
                }
            });
        }
        catch(Exception e){
            Log.println(Log.ERROR, "Database:", e.getMessage());
        }
    }

    //endregion
}
