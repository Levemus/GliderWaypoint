package com.levemus.gliderwaypoint.Messages.ServiceMessages;

import com.levemus.gliderwaypoint.Messages.SerializablePayloadMessage;

import java.util.HashMap;

/**
 * Created by markcarter on 16-01-07.
 */
public class ServiceEventMessage extends SerializablePayloadMessage<ServiceEvent.Events, String, String> {

    public ServiceEventMessage(ServiceEvent.Events opCode) {
        super(opCode, new HashMap<String, String>());
    }

    public ServiceEventMessage(ServiceEvent.Events opCode, HashMap<String, String> values) {
        super(opCode, values);
    }
}

