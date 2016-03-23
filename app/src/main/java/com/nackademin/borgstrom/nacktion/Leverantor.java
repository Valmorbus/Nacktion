package com.nackademin.borgstrom.nacktion;

/**
 * Created by borgs_000 on 2016-03-23.
 */
public class Leverantor {
    private int id;
    private String name;

    public Leverantor(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
