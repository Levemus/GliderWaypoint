package com.levemus.gliderwaypoint.WifiDirect.Operations;

import android.util.Log;

import com.levemus.gliderwaypoint.WifiDirect.Messages.OpCodes.WifiDirectEvent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by markcarter on 16-01-04.
 */
public class WifiDirectReceiveFile implements IWifiDirectOperation {
    private final String TAG = this.getClass().getSimpleName();
    private final int BUFFER_SIZE = 4096;
    public final int PORT = 7950;

    @Override
    public WifiDirectEvent.Event perform(WifiDirectOperationConfig config, HashMap<String, String> params) {

        ServerSocket welcomeSocket = null;
        try {
            welcomeSocket = new ServerSocket(PORT);
            Socket socket = welcomeSocket.accept();

            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            // Get filelength
            long fileLength = dataInputStream.readLong();
            byte[] buffer = new byte[(int)fileLength];

            // read the data
            dataInputStream.read(buffer, 0, (int)fileLength);

            // write to file
            FileOutputStream fileOutputStream = new FileOutputStream(new File(params.get("filename")));
            fileOutputStream.write(buffer, 0, (int)fileLength);
            fileOutputStream.flush();

            // Close everything
            fileOutputStream.close();
            dataInputStream.close();

            socket.close();
            welcomeSocket.close();
        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e);
            return WifiDirectEvent.Event.OPERATION_FAIL;
        }

        return (WifiDirectEvent.Event.OPERATION_SUCCESS);
    }
}
