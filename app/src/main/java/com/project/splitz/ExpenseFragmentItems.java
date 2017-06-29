package com.project.splitz;

/**
 * Created by Joseph Ang on 29/6/2017.
 */

public class ExpenseFragmentItems {
    private String title;
    private String OwnerUID;
    private String OwnerName;
    private String ID;
    private Float TotalAmount;
    private String Description;
    private String GroupID;
    private String GroupName;
    private Float Amount;

    public ExpenseFragmentItems(String title, String Description, String OwnerUID, String OwnerName, String ID, Float TotalAmount, String GroupID, String GroupName, Float Amount){
        super();
        this.title = title;
        this.Description = Description;
        this.OwnerUID = OwnerUID;
        this.OwnerName = OwnerName;
        this.ID = ID;
        this.TotalAmount = TotalAmount;
        this.GroupID = GroupID;
        this.GroupName = GroupName;
        this.Amount = Amount;
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

    public String getGroupID() { return GroupID;};

    public String getGroupName() { return GroupName;};

    public Float getAmount() { return Amount;};
}
