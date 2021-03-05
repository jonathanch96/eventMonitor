package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

import java.util.Vector;

public class RefereePenilaian implements Comparable<RefereePenilaian>{
    String key;
    int order;
    @Exclude public int getOrder() {
        return order;
    }

    @Exclude public void setOrder(int order) {
        this.order = order;
    }
    Vector<Penilaian> penilaians  = new Vector<Penilaian>();
    public double getTotal_nilai() {
        return total_nilai;
    }

    public void setTotal_nilai(double total_nilai) {
        this.total_nilai = total_nilai;
    }

    public double getTotal_potongan() {
        return total_potongan;
    }

    public void setTotal_potongan(double total_potongan) {
        this.total_potongan = total_potongan;
    }

    public double getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = grand_total;
    }

    double total_nilai;
    double total_potongan;
    double grand_total;
    int isEditable =1;

    public int getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(int isEditable) {
        this.isEditable = isEditable;
    }

    @Exclude public Vector<Penilaian> getPenilaians() {
        return penilaians;
    }

    @Exclude public void setPenilaians(Vector<Penilaian> penilaians) {
        this.penilaians = penilaians;
    }

    @Exclude public String getKey() {
        return key;
    }

    @Exclude public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int compareTo(RefereePenilaian rp) {
        Integer number1 = (Integer)getOrder();
        Integer number2 = (Integer)rp.getOrder();
        //return number2.compareTo(number1); //desc
        return number1.compareTo(number2); //asc
    }

}
