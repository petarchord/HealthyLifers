package com.healthyteam.android.healthylifers.Domain;

import android.graphics.Bitmap;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;



public class Location {
    enum Category {
        EVENT, HEALTHYFOOD, COURT, FITNESSCENTER
    }

    enum Rate {
        NONE, LIKE, DISLIKE
    }

    private Bitmap LocaitonPic;
    private String Name;
    private String Descripition;
    private String DateAdded;
    private List<String> TagList;
    private Category LocationCategory;
    private Integer likeCount;
    private Integer dislikeCount;
    private Rate UserRate;
    private Double lan;
    private Double lon;
    private List<Comment> Comments;
    //ako je neophodno da se dovlace svi objekti komentara iz baze, onda atribut ispod nije potreban
    private Integer commentCount;
    private User Author;

public Location(Bitmap locaitonPic, String name, String descripition, String dateAdded, Category category, int likeCount, int dislikeCount, Rate userRate, Double lan, Double lon, int commentCount,List<String> tagList, User author) {
        this.setLocaitonPic(locaitonPic);
        this.setName(name);
        this.setDescripition(descripition);
        this.setDateAdded(dateAdded);
        this.setCategory(category);
        this.setUserRate(userRate);
        this.setLikeCount(likeCount);
        this.setDislikeCount(dislikeCount);
        this.setLan(lan);
        this.setLon(lon);
        this.setCommentCount(commentCount);
        this.setAuthor(author);
        this.setTagList(tagList);
    }


    //region private funciton
    private String getDate() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c);
    }

    //endregion


    //region Methods
    public void addComment(String text, User user) {
        //update base
        getComments().add(new Comment(text, user));
    }

    public void removeComment(Comment comment) {
        //update base
        getComments().remove(comment);
    }

    public void likeThis() {
        if(this.UserRate==Rate.LIKE)
        return;
        if(this.UserRate== Rate.DISLIKE)
            dislikeCount--;
        likeCount++;
        this.UserRate=Rate.LIKE;
    }
    public void dislikeThis(){
        if(this.UserRate==Rate.DISLIKE)
        return;
        if(this.UserRate== Rate.LIKE)
            likeCount--;
        dislikeCount++;
        this.UserRate=Rate.DISLIKE;
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


    public Bitmap getLocaitonPic() {
        return LocaitonPic;
    }

    public String getName() {
        return Name;
    }

    public String getDescripition() {
        return Descripition;
    }

    public List<String> getTagList() {
        return TagList;
    }

    public Integer getLikeCount() {
        return likeCount;
    }
    public String getLikeCountString(){return likeCount.toString();}
    public Integer getDislikeCount() {
        return dislikeCount;
    }
    public String getDislikeCountString(){return dislikeCount.toString();}

    public Double getLan() {
        return lan;
    }

    public Double getLon() {
        return lon;
    }
    //TODO: return list of comments PersistanceControler
    public List<Comment> getComments() {
        this.Comments = new LinkedList<Comment>();

        //fetch from base {...}


        return Comments;
    }

    public String getDateAdded() {
        return DateAdded;
    }

    public Category getCategory() {
        return this.LocationCategory;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public User getAuthor() {
        return Author;
    }
    //endregion

    //region Setter

    public void setLocaitonPic(Bitmap locaitonPic) {
        LocaitonPic = locaitonPic;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDescripition(String descripition) {
        Descripition = descripition;
    }

    public void setDateAdded(String dateAdded) {
        DateAdded = dateAdded;
    }

    public void setTagList(List<String> tagList) {
        TagList = tagList;
    }

    public void setCategory(Category category) {
        this.LocationCategory = category;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount=likeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount=dislikeCount;
    }

    public void setUserRate(Rate rate) {
        this.UserRate = rate;
    }

    public void setLan(Double lan) {
        this.lan = lan;
    }

    public void setComments(List<Comment> comments) {
        Comments = comments;
    }

    public void setAuthor(User author) {
        Author = author;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
//endregion

}
