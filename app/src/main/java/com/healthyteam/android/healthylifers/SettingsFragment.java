package com.healthyteam.android.healthylifers;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private SwitchCompat bluetooth;
    private View view;
    BluetoothAdapter mBlueAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings,container,false);
        bluetooth = view.findViewById(R.id.bluetoothSwitch);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    if(!mBlueAdapter.isEnabled())
                    {
                        Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent,REQUEST_ENABLE_BT);

                    }
                    if(!mBlueAdapter.isDiscovering())
                    {
                       /* Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivity(intent); */
                        mBlueAdapter.startDiscovery();

                    }

                }
                else
                {
                       if(mBlueAdapter.isEnabled())
                    {
                        mBlueAdapter.disable();
                        showToast("Bluetooth turned off");
                    }
                    if(mBlueAdapter.isDiscovering())
                    {
                        mBlueAdapter.cancelDiscovery();
                    }


                }

            }
        });

        if(mBlueAdapter == null)
        {
            showToast("Bluetooth is not avalible on this device");
        }
        else
        {
            showToast("Bluetooth is avalible on this device");
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK)
                {
                    mBlueAdapter.enable();
                    showToast("Bluetooth turned on");
                }
                else
                {
                    showToast("User declined to turn on bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String msg)
    {
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
    }
}
