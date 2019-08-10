package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

public class Referee {
    String key;
    String name;

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

    public Referee(String name) {
        this.name = name;
    }

    public Referee() {
    }
}
