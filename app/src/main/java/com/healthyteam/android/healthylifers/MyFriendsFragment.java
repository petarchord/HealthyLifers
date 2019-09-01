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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
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


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.healthyteam.android.healthylifers.Data.OnRunTaskListener;
import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.OnGetListListener;
import com.healthyteam.android.healthylifers.Domain.User;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
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
    private Dialog friendRequestDialog;
    private Button dialogBtnYes;
    private Button dialogBtnNo;
    private Button acceptFriend;
    private Button rejectFriend;
    private ImageButton exitAddFriendsDialog;
    private static MyFriendsFragment instance;
    private MyFriendAdapter adapter;
    OnGetListListener listListener;


    private BluetoothItemsAdapter adapterAddFriends;
    private BluetoothAdapter mBlueAdapter;
    private BroadcastReceiver mReciever;
    private ArrayList<BluetoothDevice> devicesArray;
    private String friendToAdd;
   // private BluetoothDevice[] btArray;
    private static final String APP_NAME = "HealthyLifers";
    private static final UUID MY_UUID = UUID.fromString("03866f41-7d1e-4d16-96bf-2c6ba69850e4");
    private ConnectionThread mBlutetoothConnection;
    public static MyFriendsFragment getInstance(){
        if(instance==null)
            instance=new MyFriendsFragment();
        return instance;
    }
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(fragment_layout==null)
            initialize(inflater,container);
        getContext().registerReceiver(mReciever,new IntentFilter(BluetoothDevice.ACTION_FOUND));

        return fragment_layout;
    }
    void initialize(LayoutInflater inflater,ViewGroup container){
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
        friendRequestDialog = new Dialog(getContext());
        friendRequestDialog.setContentView(R.layout.dialog_friend_request);
        friendRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        acceptFriend = friendRequestDialog.findViewById(R.id.button_accept_friend);
        rejectFriend = friendRequestDialog.findViewById(R.id.button_reject_friend);
        /*dialogBtnOk = addFriendDialog.findViewById(R.id.button_okDAF);
        dialogBtnCancel=addFriendDialog.findViewById(R.id.button_cancelDAF);*/

      mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
      devicesArray = new ArrayList<>();

        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            //    String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                String action = intent.getAction();
                boolean duplicate = false;
                if(BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    for(int i=0;i<devicesArray.size();i++)
                    {
                        if(device.getAddress() == devicesArray.get(i).getAddress())
                        {
                            duplicate= true;
                        }
                    }
                    if(!duplicate)
                    {
                        devicesArray.add(device);
                        showToast("Added device with name "+device.getName() +"and MAC address"+device.getAddress()+ "to list");
                        adapterAddFriends.notifyDataSetChanged();

                    }

                }


            }
        };

        Set<BluetoothDevice> pairedDevices = mBlueAdapter.getBondedDevices();
        boolean duplicate = false;

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.

            for (BluetoothDevice device : pairedDevices) {
                for(int i=0;i<devicesArray.size();i++)
                {
                    if(device.getAddress() == devicesArray.get(i).getAddress())
                        duplicate=true;

                }
                if(!duplicate)
                devicesArray.add(device);
            }
        }
        mBlueAdapter.startDiscovery();




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
             //   addFriendDialog.show();

                ServerClass serverClass = new ServerClass();
                serverClass.start();
                addFriendDialog.show();
            }
        });

        acceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendToAdd != null)
                {
                    DomainController.getUser().addFriend(friendToAdd);
                    showToast("Friend accepted.");
                    friendRequestDialog.dismiss();
                    addFriendDialog.dismiss();
                }
            }
        });

        rejectFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToast("Friend rejected.");
                friendRequestDialog.dismiss();
                addFriendDialog.dismiss();

            }
        });
        /*dialogBtnOk.setOnClickListener(new View.OnClickListener() {
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
        });*/
        adapterAddFriends = new BluetoothItemsAdapter();


        adapter = new MyFriendAdapter();
        //TODO check if adapter friend list is the same list as user's list
        listListener =new OnGetListListener() {
            @Override
            public void onChildAdded(List<?> list, int index) {
                if(lvFriends.getAdapter()==null) {
                    adapter.setFriends((List<User>) list);
                    lvFriends.setAdapter(adapter);
                }
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
                if(lvFriends.getAdapter()==null) {
                    adapter.setFriends((List<User>) list);
                    lvFriends.setAdapter(adapter);
                }
            }

            @Override
            public void onCanclled(DatabaseError error) {

            }};
        DomainController.getUser().addGetFriendListener(listListener);

        lvAddFriends.setAdapter(adapterAddFriends);

    }


    public Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                {
                    mBlutetoothConnection = (ConnectionThread)msg.obj;
                    String uid= DomainController.getUser().getUID();
                    mBlutetoothConnection.write(uid.getBytes());
                    break;
                }
                case 1:
                {

                    String message=new String((byte[])msg.obj);
                    friendToAdd = message;
                    friendRequestDialog.show();
                 //   DomainController.getUser().addFriend(message);
                 //   showToast(message);
                    break;
                }
                case 2:
                {
                    break;
                }
                default:
                    break;
            }
        }
    };




    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReciever);
        ServerClass sc = new ServerClass();
        sc.cancel();
        mBlueAdapter.cancelDiscovery();
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



            final User friend = friends.get(i);
            if(friend.getImageUrl()!=null) {
                Picasso.get().load(friend.getImageUrl()).into(imageProfile);
            }
            else
                imageProfile.setImageResource(R.drawable.profile_picture);
            String NameSurname = friend.getName() + " " + friend.getSurname();
            txtName.setText(NameSurname);
            txtUsername.setText(friend.getUsername());
            txtPoints.setText(friend.getPoints().toString());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendProfileFragment FriendProfile = new FriendProfileFragment();
                    FriendProfile.setFriend(friend);
                    FriendProfile.setPerent(MyFriendsFragment.getInstance());
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
                Log.e("SERVER", "Socket's listen() method failed", e);
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket = null;

            while (true)
            {
                try {
                    socket = serverSocket.accept();
                  //  showToast("SERVER: CONNECTING...");
                    Log.e("SERVER", "SERVER: CONNECTING...");
                } catch (IOException e) {
                   // showToast( "Socket's accept() method failed" +e);
                    Log.e("SERVER", "Socket's accept() method failed",e);
                    e.printStackTrace();
                }

                if(socket != null)
                {
                    //write some code for transmiting the data
                  //  showToast("SERVER : STATE_CONNECTED");
                    Log.e("SERVER", "SERVER : STATE_CONNECTED");
                    manageMyConnectedSocket(socket);
                    break;
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e("SERVER", "Could not close the connect socket", e);
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
              //  showToast( "CLIENT: Socket's create() method failed"+e);
                Log.e("CLIENT", "Socket's create() method failed",e);
                e.printStackTrace();
            }
        }

        public void run()
        {
            mBlueAdapter.cancelDiscovery();
            try {
                socket.connect();
                Log.e("CLIENT", "STATE CONNECTED.");
              //  showToast("CLIENT : STATE CONNECTED.");
            } catch (IOException e) {
              //  showToast("CLIENT : STATE CONNECTION FAILED");
                Log.e("CLIENT", "STATE CONNECTION FAILED.",e);
                e.printStackTrace();

                try
                {
                    socket.close();
                }
                catch (IOException closeException)
                {
                    Log.e("CLIENT", "Could not close the client socket", closeException);
                }
                return;

            }

            manageMyConnectedSocket(socket);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("CLIENT", "Could not close the client socket", e);
            }
        }

    }


    public class ConnectionThread extends Thread
    {
        BluetoothSocket mBluetoothSocket;
        Handler mHandler;
        InputStream mmInStream;
        OutputStream mmOutStream;
        byte[] mmBufer;

        public ConnectionThread(BluetoothSocket s,Handler h)
        {
            super();
            mBluetoothSocket=s;
            mHandler=h;
            try
            {
                mmInStream = mBluetoothSocket.getInputStream();
                mmOutStream = mBluetoothSocket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.e("CONNECTION THREAD", "Error occurred while creating the streams", e);

            }

        }

        public void run()
        {
            mmBufer = new byte[1024];
            int numBytes;
            while(true)
            {
                try
                {
                    numBytes = mmInStream.read(mmBufer);
                    Message readMsg = mHandler.obtainMessage(1,numBytes,-1,mmBufer);
                    readMsg.sendToTarget();
                    Log.e("CONNECTION THREAD", "Buffer filled with contain from the input stream ");
                }
                catch (IOException e)
                {
                    Log.e("CONNECTION THREAD", "Input Stream was disconnected", e);

                }

            }


        }

        public void write(byte[] bytes)
        {
            try
            {
                mmOutStream.write(bytes);
            //    Message writtenMsg = mHandler.obtainMessage(2,-1,-1,mmBufer);
           //     writtenMsg.sendToTarget();
            }
            catch (IOException e)
            {
                Log.e("CONNECTION THREAD", "Error occurred when sending data", e);
            }


        }

        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                Log.e("CONNECTION THREAD", "Could not close the connect socket", e);
            }
        }

    }


    private void manageMyConnectedSocket(BluetoothSocket s)
    {
       // Log.e("Server-Client","connected socket:"+s);
        ConnectionThread conn = new ConnectionThread(s,mHandler);
        mHandler.obtainMessage(0,conn).sendToTarget();
        conn.start();

    }
}
