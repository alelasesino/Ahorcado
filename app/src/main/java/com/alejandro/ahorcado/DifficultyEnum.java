package com.alejandro.ahorcado;

public enum DifficultyEnum {

    EASY(15), NORMAL(10), HARD(5);

    private int lives;

    DifficultyEnum(int lives){
        this.lives = lives;
    }

    public int getLives() {
        return lives;
    }

}
