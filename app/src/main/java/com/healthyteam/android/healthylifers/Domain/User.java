package com.healthyteam.android.healthylifers.Domain;



import android.location.Location;
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
import com.healthyteam.android.healthylifers.Data.LocationData;
import com.healthyteam.android.healthylifers.Data.OnRunTaskListener;
import com.healthyteam.android.healthylifers.Data.OnUploadDataListener;
import com.healthyteam.android.healthylifers.Data.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User implements  DBReference{
    private UserData data;
    private List<User> friendList;
    private List<OnGetListListener> friendListeners;
    private List<OnGetListListener> postListeners;
    private List<UserLocation> Posts;

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
        friendList=new ArrayList<>();
        Posts = new ArrayList<>();
        }


    //region Getter


    //region db related
    public int getFriendIndex(String uid){
        for(int i=0; i<friendList.size();i++){
            if(friendList.get(i).getUID().equals(uid))
                return i;
        }
        return -1;
    }
    public int getPostIndex(String uid){
        if(Posts==null)
            return  -1;
        for(int i=0; i<Posts.size();i++){
            if(Posts.get(i).getUID().equals(uid))
                return i;
        }
        return -1;
    }
    public User getFriendByUid(String uid){
        int index = getFriendIndex(uid);
        if(index!=-1)
            return friendList.get(index);
        return null;
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
        return data.PostsIds.size();
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
    public void setLocation(Location location){
        setLatitude(location.getLatitude());
        setLongitude(location.getLongitude());
    }
    public void setCity(String city){
        data.City=city;

    }
    //TODO: maybe create new function for coordinate update where function will update city
    public void setData(UserData data){
        this.data=data;
    }
    //test region
    public void setFriendIds(List<String> Ids){
        data.FriendsIds=Ids;
    }


    //endregion

    //region Methods
    public static void getUser(String UID, final OnGetObjectListener listener)//take Data once
    {
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
    private void getFriend(String UID, final OnGetObjectListener listener)//trigger every time Data change
    {
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

    public void addGetFriendListener(OnGetListListener listener){
        if(friendListeners==null)
            friendListeners=new ArrayList<>();
        if(!friendListeners.contains(listener))
            friendListeners.add(listener);
        getFriendList(listener);
    }
    public void addGetPostsListener(OnGetListListener listener){
        if(postListeners==null)
            postListeners=new ArrayList<>();
        if(!postListeners.contains(listener))
            postListeners.add(listener);
        getPosts(listener);
    }

    private void getFriendList(final OnGetListListener listener) {
        if(friendList.size()==0){
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
            listener.onListLoaded(friendList);
    }
    public void getPosts(final OnGetListListener listener) {
        //get posts from database
        if(Posts==null) {
            Posts = new ArrayList<>();
            for (final String postId : this.getPostsIds()) {
                DatabaseReference locationRef = getDatabase().child(Constants.LocationsNode).child(postId);
                locationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserLocation location = new UserLocation();
                        location.setData(dataSnapshot.getValue(LocationData.class));
                        location.setUID(dataSnapshot.getKey());
                        int postInd = getPostIndex(location.getUID());
                        if (postInd != -1) {
                            Posts.set(postInd, location);
                            for (OnGetListListener listener : postListeners)
                                listener.onChildChange(Posts, postInd);
                        } else {
                            Posts.add(location);
                            for (OnGetListListener listener : postListeners)
                                listener.onChildAdded(Posts, postInd);
                        }
                        if (friendList.size() == getFriendsIds().size()) {
                            for (OnGetListListener listener : postListeners)
                                listener.onListLoaded(Posts);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
        else
            listener.onListLoaded(Posts);
    }
    //TODO: W: addPost and addFriend added new object to potentially non-initialize list which can cause error in future
    public void addPost(final UserLocation post)
    {
        if(post==null)
            return;
        Map<String, Object> PostIdsMap = new HashMap<>();
        getPostsIds().add(post.getUID());
        PostIdsMap.put(Constants.UserPostIdsAtt,getPostsIds());

        getDatabase().child(Constants.UsersNode).child(getUID()).updateChildren(PostIdsMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.WARN, "Database:", e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.println(Log.WARN, "Database: ", "Success");
                Posts.add(post);
            }
        });


    }
    public void removePost(final UserLocation post)//post argument is element of Posts list
    {
       getDatabase().child(Constants.LocationsNode).child(post.getUID())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getPostsIds().remove(Posts.indexOf(post));
                Save();
                Posts.remove(post);
            }
        });
    }
    //TODO: get some notification if friend added or not
    public void addFriend(String uid){
        if(getFriendIndex(uid)!=-1)
            return;
        data.FriendsIds.add(uid);
        getDatabase().child(Constants.UsersNode).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User();
                user.setData(dataSnapshot.getValue(UserData.class));
                user.setUID(dataSnapshot.getKey());
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
            getDatabase().child(Constants.UsersNode).child(getUID()).setValue(data).addOnFailureListener(new OnFailureListener() {
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
    public void updateLocation(){
        try {
            Map<String, Object> LocationValues = new HashMap<>();
            LocationValues.put(Constants.LocationLatitudeAtt,getLatitude());
            LocationValues.put(Constants.LocationLongitudeAtt,getLongitude());

            getDatabase().child(Constants.UsersNode).child(getUID()).updateChildren(LocationValues).addOnFailureListener(new OnFailureListener() {
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
    //TODO: return true can have better position
    public boolean updateCity(String City){
        if(getCity() != null)
        {
            if(getCity().equals(City))
            return false;
        }
        setCity(City);
        try {
            getDatabase().child(Constants.UsersNode).child(getUID()).child(Constants.LocationCityAtt).setValue(City).addOnFailureListener(new OnFailureListener() {
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
        return true;
    }


    public boolean UpdatePicture(final Uri ImageUri, final OnUploadDataListener listener ) {
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
