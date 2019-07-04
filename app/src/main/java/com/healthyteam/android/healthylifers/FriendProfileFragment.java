package com.healthyteam.android.healthylifers;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.healthyteam.android.healthylifers.Domain.Location;
import com.healthyteam.android.healthylifers.Domain.User;

public class FriendProfileFragment extends Fragment {
    private User friend;
    private View fragment_layout;
    private TextView txtFriendNameSurname;
    private TextView txtFriendUsername;
    private TextView txtFriendPoints;
    private ImageView imgFriendPic;
    private ListView lvPosts;
    private ImageButton btnExit;

    public void setFriend(User u){
        this.friend=u;
    }
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment_layout = inflater.inflate(R.layout.fragment_friend_profile,container,false);
        txtFriendNameSurname=fragment_layout.findViewById(R.id.TextView_FriendNameSurnameFP);
        txtFriendUsername = fragment_layout.findViewById(R.id.TextView_FriendUsernameFP);
        txtFriendPoints = fragment_layout.findViewById(R.id.TextView_FriendPointsFP);
        imgFriendPic= fragment_layout.findViewById(R.id.imageView_ProfilePicFP);
        btnExit=fragment_layout.findViewById(R.id.btnExitFP);
        lvPosts = (ListView) fragment_layout.findViewById(R.id.ListView_PostFP);

        imgFriendPic.setImageResource(R.drawable.profile_picture);
        String NameSurname= friend.getName() + " " + friend.getSurname();
        txtFriendNameSurname.setText(NameSurname);
        txtFriendUsername.setText(friend.getUsername());
        txtFriendPoints.setText(friend.getPointsStirng());

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,MyFriendsFragment.getInstance()).commit();
            }
        });
        FriendProfileFragment.PlaceItemAdapter adapter = new FriendProfileFragment.PlaceItemAdapter();
        lvPosts.setAdapter(adapter);

        return fragment_layout;
    }

    public class PlaceItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return friend.getPostsCount();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(R.layout.layout_place_item, null);
            TextView txtPlaceName = (TextView) view.findViewById(R.id.textView_PlaceNamePI);
            TextView txtDate = (TextView) view.findViewById(R.id.textView_DatePI);
            TextView txtCommentsCount = (TextView) view.findViewById(R.id.TextView_commentFI);
            final TextView txtLikeCount = (TextView) view.findViewById(R.id.TextView_likeNumFI);
            final TextView txtDislikeCount = (TextView)  view.findViewById(R.id.TextView_dislikeNumFI);
            final ImageView likeImgView =view.findViewById(R.id.imageView_LikePicFI);
            final ImageView dislikeImgView = view.findViewById(R.id.imageView_dislikePicFI);
            ImageView imageProfile = (ImageView) view.findViewById(R.id.imageView_PlacePicPI);
            final Location currlocation = friend.getPosts().get(i);


            //set picture
            imageProfile.setImageResource(R.drawable.location_clipart);
            txtPlaceName.setText(currlocation.getName());
            String dateString = getString(R.string.dateLabel)+" "+currlocation.getDateAdded();
            txtDate.setText(dateString);
            String commentsCountString = getString(R.string.CommentLabel) + getString(R.string.leftpar) + currlocation.getCommentCount()+getString(R.string.rightpar);
            txtCommentsCount.setText(commentsCountString);

            checkLikeImg(likeImgView,currlocation.isLiked());
            txtLikeCount.setText(currlocation.getLikeCountString());
            checkDislikeImg(dislikeImgView,currlocation.isDisliked());
            txtDislikeCount.setText(currlocation.getDislikeCountString());

            likeImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currlocation.likeThis();
                    checkLikeImg(likeImgView,currlocation.isLiked());
                    checkDislikeImg(dislikeImgView,currlocation.isDisliked());
                    txtLikeCount.setText(currlocation.getLikeCountString());
                    txtDislikeCount.setText(currlocation.getDislikeCountString());
                }
            });

            dislikeImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currlocation.dislikeThis();
                    checkDislikeImg(dislikeImgView,currlocation.isDisliked());
                    checkLikeImg(likeImgView,currlocation.isLiked());
                    txtLikeCount.setText(currlocation.getLikeCountString());
                    txtDislikeCount.setText(currlocation.getDislikeCountString());
                }
            });
/*            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //initialize and start Location dialog box
                }
            });*/

            return view;
        }
        private void checkLikeImg(ImageView imgLike,boolean isLiked){
            if(isLiked)
                imgLike.setImageResource(R.drawable.baseline_thumb_up_24_green);
            else
                imgLike.setImageResource(R.drawable.baseline_thumb_up_24);
        }
        private void checkDislikeImg(ImageView imgDislike,boolean isDisliked){
            if(isDisliked)
                imgDislike.setImageResource(R.drawable.baseline_thumb_down_24_red);
            else
                imgDislike.setImageResource(R.drawable.baseline_thumb_down_24);
        }

    }
}

