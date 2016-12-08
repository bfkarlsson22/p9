package com.example.brand.p9;

/**
 * Created by brand on 12/6/2016.
 */

public class User {

    private String email;
    private String name;
    private String uid;
    private String groups;
    private String username;


    public User(){
    }

    public User( String email, String name, String uid, String groups, String username){
        this.email = email;
        this.name = name;
        this.uid = uid;
        this.groups = groups;
        this.username = username;
    }



    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }

    public String getUid(){
        return uid;
    }
    public String getGroups(){
        return groups;
    }

    public String getUsername(){
        return username;
    }
}
