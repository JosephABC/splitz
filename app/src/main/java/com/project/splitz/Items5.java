package com.project.splitz;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;

public class Items5 {

    private String title;
    private String OwnerUID;
    private String OwnerName;
    private String ID;
    private Float TotalAmount;
    private String Description;
    private String GroupCurrencyID;

    public Items5(String title, String Description, String OwnerUID, String OwnerName, String ID, Float TotalAmount, String GroupCurrencyID) {
        super();
        this.title = title;
        this.Description = Description;
        this.OwnerUID = OwnerUID;
        this.OwnerName = OwnerName;
        this.ID = ID;
        this.TotalAmount = TotalAmount;
        this.GroupCurrencyID = GroupCurrencyID;

    }

    // getters and setters...

    public String getTitle() {
        return title;
    }

    public String getOwnerUID() {
        return OwnerUID;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public String getID() {
        return ID;
    }

    public Float getTotalAmount() { return TotalAmount;};

    public String getDescription() { return Description;};

    public String getGroupCurrencyID() { return GroupCurrencyID;};
}
