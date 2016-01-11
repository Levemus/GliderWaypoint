package com.levemus.gliderwaypoint.WifiDirect.Messages;

import com.levemus.gliderwaypoint.WifiDirect.Messages.OpCodes.WifiDirectEvent;

import java.util.HashMap;

public class WifiDirectEventMessage extends WifiDirectMessage<WifiDirectEvent.Event> {

    public WifiDirectEventMessage(WifiDirectEvent.Event event) {
        super(event, new HashMap<String, String>());
    }

    public WifiDirectEventMessage(WifiDirectEvent.Event event, HashMap<String, String> data) {
        super(event, data);
    }

    @Override
    public String toString() {
        return "WifiDirectEventMessage [opCode=" + mOpCode + "]";
    }

}
