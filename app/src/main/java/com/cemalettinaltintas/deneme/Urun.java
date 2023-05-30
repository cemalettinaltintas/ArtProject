package com.cemalettinaltintas.deneme;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Urun implements Serializable {
    private int id;
    private String urunadi;
    private double fiyat;
    private int adet;
    private Bitmap resim;
    public Urun(int id,String urunadi, double fiyat, int adet, Bitmap resim) {
        this.id=id;
        this.urunadi = urunadi;
        this.fiyat = fiyat;
        this.adet = adet;
        this.resim = resim;
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getUrunadi() {
        return urunadi;
    }
    public void setUrunadi(String urunadi) {
        this.urunadi = urunadi;
    }
    public double getFiyat() {
        return fiyat;
    }
    public void setFiyat(double fiyat) {
        this.fiyat = fiyat;
    }
    public int getAdet() {
        return adet;
    }
    public void setAdet(int adet) {
        this.adet = adet;
    }
    public Bitmap getResim() {
        return resim;
    }
    public void setResim(Bitmap resim) {
        this.resim = resim;
    }
}
