package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.User;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MyFriendsFragment extends Fragment {
    private View fragment_layout;
    private ListView lvFriends;
    private ListView lvAddFriends;
    private FloatingActionButton fabAddFriend;
    private Dialog addFriendDialog;
    private Button dialogBtnOk;
    private Button dialogBtnCancel;
    private Dialog deleteFriendDialog;
    private Button dialogBtnYes;
    private Button dialogBtnNo;
    private ImageButton exitAddFriendsDialog;
    private static MyFriendsFragment instance;
    private MyFriendAdapter adapter;
    private BluetoothItemsAdapter adapterAddFriends;
    private BluetoothAdapter mBlueAdapter;
    private BroadcastReceiver mReciever;
    private ArrayList<BluetoothDevice> devicesArray;
   // private BluetoothDevice[] btArray;
    private static final String APP_NAME = "HealthyLifers";
    private static final UUID MY_UUID = UUID.fromString("03866f41-7d1e-4d16-96bf-2c6ba69850e4");
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
        lvAddFriends = addFriendDialog.findViewById(R.id.ListView_BluetoothItems);
        exitAddFriendsDialog = addFriendDialog.findViewById(R.id.closeAddFriendDialog);
        /*dialogBtnOk = addFriendDialog.findViewById(R.id.button_okDAF);
        dialogBtnCancel=addFriendDialog.findViewById(R.id.button_cancelDAF);*/

        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesArray = new ArrayList<>();

        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            //    String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                devicesArray.add(device);
                showToast("Added device with name "+device.getName() +"and MAC address"+device.getAddress()+ "to list");

            }
        };


        if(!mBlueAdapter.isDiscovering())
        {
            mBlueAdapter.startDiscovery();

        }
        //registerReceiver(mReciever,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        getContext().registerReceiver(mReciever,new IntentFilter(BluetoothDevice.ACTION_FOUND));



        exitAddFriendsDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog.dismiss();
            }
        });

        dialogBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriendDialog.cancel();
            }
        });
        fabAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
                addFriendDialog.show();
            }
        });
        /*dialogBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog.cancel();
            }
        });*/
     /*   dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog.cancel();
            }
        });*/
        adapter = new MyFriendAdapter();
        adapterAddFriends = new BluetoothItemsAdapter();

        lvFriends.setAdapter(adapter);
        lvAddFriends.setAdapter(adapterAddFriends);

        return fragment_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReciever);
        mBlueAdapter.cancelDiscovery();
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

            return devicesArray.size();
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
            final BluetoothDevice dev = devicesArray.get(index);
            bluetoothName.setText(dev.getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("Cliked on"+dev.getName());
                    ClientClass clientClass = new ClientClass(dev);
                    clientClass.start();
                    showToast("Connecting");

                }
            });


            //init bluetooth name and set onClick Listeners

            return view;
        }
    }


    private void showToast(String msg)
    {
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
    }


    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass()
        {
            try {
                serverSocket = mBlueAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket = null;

            while (socket == null)
            {
                try {
                    socket = serverSocket.accept();
                    showToast("CONNECTING...");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(socket != null)
                {
                    //write some code for transmiting the data
                    showToast("SERVER : STATE_CONNECTED");
                    break;
                }
            }
        }
    }



    private class ClientClass extends Thread
    {
        BluetoothDevice device;
        BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1)
        {
            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            mBlueAdapter.cancelDiscovery();
            try {
                socket.connect();
                showToast("CLIENT : STATE CONNECTED.");
            } catch (IOException e) {
                showToast("CLIENT : STATE CONNECTION FAILED");
                e.printStackTrace();
            }
        }

    }
}
