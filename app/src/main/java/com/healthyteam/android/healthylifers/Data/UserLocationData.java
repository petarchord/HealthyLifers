package com.healthyteam.android.healthylifers.Data;

import com.google.firebase.database.Exclude;
import com.healthyteam.android.healthylifers.Domain.DBReference;

public class UserLocationData implements DBReference {
    public Double Latitude;
    public Double Longitude;
    public String Username;
    @Exclude
    public String UID;

    public UserLocationData(){}

    public void setUID(String uid){
        UID=uid;
    }
    public void setLatitude(Double lat){
        Latitude=lat;
    }
    public void setLongitude(Double lon){
        Longitude=lon;
    }
    public void setUsername(String username){
        this.Username = username;
    }

    @Override
    public String getUID() {
        return UID;
    }

    public String getUsername(){
        return this.Username;
    }
    public Double getLongitude(){
        return Longitude;
    }
    public Double getLatitude(){
        return Latitude;
    }
}
