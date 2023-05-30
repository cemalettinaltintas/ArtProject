package com.cemalettinaltintas.deneme;

public class Singleton {
    private Urun sentUrun;
    private static Singleton singleton;
    private Singleton(){
    }

    public Urun getSentUrun() {
        return sentUrun;
    }

    public void setSentUrun(Urun sentUrun) {
        this.sentUrun = sentUrun;
    }

    public  static Singleton getInstance(){
        if (singleton==null){
            singleton=new Singleton();
        }
        return singleton;
    }
}
