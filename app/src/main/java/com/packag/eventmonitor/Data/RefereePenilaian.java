package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

public class RefereePenilaian {
    String key;

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
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude public void setKey(String key) {
        this.key = key;
    }

}
