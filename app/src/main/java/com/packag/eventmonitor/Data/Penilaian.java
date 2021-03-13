package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

public class Penilaian implements Comparable<Penilaian>{
    double nilai;
    String type;
    String keterangan;
    String form_id;
    String key;
    int order;

    @Exclude public int getOrder() {
        return order;
    }

    @Exclude public void setOrder(int order) {
        this.order = order;
    }

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

    public Penilaian(double nilai, String type, String keterangan, String form_id, String key) {
        this.nilai = nilai;
        this.type = type;
        this.keterangan = keterangan;
        this.form_id = form_id;
        this.key = key;
        this.order = order;
    }

    @Override
    public int compareTo(Penilaian penilaian) {
        Integer number1 = (Integer)getOrder();
        Integer number2 = (Integer)penilaian.getOrder();
        //return number2.compareTo(number1); //desc
        return number1.compareTo(number2); //asc
    }
}
