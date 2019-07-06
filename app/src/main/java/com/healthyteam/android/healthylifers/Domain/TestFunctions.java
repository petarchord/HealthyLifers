package com.healthyteam.android.healthylifers.Domain;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TestFunctions {
    private static Context context;
    public static Integer userCount=20;
    public static Integer userIndex=0;
    public static Double minLatitude = 43.29015766810717;
    public static Double maxLatitude = 43.356438740445036;
    public static Double minLongitude = 21.822279839021746;
    public static Double maxLongitude = 21.99648510150797;
    public static final String testString = "test";
    public static final String[] Names={"Stefan","Petar", "Milica", "Jovan", "Jovana", "Mitar", "Milena", "Marko", "Jovana"};
    public static final String[] Surnames={"Jovanovic", "Petkovic", "Stankovic", "Jevtic", "Stefanovic", "Petrovic"};
    public static final String[] Tags={"fintess","sport","fudbal","kosraka","tenis","teretana","kuglanje","streljana"};
    public static final Random ran=new Random();
    public static List createFriendList(){
        List<User> friends= new LinkedList<User>();
        for(int i=0;i<userCount;i++){

            friends.add(createUser());

        }
        return friends;
    }
    public static void setContext(Context con){
        context=con;
    }
    public static Double getRanLatitude(){
        Double range = maxLatitude- minLatitude;
        return minLatitude + (range * Math.random());
    }
    public static Double getRanLongitude(){
        Double range = maxLongitude - minLongitude;
        return minLongitude + (range * Math.random());
    }
    public static User createUser(){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        int i = randBetween(0,Names.length);
        String name= Names[i%Names.length];
        String surname= Surnames[i%Surnames.length];
        String email = testString +userIndex + "@" + testString + ".com";
        String username = testString + userIndex;
        Double longitude = getRanLongitude();
        Double latitude = getRanLatitude();
        String City="";
        try {
             List<Address> addresses= geocoder.getFromLocation(latitude, longitude, 1);
            City = addresses.get(0).getLocality();
        }
        catch (Exception e){
            Log.println(Log.ERROR,"Geocoder", e.getMessage());
        }
        int randomNum = ran.nextInt(900)+100;
        Integer points = randomNum;

        User user = new User();
        //bice zamenjeno kad budem koristio auth funckiju
        String UID = FirebaseDatabase.getInstance().getReference().push().getKey();
        user.setUID(UID);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setUsername(username);
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setCity(City);
        user.setPoints(points);
        return user;

    }

    public static String returnRandomDate(){
        SimpleDateFormat dfDateTime  = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        int year = 2019;// Here you can set Range of years you need
        int month = randBetween(1, 7);

        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = randBetween(1, gc.getActualMaximum(gc.DAY_OF_MONTH));

        gc.set(day, month, year);

        return dfDateTime.format(gc.getTime());

    }
    public static List<String> returnRandomTags(){
        int tagNum= randBetween(0,6);
        List<String> tags = new LinkedList<String>();
        int tagIndex;
        for(int i=0;i<tagNum;i++){
            tagIndex=randBetween(0,7);
            tags.add(Tags[tagIndex]);
        }
        return tags;
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }


}
