package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

public class Referee {
    String key;
    String name;
    int number;

    @Exclude public String getKey() {
        return key;
    }

    @Exclude public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Referee(String name) {
        this.name = name;
        this.number = 0;
    }

    public Referee() {
        this.number= 0;
    }
}
