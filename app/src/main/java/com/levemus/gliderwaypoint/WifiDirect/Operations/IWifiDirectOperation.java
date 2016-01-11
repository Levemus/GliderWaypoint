package com.levemus.gliderwaypoint.WifiDirect.Operations;


import com.levemus.gliderwaypoint.WifiDirect.Messages.OpCodes.WifiDirectEvent;

import java.util.HashMap;

/**
 * Created by markcarter on 16-01-05.
 */
public interface IWifiDirectOperation {
    WifiDirectEvent.Event perform(WifiDirectOperationConfig config, HashMap<String, String> params);
}
