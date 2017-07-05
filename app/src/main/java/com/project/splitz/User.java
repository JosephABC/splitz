package com.project.splitz;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@IgnoreExtraProperties
public class User {
    public String Name;
    public String Email;
    public List<String> Friends;
    public List<String> Expenses;
    public List<String> groups;

    public User(){

    }
    public User(String Email, String Name, List<String> Friends, List<String> Expenses, List<String> groups) {

        this.Email = Email;
        this.Name = Name;
        this.Friends = Friends;
        this.Expenses = Expenses;
        this.groups = groups;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Email", Email);
        result.put("Name", Name);

        return result;
    }
}

