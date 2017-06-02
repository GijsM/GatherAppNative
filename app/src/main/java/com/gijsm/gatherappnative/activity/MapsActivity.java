package com.gijsm.gatherappnative.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.gijsm.gatherappnative.DatabaseUtils;
import com.gijsm.gatherappnative.R;
import com.gijsm.gatherappnative.data.UserLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String groupId;

    private ChildEventListener currentListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            try {
                if (dataSnapshot.getKey().equals("name")) return;
                UserLocation location = dataSnapshot.getValue(UserLocation.class);
                LatLng pos = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(location.name));
                if (dataSnapshot.getKey().equals(DatabaseUtils.googleId))
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                locations.put(dataSnapshot.getKey(), marker);
                updateCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            try {
                UserLocation location = dataSnapshot.getValue(UserLocation.class);
                LatLng pos = new LatLng(location.latitude, location.longitude);
                locations.get(dataSnapshot.getKey()).setPosition(pos);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            try {
                Marker marker = locations.get(dataSnapshot.getKey());
                marker.remove();
                locations.remove(dataSnapshot.getKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        groupId = getIntent().getStringExtra("groupId");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FloatingActionButton remove = (FloatingActionButton) findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseUtils.removeGroup(groupId);
                finish();
            }
        });
        FloatingActionButton copy = (FloatingActionButton) findViewById(R.id.getid);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(MapsActivity.this, groupId);
                Snackbar.make(view, "Copied code to clipboard!", Snackbar.LENGTH_LONG).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("groupName"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        database.getReference("Groups").child(groupId).removeEventListener(currentListener);
        super.onDestroy();
    }

    static private Map<String, Marker> locations = new HashMap<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        database.getReference("Groups").child(groupId).addChildEventListener(currentListener);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }


    private void updateCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker2 : locations.values()) {
            builder.include(marker2.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 200; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
