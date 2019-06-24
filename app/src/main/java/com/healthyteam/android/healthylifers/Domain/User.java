package com.healthyteam.android.healthylifers.Domain;

import android.graphics.Bitmap;
import java.util.List;


public class User {
    private static User instance;



    private String Email;
    private String Name;
    private String Surname;
    private String Username;
    private Integer Points;
    private Bitmap profileImage;
    private List<User> friendList;
    private Double lan;
    private Double lon;
    private List<Location> Posts;

    //u konacnoj imp dodati jos email, lan, lon
    public User(String name, String surname, String username, Integer points) {
        Name = name;
        Surname = surname;
        Username = username;
        Points = points;
    }



    //region Getter
    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getSurname() {
        return Surname;
    }

    public Integer getPoints() {
        return Points;
    }

    public String getPointsStirng(){return Points.toString();}
    public Bitmap getProfileImage() {
        return profileImage;
    }

    public List<User> getFriendList() {
        //get friends form database
        if(friendList==null){
            //get friends from database ~ lazyload like
            setFriendList(PersistenceController.getFriendFor(this));
        }
        return friendList;
    }

    public String getUsername(){
        return this.Username;
    }

    public List<Location> getPosts() {
        //get posts from database
        if(Posts==null){
            Posts=DomainController.getLocationsFor(this);
        }
        return Posts;
    }
    public Integer getPostsCount(){
        return getPosts().size();
    }

    //endregion

    //region Setter
    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPoints(Integer points) {
        Points = points;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }
    //endregion

    //region Methods
    public void deleteFriend(User friend){
        //delete from database
        getFriendList().remove(friend);
    }
    //endregion
}
