package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

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
    private BluetoothAdapter mBlueAdapter;

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

        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();


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
        lvFriends.setAdapter(adapter);

        return fragment_layout;
    }

    public class MyFriendAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return DomainController.getUser().getFriendList().size();
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

            final User friend = DomainController.getUser().getFriendList().get(i);
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
                            DomainController.getUser().deleteFriend(friend);
                            adapter.notifyDataSetChanged();
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

            return mBlueAdapter.getBondedDevices().size();
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
            view = inflater.inflate(R.layout.layout_bluetooth_item,null);
            TextView bluetoothName = view.findViewById(R.id.TextView_BluetoothNameBI);
            ArrayList<BluetoothDevice> devices =(ArrayList<BluetoothDevice>) mBlueAdapter.getBondedDevices();
            BluetoothDevice device = devices.get(index);
            bluetoothName.setText(device.getName());


            //init bluetooth name and set onClick Listeners

            return view;
        }
    }
}
