package com.levemus.gliderwaypoint.WifiDirect;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.levemus.gliderwaypoint.Managers.IClient;
import com.levemus.gliderwaypoint.Messages.IMessage;
import com.levemus.gliderwaypoint.Messages.SerializablePayloadMessage;
import com.levemus.gliderwaypoint.WifiDirect.Messages.OpCodes.WifiDirectEvent;
import com.levemus.gliderwaypoint.WifiDirect.Messages.OpCodes.WifiDirectRequest;
import com.levemus.gliderwaypoint.WifiDirect.Messages.WifiDirectEventMessage;
import com.levemus.gliderwaypoint.WifiDirect.Messages.WifiDirectRequestMessage;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by markcarter on 16-01-04.
 */
public class WifiDirectManager implements IClient {
    private final String TAG = this.getClass().getSimpleName();
    private WifiDirectService mService = new WifiDirectService();
    private Handler mWifiDirectTimeoutHandler = new Handler();
    private final int WIFIDIRECT_TIMEOUT = 1000 * 60 * 5; // mins in ms
    private boolean mWifiEnabled = false;
    private boolean mIsServer = true;
    private String mServerName = "Android_a0b9"; // TODO: make HUD activity to select this and pass through.
    private String mClientName = "Android_6fd1";// TODO: make Client activity to select this and pass through.
    private IClient mClient;
    private Context mContext;

    public void start(final Activity activity) {
        mContext = (Context)activity;

        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        mWifiEnabled = wifiManager.isWifiEnabled();
        if(!mWifiEnabled && mIsServer)
            wifiManager.setWifiEnabled(true);

        mService.registerClient(this);
        mService.start(activity, WifiDirectService.class, id());
        if(mIsServer) {
            Runnable timeoutRunner = new Runnable() {
                public void run() {
                    stop(activity);
                }
            };
            mWifiDirectTimeoutHandler.postDelayed(timeoutRunner, WIFIDIRECT_TIMEOUT);
        }
    }

    public void stop(Activity activity) {
        mContext = (Context)activity;
        mWifiDirectTimeoutHandler.removeCallbacksAndMessages(null);
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        if(!mWifiEnabled && mIsServer) {
            Toast.makeText(mContext, "Turnpoint file service shutting down.", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(false);
        }
        mService.deRegisterClient(this);
        mService.stop(activity, WifiDirectService.class);
    }

    public void pause(Activity activity) {mContext = (Context)activity;}
    public void resume(Activity activity) {mContext = (Context)activity;}

    public void sendRequest(SerializablePayloadMessage msg) { mService.sendRequest(msg); }

    public UUID id() {
        return UUID.fromString("52cdc5d3-5aee-4285-aa17-4687f107804f");
    }

    @Override
    public void onMsg(IMessage msg) {
        if(msg instanceof WifiDirectEventMessage) {
            WifiDirectEventMessage eventMessage = (WifiDirectEventMessage)msg;
            Log.d(TAG, "WifiDirectEventMessage: " + eventMessage);
            if(eventMessage.opCode() == WifiDirectEvent.Event.PEER_ONLINE) {
                final String name = eventMessage.get("name");
                final String address = eventMessage.get("address");
                Log.d(TAG, "Discovered: " + name + "[" + address + "]");
                if(name != null && !name.isEmpty()
                        && name.compareTo((!mIsServer ? mServerName : mClientName)) == 0) {
                    HashMap<String, String> operationParams = new HashMap<String, String>(){{
                        put("name", name);
                        put("address", address);
                    }};
                    Log.d(TAG, "Requesting Connection with: " + name + "[" + address + "]");
                    sendRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.CONNECT, operationParams));
                }
            }
            else if(eventMessage.opCode() == WifiDirectEvent.Event.CONNECTED) {
                Log.d(TAG, "Connected, Transfering Data.");
                HashMap<String, String> operationParams = new HashMap<String, String>(){{
                    put("filename", "waypoints.txt");
                }};
                if(mIsServer)
                    sendRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.RX_DATA, operationParams));
                else
                    sendRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.TX_DATA, operationParams));
            }
            else if(eventMessage.opCode() == WifiDirectEvent.Event.CONNECTING) {
                Log.d(TAG, "Requesting Connection Info.");
                sendRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.CONNECTION_INFO));
            }
            else if(eventMessage.opCode() == WifiDirectEvent.Event.DISCONNECTED ||
                    eventMessage.opCode() == WifiDirectEvent.Event.AVAILABLE) {
                Log.d(TAG, "Discovering Peers.");
                sendRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.DISCOVER_PEERS));
            }
            else if(eventMessage.opCode() == WifiDirectEvent.Event.OPERATION_FAIL) {
                Log.d(TAG, "Transfer failed.");
                Toast.makeText(mContext, "Turnpoint upload failed.", Toast.LENGTH_SHORT).show();
                sendRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.DISCONNECT));
            }
            else if(eventMessage.opCode() == WifiDirectEvent.Event.OPERATION_SUCCESS) {
                Log.d(TAG, "Transfer complete.");
                ((Activity)mContext).runOnUiThread(
                        new Runnable() {
                            public void run() {
                                Toast.makeText(mContext, "Turnpoint upload success.", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                sendRequest(new WifiDirectRequestMessage(WifiDirectRequest.Request.DISCONNECT));
            }
        }
    }

    public void setIsServer(boolean isServer) {mIsServer = isServer;}
}
