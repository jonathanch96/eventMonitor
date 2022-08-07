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
        return total_nilai.doubleValue();
    }

    public void setTotal_nilai(double total_nilai) {
        this.total_nilai = BigDecimal.valueOf(total_nilai);
    }

    public double getTotal_potongan() {
        return total_potongan.doubleValue();
    }

    public void setTotal_potongan(double total_potongan) {
        this.total_potongan = BigDecimal.valueOf(total_potongan);
    }

    public double getGrand_total() {
        return grand_total.doubleValue();
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = BigDecimal.valueOf(grand_total);
    }
    public BigDecimal getGrand_total_bd(){
        grand_total = grand_total.setScale(2, RoundingMode.HALF_EVEN);
        return grand_total;
    }
    public BigDecimal getTotal_nilai_bd(){
        return total_nilai;
    }
    public BigDecimal getTotal_potongan_bd(){return total_potongan;}

    BigDecimal total_nilai = BigDecimal.valueOf(0);
    BigDecimal total_potongan  = BigDecimal.valueOf(0);
    BigDecimal grand_total  = BigDecimal.valueOf(0);
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
