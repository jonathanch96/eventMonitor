package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

public class Penilaian {
    double nilai;
    String type;
    String keterangan;
    String form_id;
    String key;

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude public void setKey(String key) {
        this.key = key;
    }

    public double getNilai() {
        return nilai;
    }

    public void setNilai(double nilai) {
        this.nilai = nilai;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public Penilaian() {
       nilai=0;
    }

    public Penilaian(double nilai, String type, String keterangan, String form_id) {
        this.nilai = nilai;
        this.type = type;
        this.keterangan = keterangan;
        this.form_id = form_id;
    }
}
