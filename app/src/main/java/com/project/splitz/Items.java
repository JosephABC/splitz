package com.project.splitz;

public class Items {

    private String title;
    private String description;
    private String GroupCurrencyID;

    public Items(String title, String description, String GroupCurrencyID) {
        super();
        this.title = title;
        this.description = description;
        this.GroupCurrencyID = GroupCurrencyID;
    }

    // getters and setters...

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getGroupCurrencyID(){ return GroupCurrencyID;};
}
