package com.packag.eventmonitor.Data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class Events {
    String code;
    String date;
    int status;
    String themes;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int total_referee;
    int total_team = 0;
    Vector<Team> team  = new Vector<Team>();
    Vector<Referee> referee  = new Vector<Referee>();

    String key;
    Timestamp created_at;


    @Exclude public String getKey() {
        return key;
    }

    @Exclude public void setKey(String key) {
        this.key = key;
    }

    public Events(String code, String date, int status, String themes, int total_referee,String name) {
        this.code = code;
        this.date = date;
        this.status = status;
        this.themes = themes;
        this.name = name;
        this.total_referee = total_referee;
        this.created_at = new Timestamp(new Date());
    }


    public Events() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public int getTotal_referee() {
        return total_referee;
    }

    public void setTotal_referee(int total_referee) {
        this.total_referee = total_referee;
    }

    public int getTotal_team() {
        return total_team;
    }

    public void setTotal_team(int total_team) {
        this.total_team = total_team;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Exclude public Vector<Team> getTeam() {
        return team;
    }



    @Exclude public void setTeam(Vector<Team> team) {
        this.team = team;
    }

    @Exclude public Vector<Referee> getReferee() {
        return referee;
    }



    @Exclude public void setReferee(Vector<Referee> referee) {
        this.referee = referee;
    }


    @Override
    public String toString() {
        return "Events{" +
                "code='" + code + '\'' +
                ", date='" + date + '\'' +
                ", status=" + status +
                ", themes='" + themes + '\'' +
                ", total_referee=" + total_referee +
                ", total_team=" + total_team +
                ", team=" + team.toString() +
                ", key='" + key + '\'' +
                '}';
    }
}
