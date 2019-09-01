/* package com.healthyteam.android.healthylifers;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class BluetoothServerClass extends Thread
{
    private BluetoothServerSocket serverSocket;

    public BluetoothServerClass()
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
*/