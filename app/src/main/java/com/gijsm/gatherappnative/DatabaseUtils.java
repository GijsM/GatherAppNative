package com.gijsm.gatherappnative;

import android.util.Log;

import com.gijsm.gatherappnative.activity.MainActivity;
import com.gijsm.gatherappnative.data.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DatabaseUtils {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static String userId;
    public static String googleId;

    public static boolean connected = false;

    public static ChildEventListener eventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Group group = new Group(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
            MainActivity.helper.addGroup(dataSnapshot.getKey());
            MainActivity.groups.add(group);
            MainActivity.adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Group group = new Group(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
            MainActivity.helper.removeGroup(dataSnapshot.getKey());
            MainActivity.groups.remove(group);
            MainActivity.adapter.notifyDataSetChanged();
            Log.d("GatherApp", "Groups left:" + MainActivity.groups.size());
        }

        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
        @Override public void onCancelled(DatabaseError databaseError) {}
    };


    public static void setGroupListener() {
        if (connected) return;
        connected = true;
        DatabaseReference reference = database.getReference("Users").child(userId);
        reference.addChildEventListener(eventListener);
    }

    public static void removeGroup(String groupId) {
        database.getReference("Users").child(userId).child(groupId).removeValue();
        database.getReference("Groups").child(groupId).child(googleId).removeValue();
    }

    public static void addGroup(final String key) {
        database.getReference("Groups").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                if (snapshot.getKey().equals("name")) {
                    database.getReference("Users").child(userId).child(key).setValue(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void createGroup(String name) {
        DatabaseReference reference = database.getReference("Groups").push();
        reference.child("name").setValue(name);
        database.getReference("Users").child(userId).child(reference.getKey()).setValue(name);
    }

}
