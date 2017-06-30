package com.project.splitz;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Groups {

    public String groupName;
    public Map<String, Float> participants = new HashMap<String, Float>();
    public List<String> Expenses;

    public Groups(){

    }

    public Groups(String groupName, Map<String, Float> participants, List<String> Expenses) {
        this.groupName = groupName;
        this.participants = participants;
        this.Expenses = Expenses;

    }



}
