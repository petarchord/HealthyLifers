package com.healthyteam.android.healthylifers.Domain;



import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.healthyteam.android.healthylifers.Data.OnRunTaskListener;
import com.healthyteam.android.healthylifers.Data.OnUploadDataListener;
import com.healthyteam.android.healthylifers.Data.UserData;

import java.util.ArrayList;
import java.util.List;


public class User implements  DBReference{
    private UserData data;
    private List<User> friendList;
    private List<OnGetListListener> friendListeners;
    private List<OnGetListListener> postListeners;
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



    //region db


    public List<Location> getPosts() {
        //get posts from database
        if(Posts==null){
            Posts=DomainController.getLocationsFor(this);
        }
        return Posts;
    }

    //endregion

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


    public String getUID(){
       return data.UID;
    }
    public String getUsername(){
        return data.Username;
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
    public String getCity(){
        return data.City;
    }
    public Double getLatitude(){
        return data.Latitude;
    }
    public Double getLongitude(){
        return data.Longitude;
    }
    //endregion

    //region Test
    public void setUID(String uid){
        data.UID=uid;
    }
    public static User cloneUser(User user){
        User cloneUser =new User();
        cloneUser.setUID(user.getUID());
        cloneUser.setName(user.getName());
        cloneUser.setSurname(user.getSurname());
        cloneUser.setEmail(user.getEmail());
        cloneUser.setUsername(user.getUsername());
        cloneUser.setLatitude(user.getLatitude());
        cloneUser.setLongitude(user.getLongitude());
        cloneUser.setCity(user.getCity());
        cloneUser.setPoints(user.getPoints());
        return cloneUser;
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
    //test region
    public void setFriendIds(List<String> Ids){
        data.FriendsIds=Ids;
    }

    //endregion

    //endregion

    //region Methods
    public static void getUser(String UID, final OnGetObjectListener listener){
        DatabaseReference userReference =getDatabase().child(Constants.UsersNode).child(UID);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user=  new User();
                    user.setData(dataSnapshot.getValue(UserData.class));
                    user.setUID(dataSnapshot.getKey());
                    listener.OnSuccess(user);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public static void createAccount(String email, String password,final OnRunTaskListener listener) {
        listener.OnStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.OnComplete((Task<AuthResult>) task);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("CreateAccount:",e.getMessage());
            }
        });
    }
    public static void signIn(String email, String password,final OnRunTaskListener listener) {
        listener.OnStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.OnComplete((Task<AuthResult>) task);
                    }
                });
    }
    public int getFriendIndex(String uid){
        if(friendList==null)
            return  -1;
        for(int i=0; i<friendList.size();i++){
            if(friendList.get(i).getUID().equals(uid))
                return i;
        }
        return -1;
    }

    public void addGetFriendListener(OnGetListListener listener){
        if(friendListeners==null)
            friendListeners=new ArrayList<>();
        friendListeners.add(listener);
        getFriendList();
    }
    private void getFriendList() {
        if(friendList==null){
            friendList=new ArrayList<>();
            for(final String friendId:this.getFriendsIds())
            {
                getFriend(friendId, new OnGetObjectListener() {
                    @Override
                    public void OnSuccess(Object o) {
                        User user = (User) o;
                        int friendIndex = getFriendIndex(user.getUID()) ;
                        if(friendIndex!=-1) {
                            friendList.set(friendIndex, user);
                            for(OnGetListListener listener:friendListeners)
                                listener.onChildChange(friendList,friendIndex);
                        }
                        else{
                            friendList.add((User) o);
                            for(OnGetListListener listener:friendListeners)
                                listener.onChildAdded(friendList,friendIndex);
                        }
                        if(friendList.size()==getFriendsIds().size()){
                            for(OnGetListListener listener:friendListeners)
                                listener.onListLoaded(friendList);
                        }
                    }
                });
            }
        }
        else
            for(OnGetListListener listener:friendListeners)
                listener.onListLoaded(friendList);
    }
    public void addFriend(String uid){
        data.FriendsIds.add(uid);
        getDatabase().child(Constants.UsersNode).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                user.setData(dataSnapshot.getValue(UserData.class));
                friendList.add(user);
                Save();
                for(OnGetListListener listener:friendListeners)
                    listener.onChildAdded(friendList,friendList.size()-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                for(OnGetListListener listener:friendListeners)
                    listener.onCanclled(databaseError);
            }
        });

    }
    public void deleteFriend(String uid){
        final int friendIndex = getFriendIndex(uid);
        data.FriendsIds.remove(uid);
        getDatabase().child(Constants.UsersNode).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                user.setData(dataSnapshot.getValue(UserData.class));
                user.getFriendsIds().remove(getUID());
                user.Save();
                Save();

                User removedFriend = friendList.get(friendIndex);
                friendList.remove(friendIndex);
                for(OnGetListListener listener:friendListeners)
                     listener.onChildRemove(friendList,friendIndex,removedFriend);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                for(OnGetListListener listener:friendListeners)
                    listener.onCanclled(databaseError);
            }
        });

    }
    public void Save(){
        try {
            FirebaseDatabase.getInstance().getReference().child(Constants.UsersNode).child(getUID()).setValue(data).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.println(Log.WARN, "Database:", e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.println(Log.WARN, "Database: ", "Success");
                }
            });
        }
        catch(Exception e){
            Log.println(Log.ERROR, "Database:", e.getMessage());
        }
    }
    private  void getFriend(String UID, final OnGetObjectListener listener){
        DatabaseReference userReference =getDatabase().child(Constants.UsersNode).child(UID);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=  new User();
                user.setData(dataSnapshot.getValue(UserData.class));
                user.setUID(dataSnapshot.getKey());
                listener.OnSuccess(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public boolean UpadatePicture(final Uri ImageUri, final OnUploadDataListener listener ) {
        listener.onStart();
        if (ImageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().
                    getReference("uploads").
                    child(String.valueOf( getUID()));

            fileReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot.getMetadata().getReference()!=null) {
                                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        setProfileImage(uri.toString());
                                        Save();
                                        listener.onSuccess();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onFailed(e);
                        }
                    });
            return true;
        }
        else {
            return false;
        }
    }





    //endregion

    private static DatabaseReference mDatabase;
    private static DatabaseReference getDatabase() {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase;
    }





}
