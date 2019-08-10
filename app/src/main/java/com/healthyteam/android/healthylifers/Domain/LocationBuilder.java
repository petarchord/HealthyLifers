package com.healthyteam.android.healthylifers.Domain;

import android.graphics.Bitmap;

import java.util.List;

public class LocationBuilder implements Builder {
    private Bitmap LocaitonPic;
    private String Name;
    private String Descripition;
    private String DateAdded;
    private List<String> TagList;
    private Location.Category Category;
    private int likeCount;
    private int dislikeCount;
    private Location.Rate UserRate;
    private Double lan;
    private Double lon;
   // private List<Comments> Comments;
    //ako je neophodno da se dovlace svi objekti komentara iz baze, onda atribut ispod nije potreban
    private int commentCount;
    private User Author;


    @Override
    public void setPicture(Bitmap locaitonPic){
        this.LocaitonPic=locaitonPic;
    }
    @Override
    public void setName(String Name) {
        this.Name=Name;
    }

    @Override
    public void setDescripition(String Desc) {
        this.Descripition=Desc;
    }

    @Override
    public void setDateAdded(String DateAdded) {
        this.DateAdded=DateAdded;
    }

    @Override
    public void setCategory(Location.Category category) {
        this.Category=category;
    }

    @Override
    public void setLikeCount(int Count) {
        this.likeCount=Count;
    }

    @Override
    public void setDislikeCount(int Count) {
        this.dislikeCount=Count;
    }

    @Override
    public void setUserRate(Location.Rate Rate) {
        this.UserRate=Rate;
    }

    @Override
    public void setLangitude(Double lan) {
        this.lan=lan;
    }

    @Override
    public void setLongitude(Double lon) {
        this.lon=lon;
    }

    @Override
    public void setCommentCount(int Count) {
        this.commentCount=Count;
    }
    @Override
    public void setAuthor(User Author){
        this.Author=Author;
    }

    public Location getResult(){
        return new Location();
    }

    @Override
    public void setTags(List<String> tags) {
        this.TagList=tags;
    }
}
