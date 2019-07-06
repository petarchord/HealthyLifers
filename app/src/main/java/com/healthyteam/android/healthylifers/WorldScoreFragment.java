package com.healthyteam.android.healthylifers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.OnGetListListener;
import com.healthyteam.android.healthylifers.Domain.User;

import java.util.List;

public class WorldScoreFragment extends Fragment {
    private View layout_fragment;
    private ListView listWorldScore;

    private static WorldScoreFragment instance;

    public static WorldScoreFragment getInstance(){
        if(instance==null)
            instance=new WorldScoreFragment();
        return instance;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout_fragment = inflater.inflate(R.layout.fragment_world_score,container,false);
        listWorldScore = (ListView) layout_fragment.findViewById(R.id.ListView_worldScore);

        DomainController.getWorldScoreUsersDB(new OnGetListListener() {
            @Override
            public void onSucces(List<?> list) {
                WorldScoreAdapter adapter = new WorldScoreAdapter();
                adapter.setWorldUser((List<User>) list);
                listWorldScore.setAdapter(adapter);
            }
        });


        return layout_fragment;
    }

    public class WorldScoreAdapter extends BaseAdapter {
        List<User> WorldUsers;

        public void setWorldUser(List<User> worldUsers) {
            WorldUsers = worldUsers;
        }

        @Override
        public int getCount() {
            //all user count
            return WorldUsers.size();
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
            view = inflater.inflate(R.layout.layout_score, null);
            TextView txtName = (TextView) view.findViewById(R.id.textView_NameSurnameScore);
            TextView txtUsername = (TextView) view.findViewById(R.id.textView_UsernameScore);
            TextView txtPoints = (TextView) view.findViewById(R.id.textView_PointsScore);

            //get user from database
            User friend = WorldUsers.get(i);
            String NameSurname = friend.getName() + " " + friend.getSurname();
            txtName.setText(NameSurname);
            txtUsername.setText(friend.getUsername());
            txtPoints.setText(friend.getPointsStirng());


            return view;
        }
    }
}
