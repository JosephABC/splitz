package com.project.splitz;

/**
 * Created by Joseph Ang on 26/6/2017.
 */

public class ItemsUserInfo {
    private String name;
    private String email;
    private Float amount;
    private String CurrencyID;

    public ItemsUserInfo(String name, String email, Float amount, String CurrencyID){
        super();
        this.name = name;
        this.email = email;
        this.amount = amount;
        this.CurrencyID = CurrencyID;
    }
    public String getName(){return name;};
    public String getEmail(){return email;};
    public Float getAmount(){return amount;};
    public String getCurrencyID(){return CurrencyID;};
}
