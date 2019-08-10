package com.healthyteam.android.healthylifers.Data;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class CommentData {
    public String Text;
    public String AuthorID;
    public List<String> likedBy;
    public List<String> dislikedBy;

    public CommentData(){
        likedBy=new ArrayList<>();
        dislikedBy= new ArrayList<>();
    }

    @Exclude
    public String UID;
}
