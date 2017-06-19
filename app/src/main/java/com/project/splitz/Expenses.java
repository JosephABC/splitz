package com.project.splitz;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Expenses {

    public String title;
    public String description;
    public float amount;
    public String ownerUID;
    public String groupId;
    public List<String> participants;

    public Expenses(){

    }


    public Expenses(String title, String description, float amount, String ownerUID, String groupId, List<String> participants) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.ownerUID = ownerUID;
        this.groupId = groupId;
        this.participants = participants;

    }


}
