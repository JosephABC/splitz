package com.project.splitz;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Expenses {

    public String title;
    public String description;
    public float totalAmount;
    public String ownerUID;
    public String ownerName;
    public Map<String, Float> payers;
//    public List<String> payers;

    public Expenses(){

    }


    public Expenses(String title, String description, float totalAmount, String ownerUID, String ownerName, Map<String, Float> payers) {
        this.title = title;
        this.description = description;
        this.totalAmount = totalAmount;
        this.ownerUID = ownerUID;
        this.ownerName = ownerName;
        this.payers = payers;

    }


}
