package com.healthyteam.android.healthylifers.Domain;

public  class Comment {
    private String Text;
    private int LikeCount;
    private int DislikeCount;
    private User Creator;

    public Comment(String Text, User Creator) {
        this.Text = Text;
        this.Creator = Creator;
    }
}
