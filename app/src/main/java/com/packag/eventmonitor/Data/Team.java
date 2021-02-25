package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

import java.util.Vector;

public class Team {
    String team_name;
    String key;
    int no_urut;
    Vector<RefereePenilaian> dataNilai;
    RefereePenilaian penilaian;


    double nilai_bersih;
    double pengurangan_nb;
    double total_nilai; //final

    public double getNilai_bersih() {
        return nilai_bersih;
    }

    public void setNilai_bersih(double nilai_bersih) {
        this.nilai_bersih = nilai_bersih;
    }

    public double getPengurangan_nb() {
        return pengurangan_nb;
    }

    public void setPengurangan_nb(double pengurangan_nb) {
        this.pengurangan_nb = pengurangan_nb;
    }

    public double getTotal_nilai() {
        return total_nilai;
    }

    public void setTotal_nilai(double total_nilai) {
        this.total_nilai = total_nilai;
    }

    @Exclude public RefereePenilaian getPenilaian() {
        return penilaian;
    }

    @Exclude public void setPenilaian(RefereePenilaian penilaian) {
        this.penilaian = penilaian;
    }


    @Exclude public Vector<RefereePenilaian> getDataNilai() {
        return dataNilai;
    }

    @Exclude public void setDataNilai(Vector<RefereePenilaian> dataNilai) {
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
