package com.alejandro.ahorcado.model;

import android.content.Context;

import com.alejandro.ahorcado.R;

import java.util.Date;

public class Player {

    private String name;
    private int points;
    private Date date;

    public Player(Context context){
        name = context.getString(R.string.default_player_name);
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
