package com.healthyteam.android.healthylifers.Domain;



import android.net.Uri;

import com.healthyteam.android.healthylifers.Data.OnUploadDataListener;
import com.healthyteam.android.healthylifers.Data.UserData;

import java.util.List;


public class User {
    private UserData data;
    private List<User> friendList;
    private List<Location> Posts;

    //u konacnoj imp dodati jos email, lan, lon
    public User(String name, String surname, String username, Integer points) {
        data = new UserData();
        data.Name = name;
        data.Surname = surname;
        data.Username = username;
        data.Points = points;
    }

    public User(){
        data = new UserData();
        }


    //region Getter
    public String getName() {
        return data.Name;
    }

    public String getEmail() {
        return data.Email;
    }

    public String getSurname() {
        return data.Surname;
    }

    public Integer getPoints() {
        return data.Points;
    }

    public String getPointsStirng(){return data.Points.toString();}
    public String getImageUrl() {
        return data.ImageUrl;
    }

    public List<User> getFriendList() {
        //get friends form database
        if(friendList==null){
            //get friends from database ~ lazyload like
            setFriendList(PersistenceController.getFriendFor(this));
        }
        return friendList;
    }
    public String getUID(){
       return data.UID;
    }
    public String getUsername(){
        return data.Username;
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
    public List<String> getFriendsIds(){
        return data.FriendsIds;
    }
    public List<String> getPostsIds(){
        return data.PostsIds;
    }

    //endregion
    //region Test
    public void setUID(String uid){
        data.UID=uid;
    }

    //endregion
    //region Setter
    public void setName(String name) {
        data.Name = name;
    }

    public void setEmail(String email) {
        data.Email = email;
    }

    public void setSurname(String surname) {
        data.Surname = surname;
    }

    public void setUsername(String username) {
        data.Username = username;
    }

    public void setPoints(Integer points) {
        data.Points = points;
    }

    public void setProfileImage(String imageUrl) {
        data.ImageUrl = imageUrl;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }
    public void setLatitude(Double lat){
        data.Latitude=lat;
    }
    public void setLongitude(Double lon){
        data.Longitude= lon;
    }
    public void setCity(String city){
        data.City=city;

    }
    public void setData(UserData data){
        this.data=data;
    }
    //endregion

    //region Methods
    public void deleteFriend(User friend){
        //delete from database
        getFriendList().remove(friend);
    }

    public void Save(){
        data.Save();
    }
    public  boolean UpadatePicture(Uri imageUri, OnUploadDataListener listener){
        return  data.uploadPhoto(imageUri,listener);
    }

    //endregion
}
