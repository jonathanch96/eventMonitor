package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

import java.util.Vector;

public class Team {
    String team_name;
    String key;
    int no_urut;
    Vector<Penilaian> dataNilai;
    Penilaian penilaian;


    @Exclude public Penilaian getPenilaian() {
        return penilaian;
    }

    @Exclude public void setPenilaian(Penilaian penilaian) {
        this.penilaian = penilaian;
    }


    @Exclude public Vector<Penilaian> getDataNilai() {
        return dataNilai;
    }

    @Exclude public void setDataNilai(Vector<Penilaian> dataNilai) {
        this.dataNilai = dataNilai;
    }

    public Team(String team_name, int no_urut) {
        this.team_name = team_name;
        this.no_urut = no_urut;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    @Exclude public String getKey() {
        return key;
    }

    @Exclude public void setKey(String key) {
        this.key = key;
    }

    public int getNo_urut() {
        return no_urut;
    }

    public void setNo_urut(int no_urut) {
        this.no_urut = no_urut;
    }

    public Team() {
    }

    @Override
    public String toString() {
        return "Team{" +
                "team_name='" + team_name + '\'' +
                ", key='" + key + '\'' +
                ", no_urut=" + no_urut +
                '}';
    }
}
