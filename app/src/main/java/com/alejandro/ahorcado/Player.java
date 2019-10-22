package com.alejandro.ahorcado;

import java.util.Date;

public class Player {

    private String name;
    private int points;
    private Date date;

    public Player(){}

    public Player(String name, int points, Date date) {
        this.name = name;
        this.points = points;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
