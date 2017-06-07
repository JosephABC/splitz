package com.project.splitz;
import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {
    public String Name;
    public String Email;

    public User(String Email, String Name) {
        this.Name = Name;
        this.Email = Email;
    }

}

