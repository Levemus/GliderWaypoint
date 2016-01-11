package com.levemus.gliderwaypoint.WifiDirect.Messages;

import com.levemus.gliderwaypoint.Messages.SerializablePayloadMessage;

import java.util.HashMap;

public abstract class WifiDirectMessage<E> extends SerializablePayloadMessage<E, String, String> {

    public WifiDirectMessage(E opCode) {
        super(opCode, new HashMap<String, String>());
    }

    public WifiDirectMessage(E opCode, HashMap<String, String> data) {
        super(opCode, data);
    }

}
