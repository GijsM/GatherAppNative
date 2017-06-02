package com.gijsm.gatherappnative.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gijs on 8-4-2017.
 */

public class Group {
    public String groupID;
    public String name;
    public Group(String groupID, String name) {
        this.groupID = groupID;
        this.name = name;
    }

    public JSONObject getAsJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("groupID", groupID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group) {
            Group group = (Group) obj;
            if (group.name.equals(this.name) && group.groupID.equals(this.groupID)) return true;
        }
        return false;
    }
}
