package com.gijsm.gatherappnative.activity.popup;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gijsm.gatherappnative.DatabaseUtils;
import com.gijsm.gatherappnative.R;

/**
 * Created by Gijs on 10-4-2017.
 */

public class GroupAddPopup extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_add_laout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        getWindow().setLayout((int) (displayMetrics.widthPixels*.8), (int)(displayMetrics.heightPixels*.25));

        Button createbutton = (Button) findViewById(R.id.createnewgroup);
        createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.group_create_new);
                ((EditText) findViewById(R.id.groupidname)).setHint("Name");
                Button createbuttonfinal = (Button) findViewById(R.id.submitgroup);
                createbuttonfinal.setText("Create new Group");
                createbuttonfinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseUtils.createGroup(((EditText) findViewById(R.id.groupidname)).getText().toString());
                        finish();
                    }
                });
            }
        });

        Button joinbutton = (Button) findViewById(R.id.joingroup);
        joinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.group_create_new);
                ((EditText) findViewById(R.id.groupidname)).setHint("Code");
                Button createbuttonfinal = (Button) findViewById(R.id.submitgroup);
                createbuttonfinal.setText("Join Group");
                createbuttonfinal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseUtils.addGroup(((EditText) findViewById(R.id.groupidname)).getText().toString());
                        finish();
                    }
                });
            }
        });
    }
}
