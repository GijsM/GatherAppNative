package com.gijsm.gatherappnative.locationservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.gijsm.gatherappnative.DatabaseUtils;
import com.gijsm.gatherappnative.data.UserLocation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Set;

public class LocationService extends Service {
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 6000;
    private static final float LOCATION_DISTANCE = 10f;

    private FirebaseDatabase database;
    public String googleId;
    private UserLocation lastLocation;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    LocationListener listener = new LocationListener();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    listener);
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    listener);
            lastLocation = getUserLocation(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        } catch (SecurityException e) {
            Log.e("GatherApp", "No permission");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(listener);
            } catch (SecurityException ex) {
                Log.i("GatherApp", "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.getData().getInt("what")) {
                case 0:
                    googleId = msg.getData().getString("id");
                    addGroup(msg.getData().getString("group"));
                    break;
                case 1:
                    googleId = msg.getData().getString("id");
                    removeGroup(msg.getData().getString("group"));
                    break;
                case 2:
                    googleId = msg.getData().getString("id");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            FirebaseApp.initializeApp(LocationService.this);

            updateGroups(getUserLocation(location));
        }

        @Override public void onProviderDisabled(String provider) {}
        @Override public void onProviderEnabled(String provider) {}
        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public UserLocation getUserLocation(Location location) {
        UserLocation userLocation = new UserLocation();
        userLocation.name = DatabaseUtils.auth.getCurrentUser().getDisplayName();
        userLocation.latitude = location.getLatitude();
        userLocation.longitude = location.getLongitude();
        return userLocation;
    }


    private Set<String> groups = new HashSet<>();

    public void addGroup(String group) {
        if (group == null) return;
        groups.add(group);
        if (lastLocation != null) {
            database.getReference("Groups").child(group).child(googleId).setValue(lastLocation);
        }
    }

    public void removeGroup(String group) {
        if (group == null) return;
        groups.remove(group);
        Log.d("GatherApp", "removed:" + group);
    }

    private void updateGroups(UserLocation location) {
        lastLocation = location;
        if (database == null) {
            FirebaseApp.initializeApp(this);
            database = FirebaseDatabase.getInstance();
        }
        Log.d("GatherApp", "Updating " + groups.size() + " locations...");
        for (String groupID : groups) {
            database.getReference("Groups").child(groupID).child(googleId).setValue(location);
        }
    }


}