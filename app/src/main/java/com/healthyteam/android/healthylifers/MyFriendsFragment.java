package com.healthyteam.android.healthylifers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.healthyteam.android.healthylifers.Domain.UserTest;

public class MyFriendsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_friends,container,false);
        ListView list = (ListView) view.findViewById(R.id.ListView_Friends);
        MyFriendAdapter adapter = new MyFriendAdapter();
        list.setAdapter(adapter);

        return view;
    }

    public class MyFriendAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return UserTest.getInstance().getFriendList().size();
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
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(R.layout.layout_friend, null);
            TextView txtName = (TextView) view.findViewById(R.id.textView_NameSurname);
            TextView txtUsername = (TextView) view.findViewById(R.id.textView_Username);
            TextView txtPoints = (TextView) view.findViewById(R.id.textView_Points);
            ImageView imageProfile = (ImageView) view.findViewById(R.id.imageView_ProfilePic);

            UserTest friend = UserTest.getInstance().getFriendList().get(i);
            String NameSurname = friend.getName() + " " + friend.getSurname();
            txtName.setText(NameSurname);
            txtUsername.setText(friend.getUsername());
            txtPoints.setText(friend.getPoints().toString());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //need to add image set
            //imageProfile.setimag;

            return view;
        }
    }
}
