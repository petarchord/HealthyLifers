package com.healthyteam.android.healthylifers.Domain;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.healthyteam.android.healthylifers.Data.CommentData;
import com.healthyteam.android.healthylifers.Data.LocationData;
import com.healthyteam.android.healthylifers.Data.OnUploadDataListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class UserLocation {
    public static enum Category {
        EVENT, HEALTHYFOOD, COURT, FITNESSCENTER
    }

    public static enum Rate {
        NONE, LIKE, DISLIKE

    }
    private LocationData Data;
    private List<Comment> Comments;
    //ako je neophodno da se dovlace svi objekti komentara iz baze, onda atribut ispod nije potreban
    private Rate UserRate;
    private User Author;

    public UserLocation(){
        Data=new LocationData();
        Comments = new LinkedList<>();
    }

    //region private funciton
    private String getDate() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c);
    }
    private int getStringIndex(String str, List<String> strList){
        for(int i=0;i<strList.size();i++) {
            if (strList.get(i).equals(str))
                return i;
        }
        return -1;
    }
    public int getCommentIndex(String uid){
        if(Comments==null)
            return  -1;
        for(int i=0; i<Comments.size();i++){
            if(Comments.get(i).getUID().equals(uid))
                return i;
        }
        return -1;
    }
    //endregion



    //region Methods

    public void addComment(String text, User user) {
        final Comment comment = new Comment();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference(Constants.CommentsNode).push().getKey();
        comment.setUID(key);
        comment.setText(text);
        comment.setCreator(user);
        comment.Save(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Data.CommentsIds.add(comment.getUID());
                Comments.add(comment);
                Save(null);
            }
        });
    }

    public void removeComment(final Comment comment) //Comment comment arg is element of Comments list
    {
        FirebaseDatabase.getInstance().getReference().child(Constants.CommentsNode).child(comment.getUID())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getCommentsIds().remove(Comments.indexOf(comment));
                Save(null);
                Comments.remove(comment);

            }
        });
        Comments.remove(comment);
    }

    public void likeThis(String byUserUID) {
        if(this.getUserRate(byUserUID)==Rate.LIKE)
            return;
        int ind = getStringIndex(byUserUID,Data.DislikedBy);
        if(ind > -1)
            Data.DislikedBy.remove(ind);
        this.UserRate=Rate.LIKE;
        Data.likedBy.add(byUserUID);

        Save(null);
    }
    public void dislikeThis(String byUserUID){
        if(this.getUserRate(byUserUID)==Rate.DISLIKE)
            return;
        int ind = getStringIndex(byUserUID,Data.likedBy);
        if(ind > -1)
            Data.likedBy.remove(ind);
        this.UserRate=Rate.DISLIKE;
        Data.DislikedBy.add(byUserUID);
        Save(null);
    }
    public Boolean isLiked(){
        if(this.UserRate == Rate.LIKE)
            return true;
        return false;
    }
    public Boolean isDisliked(){
        if(this.UserRate==Rate.DISLIKE)
            return  true;
        return false;
    }
    //endregion

    //region Getter


    public String getImageUrl() {
        return Data.imageURL;
    }

    public String getUID(){
        return  Data.UID;
    }
    public String getName() {
        return Data.Name;
    }

    public String getDescripition() {
        return Data.Description;
    }

    public List<String> getTagList() {
        return Data.Tags;
    }
    public String getTagsString(){
        String result="";
        int i;
        for(i=0;i<getTagList().size()-1;i++){
            result += getTagList().get(i)+ ", ";
        }
            result+=getTagList().get(i);
        return result;
    }

    public Integer getLikeCount() {
        return Data.likedBy.size();
    }

    public String getLikeCountString(){return String.valueOf(Data.likedBy.size());}
    public Integer getDislikeCount() {
        return Data.DislikedBy.size();
    }
    public String getDislikeCountString(){return String.valueOf(Data.DislikedBy.size());}

    public Double getLan() {
        return Data.Latitude;
    }

    public Double getLon() {
        return Data.Longitude;
    }


    public String getDateAdded() {
        return Data.DateAdded;
    }

    public Category getCategory() {
        return Category.values()[Data.Category];
    }


    public Rate getUserRate(String uid){
        if(UserRate!=null)
            return UserRate;
        UserRate= Rate.NONE;
        if(getStringIndex(uid,Data.likedBy)!=-1)
            UserRate = Rate.LIKE;
        if(getStringIndex(uid,Data.DislikedBy)!=-1)
            UserRate = Rate.DISLIKE;
        return UserRate;
    }

    public List<String> getCommentsIds(){
        return  Data.CommentsIds;
    }
    public Integer getCommentCount() {
        return Data.CommentsIds.size();
    }

    public String getAuthorUID(){
        return Data.AuthorUID;
    }


    //endregion

    //region Setter
    public void setUID(String uid) {
        Data.UID=uid;
    }
    public void setData(LocationData data){
        this.Data=data;

    }
    public void setLocaitonPic(String picURL) {
        Data.imageURL = picURL;
    }

    public void setName(String name) {
        Data.Name = name;
    }

    public void setDescripition(String descripition) {
        Data.Description = descripition;
    }

    public void setDateAdded(String dateAdded) {
        Data.DateAdded = dateAdded;
    }

    public void setTagList(List<String> tagList) {
        Data.Tags = tagList;
    }
    public void setTagListFromString(String Tags){
        this.setTagList(new LinkedList<String>());
        String[] splitTags = Tags.split("#");
        for(int i=1;i<splitTags.length;i++){
            this.getTagList().add(splitTags[i]);
        }

    }
    public void setCategory(Integer category) {
        Data.Category = category;
    }
    public void setCategory(Category category){
        Data.Category = category.ordinal();
    }

    public void setUserRate(Rate rate) {
        UserRate = rate;
    }

    public void setLat(Double lat) {
        Data.Latitude = lat;
    }
    public void setLon(Double lon) {
        Data.Longitude = lon;
    }
    public void setCity(String City){
        this.Data.City=City;
    }
    public void setComments(List<Comment> comments) {
        Comments = comments;
    }
    public void setAuthor(User author) {
        Author = author;
        Data.AuthorUID=author.getUID();
    }

