package com.healthyteam.android.healthylifers.Domain;

import android.graphics.Bitmap;

import java.util.LinkedList;
import java.util.List;


public class PersistenceController {
    private static final int startWorldUserCount=50;

    public static List<User> getFriendFor(User u){
        //get user from database
        return TestFunctions.createFriendList();
    }
    //below is test funciton
    public static List<User> getWorldUsers(){
        //get user for presentation from database
        List<User> WorldUsers;
        TestFunctions.userCount=startWorldUserCount;
        WorldUsers =TestFunctions.createFriendList();
        TestFunctions.userCount=20;
        return WorldUsers;
    }


    public static List<User> getMoreWorldUsers(List<User> WorldUsers){
        //get more users with taking count of how much is already fetched
        WorldUsers.addAll(TestFunctions.createFriendList());
        return WorldUsers;
    }

    public  static List<UserLocation> getLocationFor(User u){
        LinkedList<UserLocation> userLocations = new LinkedList<UserLocation>();
        //get location for User from database
        Bitmap picture=null;
        String name= "Location name";
        String description="Location description";
        Double lan=99.9999;
        Double lon=88.8888;
        int LocationCount= TestFunctions.randBetween(5,20);

        LocationBuilder builder = new LocationBuilder();
        //only builder function in loop in final implementation
        for (int i=0;i<LocationCount;i++){
            //test
            String dateAdded=TestFunctions.returnRandomDate();
            int likeCount = TestFunctions.randBetween(0,300);
            int dislikeCount= TestFunctions.randBetween(0,300);
            int commentCount= TestFunctions.randBetween(0,30);
            int rate=TestFunctions.randBetween(0,2);
            int category = TestFunctions.randBetween(0,3);
            List<String> tags = TestFunctions.returnRandomTags();
            //
            builder.setPicture(picture);
            builder.setName(name);
            builder.setDescripition(description);
            builder.setLangitude(lan);
            builder.setLongitude(lon);
            builder.setDateAdded(dateAdded);
            builder.setLikeCount(likeCount);
            builder.setDislikeCount(dislikeCount);
            builder.setCommentCount(commentCount);
            builder.setUserRate(UserLocation.Rate.values()[rate]);
            builder.setCategory(UserLocation.Category.values()[category]);
            builder.setTags(tags);
            userLocations.add(builder.getResult());
        }
        return userLocations;



    }

    public static List<Comment> getCommentsFor(UserLocation l){
        LinkedList<Comment> commentList = new LinkedList<Comment>();
        String text = "Comment content";
        for(int i=0;i<l.getCommentCount();i++) {
            User Author = TestFunctions.createUser();
            int likeCount = TestFunctions.randBetween(0,300);
            int dislikeCount= TestFunctions.randBetween(0,300);
            Comment comm = new Comment();
            comm.setCreator(Author);
            comm.setText(text);
            commentList.add(comm);


        }
        return  commentList;
    }
}
