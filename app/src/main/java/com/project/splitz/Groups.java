package com.project.splitz;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Groups {

    public String groupName;
//    public List participants;


    public Groups(String groupName) {
        this.groupName = groupName;
//        this.participants = participants;

    }


}