//endregion

    //region dbFunction

    //READ
    private List<OnGetObjectListener> authorListners;
    public void addGetAuthorListener(OnGetObjectListener listener){
        if(authorListners ==null)
            authorListners = new LinkedList<>();
        if(!authorListners.contains(listener))
            authorListners.add(listener);
        getAuthor();
    }
    private void getAuthor() {
        if(Author==null) {
            Author = new User();
            User.getUser(getUID(), new OnGetObjectListener() {
                @Override
                public void OnSuccess(Object o) {
                    Author = (User) o;
                    for (OnGetObjectListener listener : authorListners)
                        listener.OnSuccess(Author);
                }
            });


        }
        else
            for (OnGetObjectListener listener : authorListners)
                listener.OnSuccess(Author);
    }
    private List<OnGetListListener> commentsListener;
    public void addGetCommentsListener(OnGetListListener listener){
        if(commentsListener ==null)
            commentsListener = new LinkedList<>();
        if(!authorListners.contains(listener))
            commentsListener.add(listener);
        getComments(listener);
    }
    private void getComments(OnGetListListener listener)
    {
        if(Comments==null) {
            Comments = new ArrayList<>();
            for (final String commentId : this.getCommentsIds()) {
                DatabaseReference CommentRef = getDatabase().child(Constants.LocationsNode).child(commentId);
                CommentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Comment comment = new Comment();
                        comment.setData(dataSnapshot.getValue(CommentData.class));
                        comment.setUID(dataSnapshot.getKey());
                        int commentInd = getCommentIndex(comment.getUID());
                        if (commentInd != -1) {
                            Comments.set(commentInd, comment);
                            for (OnGetListListener listener : commentsListener)
                                listener.onChildChange(Comments, commentInd);
                        } else {
                            Comments.add(comment);
                            for (OnGetListListener listener : commentsListener)
                                listener.onChildAdded(Comments, commentInd);
                        }
                        if (Comments.size() == getCommentCount()) {
                            for (OnGetListListener listener : commentsListener)
                                listener.onListLoaded(Comments);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
        else
            listener.onListLoaded(Comments);
    }



    //WRITE
    public void Save(final OnSuccessListener listener){
        if(getUID()==null){
            String key =getDatabase().child(Constants.LocationsNode).push().getKey();
            setUID(key);
        }
        try {
            getDatabase().child(Constants.LocationsNode).child(getUID()).setValue(Data).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.println(Log.WARN, "Database:", e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.println(Log.WARN, "Database: ", "Success");
                    if(listener!=null)
                        listener.onSuccess(aVoid);
                }
            });
        }
        catch(Exception e){
            Log.println(Log.ERROR, "Database:", e.getMessage());
        }
    }
    public boolean UpdatePicture(final Uri ImageUri, final OnUploadDataListener listener) {
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
                                        setLocaitonPic(uri.toString());
                                        Save(null);
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
    //UPDATE


    //DELETE


    //endregion

    private static DatabaseReference mDatabase;
    private static DatabaseReference getDatabase() {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase;
    }

}
