package com.nackademin.borgstrom.nacktion;

/**
 * Created by borgs_000 on 2016-03-21.
 */
public class Product {
    private int id;
    private String name;
    private String description;
    private String acceptpris;
    private String slutTid;
    private String imagebit;

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Product(int id, String name, String description, String acceptpris, String slutTid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.acceptpris = acceptpris;
        this.slutTid = slutTid;
    }

    public Product(int id, String name, String description, String acceptpris, String slutTid, String imagebit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.acceptpris = acceptpris;
        this.slutTid = slutTid;
        this.imagebit = imagebit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAcceptpris() {
        return acceptpris;
    }

    public String getSlutTid() {
        return slutTid;
    }


    public String getImage() {
        return imagebit;
    }

    @Override
    public String toString() {
        return name;
    }
}
