package com.project.splitz;

/**
 * Created by Joseph Ang on 2/7/2017.
 */

public class ItemGroupFragment {
    private String GroupName;
    private String GroupID;
    private String GroupCurrencyID;
    public ItemGroupFragment(String GroupName, String GroupID, String GroupCurrencyID) {
        super();
        this.GroupName = GroupName;
        this.GroupID = GroupID;
        this.GroupCurrencyID = GroupCurrencyID;
    }
    public String getGroupName() {
        return GroupName;
    }

    public String getGroupID() {
        return GroupID;
    }

    public String getGroupCurrencyID(){ return GroupCurrencyID;};

}
