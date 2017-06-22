package com.project.splitz;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Items3 {

    private String title;
    private String OwnerName;
    private String ID;
    private Float Amount;

    public Items3(String title, String OwnerName, String ID, Float Amount) {
        super();
        this.title = title;
        this.OwnerName = OwnerName;
        this.ID = ID;
        this.Amount = Amount;

    }

    // getters and setters...

    public String getTitle() {
        return title;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public String getID() {
        return ID;
    }

    public Float getAmount() { return Amount;};
}
