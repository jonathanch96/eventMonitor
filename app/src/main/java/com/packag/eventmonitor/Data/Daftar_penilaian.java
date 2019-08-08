package com.packag.eventmonitor.Data;

public class Daftar_penilaian {
    String kriteria;
    int no;
    int type;

    public Daftar_penilaian() {
    }

    public Daftar_penilaian(String kriteria, int no, int type) {
        this.kriteria = kriteria;
        this.no = no;
        this.type = type;
    }

    public String getKriteria() {
        return kriteria;
    }

    public void setKriteria(String kriteria) {
        this.kriteria = kriteria;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
