package com.packag.eventmonitor.Data;

import com.google.firebase.firestore.Exclude;

public class Penilaian {
    double n1;
    double n2;
    double n3;
    double n4;
    double n5;
    double n6;
    double n7;
    double n8;
    double n9;
    double n10;
    double ks1;
    double ks2;
    double ks3;
    double ks4;
    double tk;
    double p;
    double tb;
    String key;

    @Exclude public String getKey() {
        return key;
    }

    @Exclude public void setKey(String key) {
        this.key = key;
    }

    public Penilaian(double n1, double n2, double n3, double n4, double n5, double n6, double n7,
                     double n8, double n9, double n10, double ks1, double ks2, double ks3, double ks4) {
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;
        this.n5 = n5;
        this.n6 = n6;
        this.n7 = n7;
        this.n8 = n8;
        this.n9 = n9;
        this.n10 = n10;
        this.ks1 = ks1;
        this.ks2 = ks2;
        this.ks3 = ks3;
        this.ks4 = ks4;
        this.tk = (n1+n2+n3+n4+n5+n6+n7+n8+n9+n10);
        this.p = (ks1+ks2+ks3+ks4);
        this.tb = (this.tk-this.p);
    }

    public double getN1() {
        return n1;
    }

    public void setN1(double n1) {
        this.n1 = n1;
    }

    public double getN2() {
        return n2;
    }

    public void setN2(double n2) {
        this.n2 = n2;
    }

    public double getN3() {
        return n3;
    }

    public void setN3(double n3) {
        this.n3 = n3;
    }

    public double getN4() {
        return n4;
    }

    public void setN4(double n4) {
        this.n4 = n4;
    }

    public double getN5() {
        return n5;
    }

    public void setN5(double n5) {
        this.n5 = n5;
    }

    public double getN6() {
        return n6;
    }

    public void setN6(double n6) {
        this.n6 = n6;
    }

    public double getN7() {
        return n7;
    }

    public void setN7(double n7) {
        this.n7 = n7;
    }

    public double getN8() {
        return n8;
    }

    public void setN8(double n8) {
        this.n8 = n8;
    }

    public double getN9() {
        return n9;
    }

    public void setN9(double n9) {
        this.n9 = n9;
    }

    public double getN10() {
        return n10;
    }

    public void setN10(double n10) {
        this.n10 = n10;
    }

    public double getKs1() {
        return ks1;
    }

    public void setKs1(double ks1) {
        this.ks1 = ks1;
    }

    public double getKs2() {
        return ks2;
    }

    public void setKs2(double ks2) {
        this.ks2 = ks2;
    }

    public double getKs3() {
        return ks3;
    }

    public void setKs3(double ks3) {
        this.ks3 = ks3;
    }

    public double getKs4() {
        return ks4;
    }

    public void setKs4(double ks4) {
        this.ks4 = ks4;
    }

    public double getTk() {
        return tk;
    }

    public void setTk(double tk) {
        this.tk = tk;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getTb() {
        return tb;
    }

    public void setTb(double tb) {
        this.tb = tb;
    }

    public Penilaian() {
        n1=0;
        n2=0;
        n3=0;
        n4=0;
        n5=0;
        n6=0;
        n7=0;
        n8=0;
        n9=0;
        n10=0;
        ks1=0;
        ks2=0;
        ks3=0;
        ks4=0;
        tb=0;
        tk=0;
        p=0;
    }
}
