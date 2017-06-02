package com.gijsm.gatherappnative.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gijsm.gatherappnative.R;
import com.gijsm.gatherappnative.activity.popup.GroupAddPopup;
import com.gijsm.gatherappnative.data.Group;
import com.gijsm.gatherappnative.locationservice.LocationServiceHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "GatherApp";
    public static LocationServiceHelper helper;
    public static MainActivity instance;

    public static List<Group> groups = new ArrayList<>();
    public static ArrayAdapter<Group> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GroupAddPopup.class));
            }
        });
        adapter = new ArrayAdapter<>(this,R.layout.textview, groups);
        ListView view = (ListView) findViewById(R.id.grouplist);
        view.setAdapter(adapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("groupId", adapter.getItem(i).groupID);
                intent.putExtra("groupName", adapter.getItem(i).name);
                startActivity(intent);
            }
        });
        startActivityForResult(new Intent(this, SignInActivity.class), 0);
    }

    @Override
    protected void onDestroy() {
        helper.unbind();
        super.onDestroy();
    }

    public static void initLocationServiceHelper() {
        if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(instance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(instance, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        helper = new LocationServiceHelper(instance);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        initLocationServiceHelper();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
