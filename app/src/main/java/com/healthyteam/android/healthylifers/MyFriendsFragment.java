package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.healthyteam.android.healthylifers.Data.OnRunTaskListener;
import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.OnGetListListener;
import com.healthyteam.android.healthylifers.Domain.User;

import java.util.List;

public class MyFriendsFragment extends Fragment {
    private View fragment_layout;
    private ListView lvFriends;
    private FloatingActionButton fabAddFriend;
    private Dialog addFriendDialog;
    private Button dialogBtnOk;
    private Button dialogBtnCancel;
    private Dialog deleteFriendDialog;
    private Button dialogBtnYes;
    private Button dialogBtnNo;
    private static MyFriendsFragment instance;
    private MyFriendAdapter adapter;

    public static MyFriendsFragment getInstance(){
        if(instance==null)
            instance=new MyFriendsFragment();
        return instance;
    }
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragment_layout = inflater.inflate(R.layout.fragment_my_friends,container,false);
        lvFriends = fragment_layout.findViewById(R.id.ListView_Friends);
        fabAddFriend = fragment_layout.findViewById(R.id.floatingActionButton_addFriend);
        deleteFriendDialog = new Dialog(getContext());
        deleteFriendDialog.setContentView(R.layout.dialog_delete_friend);
        deleteFriendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBtnYes=deleteFriendDialog.findViewById(R.id.button_oksDAF);
        dialogBtnNo=deleteFriendDialog.findViewById(R.id.button_cancelDAF);
        addFriendDialog= new Dialog(getContext());
        addFriendDialog.setContentView(R.layout.dialog_add_friend);
        addFriendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBtnOk = addFriendDialog.findViewById(R.id.button_okDAF);
        dialogBtnCancel=addFriendDialog.findViewById(R.id.button_cancelDAF);

        dialogBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriendDialog.cancel();
            }
        });
        fabAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog.show();
            }
        });
        dialogBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog.cancel();
            }
        });
        dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog.cancel();
            }
        });
        adapter = new MyFriendAdapter();
        //TODO: test this. Check list initialisation
        DomainController.getUser().addGetFriendListener(new OnGetListListener() {
            @Override
            public void onChildAdded(List<?> list, int index) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChange(List<?> list, int index) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemove(List<?> list, int index,Object removedObject) {
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(List<?> list, int index) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onListLoaded(List<?> list) {
                adapter.setFriends((List<User>) list);
                lvFriends.setAdapter(adapter);
            }

            @Override
            public void onCanclled(DatabaseError error) {

            }
        });


        return fragment_layout;
    }

    public class MyFriendAdapter extends BaseAdapter {
        List<User> friends;
        public void setFriends(List<User> friends){
            this.friends=friends;
        }
        @Override
        public int getCount() {
            return friends.size();
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
            final int index =i;
            view = inflater.inflate(R.layout.layout_friend, null);
            TextView txtName = (TextView) view.findViewById(R.id.textView_NameSurname);
            TextView txtUsername = (TextView) view.findViewById(R.id.textView_Username);
            TextView txtPoints = (TextView) view.findViewById(R.id.textView_Points);
            Button btnDelete= view.findViewById(R.id.button_deleteFriend);
            ImageView imageProfile = (ImageView) view.findViewById(R.id.imageView_ProfilePic);

            imageProfile.setImageResource(R.drawable.profile_picture);
            final User friend = friends.get(i);
            String NameSurname = friend.getName() + " " + friend.getSurname();
            txtName.setText(NameSurname);
            txtUsername.setText(friend.getUsername());
            txtPoints.setText(friend.getPoints().toString());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendProfileFragment FriendProfile = new FriendProfileFragment();
                    FriendProfile.setFriend(friend);
                    ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,FriendProfile).commit();
                }
            });


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBtnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DomainController.getUser().deleteFriend(friend.getUID());
                            deleteFriendDialog.cancel();
                        }
                    });
                    deleteFriendDialog.show();
                }});
            //need to add image set
            //imageProfile.setimag;

            return view;
        }
    }
    //TODO: set Bluetooth items adapter
    public class BluetoothItemsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //get count from some android service
            return 0;
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
            //init bluetooth name and set onClick Listeners

            return view;
        }
    }
}
