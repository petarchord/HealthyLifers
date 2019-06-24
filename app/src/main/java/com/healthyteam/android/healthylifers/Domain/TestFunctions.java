package com.healthyteam.android.healthylifers.Domain;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TestFunctions {
    public static Integer userCount=20;


    public static final String[] Names={"Stefan","Petar", "Milica", "Jovan", "Jovana", "Mitar", "Milena", "Marko", "Jovana"};
    public static final String[] Surnames={"Jovanovic", "Petokovic", "Stankovic", "Jevtic", "Stefanovic", "Petrovic"};
    public static final String[] Tags={"fintess","sport","fudbal","kosraka","tenis","teretana","kuglanje","streljana"};
    public static final Random ran=new Random();

    public static List createFriendList(){
        List<User> friends= new LinkedList<User>();
        for(int i=0;i<userCount;i++){

            friends.add(createUser());

        }
        return friends;
    }
    public static User createUser(){
        int i = randBetween(0,Names.length);
        String name= Names[i%Names.length];
        String surname= Surnames[i%Surnames.length];
        int randomNum = ran.nextInt(900)+100;
        String username= name + randomNum;
        Integer points = randomNum;
        return new User(name,surname,username,points);

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
