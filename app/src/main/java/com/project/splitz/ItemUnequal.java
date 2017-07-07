package com.project.splitz;

/**
 * Created by Joseph Ang on 6/7/2017.
 */

public class ItemUnequal {
    private String UserID;
    private String UserName;
    private String UserEmail;
    private String OriginalCurrency;
    private Float OriginalAmount;
    private String EndCurrency;
    private Float EndAmount;
    private Float ExchangeRate;
    private Float TotalAmount;

    public ItemUnequal(String UserID, String UserName, String UserEmail,String OriginalCurrency, Float OriginalAmount, String EndCurrency, Float EndAmount, Float ExchangeRate, Float TotalAmount) {
        super();
        this.UserID = UserID;
        this.UserName = UserName;
        this.UserEmail = UserEmail;
        this.OriginalCurrency = OriginalCurrency;
        this.OriginalAmount = OriginalAmount;
        this.EndCurrency = EndCurrency;
        this.EndAmount = EndAmount;
        this.ExchangeRate = ExchangeRate;
        this.TotalAmount = TotalAmount;

    }
    public String getUserID() {
        return UserID;
    }
    public String getUserName() {
        return UserName;
    }
    public String getUserEmail() {return UserEmail;}
    public String getOriginalCurrency() {
        return OriginalCurrency;
    }
    public Float getOriginalAmount() {
        return OriginalAmount;
    }
    public String getEndCurrency() {
        return EndCurrency;
    }
    public Float getEndAmount() {
        return EndAmount;
    }
    public Float getExchangeRate() {
        return ExchangeRate;
    }
    public Float getTotalAmount() {
        return TotalAmount;
    }

    public void setOriginalAmount(Float OriginalAmount){
        this.OriginalAmount = OriginalAmount;
    }
    public void setEndAmount(Float EndAmount){
        this.EndAmount = EndAmount;
    }
}
