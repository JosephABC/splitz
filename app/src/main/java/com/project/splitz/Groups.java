package com.project.splitz;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Groups {

    public String groupName;

/*    public Group() {

    }*/

    public Groups(String groupName) {
        this.groupName = groupName;
    }

}