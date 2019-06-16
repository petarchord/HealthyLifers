package com.healthyteam.android.healthylifers.Domain;

import android.media.Image;

import java.util.LinkedList;
import java.util.Random;
import java.util.List;


public class UserTest {
    private static UserTest instance;

    private String Name;
    private String Surname;
    private String Username;
    private Integer Points;
    private Image profileImage;
    private List<UserTest> friendList;


    //region test
    static final String[] Names={"Stefan","Petar", "Milica", "Jovan", "Jovana", "Mitar", "Milena", "Marko", "Jovana"};
    static final String[] Surnames={"Jovanovic", "Petokovic", "Stankovic", "Jevtic", "Stefanovic", "Petrovic"};
    static final Random ran=new Random();

    //konstruktor ce biti public
    private UserTest(String name, String surname, String username, Integer points) {
        Name = name;
        Surname = surname;
        Username = username;
        Points = points;
    }

    //umesto ove, funkcija koja dovlaci prijatelje iz baze
    private static List createFriendList(){
        List<UserTest> friends= new LinkedList<UserTest>();
        for(int i=0;i<20;i++){
            String name= Names[i%Names.length];
            String surname= Surnames[i%Surnames.length];
            int randomNum = ran.nextInt(900)+100;
            String username= name + randomNum;
            Integer points = randomNum;
            friends.add(new UserTest(name,surname,username,points));

        }
        return friends;
    }

    //privremeno je Singleton. Druga klasa ce cuvati referencu na UserTest objekat logovanog korisnika
     public static UserTest getInstance(){
        if(UserTest.instance==null) {
            //get basic info of user from database
            instance = new UserTest("Pera", "Peric", "Pera123", 123);
        }
        return instance;
    }
    //endregion

    //region Getter
    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public Integer getPoints() {
        return Points;
    }

    public Image getProfileImage() {
        return profileImage;
    }

    public List<UserTest> getFriendList() {
        if(friendList==null){
            //get friends from database ~ lazyload like
            setFriendList(UserTest.createFriendList());
        }
        return friendList;
    }

    public String getUsername(){
        return this.Username;
    }

    //endregion

    //region Setter
    public void setName(String name) {
        Name = name;
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

    public void setProfileImage(Image profileImage) {
        this.profileImage = profileImage;
    }

    public void setFriendList(List<UserTest> friendList) {
        this.friendList = friendList;
    }
    //endregion

    //region Methods

    //endregion
}
