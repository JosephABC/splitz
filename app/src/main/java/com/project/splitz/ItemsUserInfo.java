package com.project.splitz;

/**
 * Created by Joseph Ang on 26/6/2017.
 */

public class ItemsUserInfo {
    private String name;
    private String email;
    private Float amount;

    public ItemsUserInfo(String name, String email, Float amount){
        super();
        this.name = name;
        this.email = email;
        this.amount = amount;
    }
    public String getName(){return name;};
    public String getEmail(){return email;};
    public Float getAmount(){return amount;};
}
