package com.levemus.gliderwaypoint.WifiDirect;

import android.app.Activity;

import com.levemus.gliderwaypoint.Providers.MessageService;
import com.levemus.gliderwaypoint.Providers.ServiceProviderThread;


/**
 * Created by markcarter on 16-01-04.
 */
public class WifiDirectService extends MessageService {
    private final String TAG = this.getClass().getSimpleName();

    private WifiDirectServiceThread mWorkerThread = new WifiDirectServiceThread(TAG);
    protected ServiceProviderThread workerThread() {
        return mWorkerThread;
    }

    @Override
    public void pause(Activity activity) {
        mWorkerThread.pause(activity);
    }

    @Override
    public void resume(Activity activity) {
        mWorkerThread.resume(activity);
    }

    @Override
    public void onDestroy() {
        mWorkerThread.shutdown();
    }

}
