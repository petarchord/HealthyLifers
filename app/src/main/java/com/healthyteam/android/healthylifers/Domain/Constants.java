package com.healthyteam.android.healthylifers.Domain;

public class Constants {
    public static final String UsersNode ="users";
    public static final String LocationsNode ="locations";
    public static final String CommentsNode= "comments";
    public static final String PictureNode = "picture";

    public static final String UserPointsAtt = "Points";
    public static final String UserFriendIdsAtt = "FriendIds";
    public static final String UserActiveAtt="Active";
    public static final String UserPostIdsAtt="PostsIds";
    public static final String UserCityAtt="City";

    public static final String LocationCategoryAtt="Category";
    public static final String LocationLatitudeAtt="Latitude";
    public static final String LocationLongitudeAtt ="Longitude";
    public static final String LocationCityAtt = "City";
    public static final Integer showUserCount = 30;
    public static final Integer LocationCategoryEvent = UserLocation.Category.EVENT.ordinal();
    public static final Integer LocationCategoryHealthyFood = UserLocation.Category.HEALTHYFOOD.ordinal();
    public static final Integer LocationCategoryCourt= UserLocation.Category.COURT.ordinal();
    public static final Integer LocationCategoryFitnessCenter= UserLocation.Category.FITNESSCENTER.ordinal();
}