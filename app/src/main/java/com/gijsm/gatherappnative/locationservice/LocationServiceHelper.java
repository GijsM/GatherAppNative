package com.gijsm.gatherappnative.locationservice;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.gijsm.gatherappnative.DatabaseUtils;
import com.gijsm.gatherappnative.activity.MainActivity;

/**
 * Created by Gijs on 10-4-2017.
 */

public class LocationServiceHelper {

    Activity activity;

    public LocationServiceHelper(Activity activity) {
        Log.d(MainActivity.TAG, "Trying to start activitiy...");
        this.activity = activity;
        Intent intent = new Intent(activity, LocationService.class);
        if (!isMyServiceRunning(LocationService.class)) activity.startService(intent);
        activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind() {
        activity.unbindService(mConnection);
    }

    static Messenger mService = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            DatabaseUtils.setGroupListener();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    public static void addGroup(String s) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("group", s);
        bundle.putString("id", DatabaseUtils.googleId);
        bundle.putInt("what", 0);
        message.setData(bundle);
        try {
            mService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void removeGroup(String s) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("group", s);
        bundle.putString("id", DatabaseUtils.googleId);
        bundle.putInt("what", 1);
        message.setData(bundle);
        try {
            mService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
