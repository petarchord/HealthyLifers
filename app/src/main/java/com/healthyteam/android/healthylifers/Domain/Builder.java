package com.healthyteam.android.healthylifers.Domain;

import android.graphics.Bitmap;

import java.util.List;

public interface Builder {
    void setPicture(Bitmap LocationPic);
    void setName(String Name);
    void setDescripition(String Desc);
    void setDateAdded(String DateAdded);
    void setCategory(UserLocation.Category category);
    void setLikeCount(int Count);
    void setDislikeCount(int Count);
    void setUserRate(UserLocation.Rate Rate);
    void setLangitude(Double lan);
    void setLongitude(Double lon);
    void setCommentCount(int Count);
    void setTags(List<String> tags);
    void setAuthor(User Author);



}
