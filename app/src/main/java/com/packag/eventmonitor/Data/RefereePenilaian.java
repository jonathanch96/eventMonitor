package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        BigDecimal d = new BigDecimal(total_nilai);
        d = d.setScale(2, RoundingMode.HALF_EVEN);
        return d.doubleValue();
    }

    public void setTotal_nilai(double total_nilai) {
        this.total_nilai = total_nilai;
    }

    public double getTotal_potongan() {
        BigDecimal d = new BigDecimal(total_potongan);
        d = d.setScale(2, RoundingMode.HALF_EVEN);
        return d.doubleValue();
    }

    public void setTotal_potongan(double total_potongan) {
        this.total_potongan = total_potongan;
    }

    public double getGrand_total() {
        BigDecimal d = new BigDecimal(grand_total);
        d = d.setScale(2, RoundingMode.HALF_EVEN);
        return d.doubleValue();
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = grand_total;
    }
    @Exclude public BigDecimal getGrand_total_bd(){
        return new BigDecimal(grand_total);
    }
    @Exclude public BigDecimal getTotal_nilai_bd(){
        return new BigDecimal(total_nilai);
    }
    @Exclude public BigDecimal getTotal_potongan_bd(){
        return new BigDecimal(total_potongan);
    }

    double total_nilai = 0;
    double total_potongan  = 0;
    double grand_total  = 0;
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
