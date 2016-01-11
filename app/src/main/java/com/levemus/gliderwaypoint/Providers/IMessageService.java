package com.levemus.gliderwaypoint.Providers;

import android.app.Activity;

import com.levemus.gliderwaypoint.Managers.IClient;
import com.levemus.gliderwaypoint.Messages.SerializablePayloadMessage;

import java.util.UUID;

/**
 * Created by mark@levemus on 16-01-02.
 */

public interface IMessageService {
    void start(Activity activity, Class service, UUID id);
    void stop(Activity activity, Class service);
    void sendRequest(SerializablePayloadMessage msg);
    void pause(Activity activity);
    void resume(Activity activity);
    void registerClient(IClient client);
    void deRegisterClient(IClient client);
}
