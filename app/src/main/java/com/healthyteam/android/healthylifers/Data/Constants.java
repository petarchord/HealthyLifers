package com.healthyteam.android.healthylifers.Data;

import com.healthyteam.android.healthylifers.Domain.Location;

public class Constants {
    public static final String UsersNode ="users";
    public static final String LocationsNode ="locations";
    public static final String CommentsNode= "comments";
    public static final String PictureNode = "picture";

    public static final String UserPointsAtt = "Points";
    public static final String UserFriendIdsAtt = "FriendIds";
    public static final String LocationCategoryAtt="Category";

    public static final Integer showUserCount = 30;
    public static final Integer LocationCategoryEvent = Location.Category.EVENT.ordinal();
    public static final Integer LocationCategoryHealthyFood =Location.Category.HEALTHYFOOD.ordinal();
    public static final Integer LocationCategoryCourt=Location.Category.COURT.ordinal();
    public static final Integer LocationCategoryFitnessCenter=Location.Category.FITNESSCENTER.ordinal();
}