package com.alejandro.ahorcado;

import android.util.Log;

import java.util.ArrayList;
import java.util.function.Predicate;

public class HangGame {
    //String = "abcdefghijklmnñopqrstuvwxyz"
    //String={"", "", "", "", ""}; CADA POSICION DEL ARRAY ES UN CARACTER DE LA PALABRA
    //PARA CADA CARACTER ALMACENO UN CADENA CON TODAS LOS CARACTERES QUE YA SE INSERTARON
    //NO MOSTRAR LAS PALABRAS QUE YA ESTEN INSERTARDAS
    private String[] charsPosition;
    private ArrayList<String> availablePositions;
    //TODO CAMBIAR STRING BUILDER POR ARRAY DE CHARS Y CHAR POSITION Y AVAILABLE POSITION POR UN HASHMAP
    private StringBuilder word, hiddenWord;
    private int lives, currentLives, points;
    private boolean comodin;

    public HangGame(){

        lives = DifficultyEnum.NORMAL.getLives();
        currentLives = lives;

    }

    private void initAvailablePositions(){

        availablePositions = new ArrayList<>();

        if(comodin)
            availablePositions.add("*");

        for(int i = 1; i<=word.length(); i++)
            availablePositions.add(String.valueOf(i));

    }

    private void initCharsPosition(){

        String chars = "abcdefghijklmnñopqrstuvwxyz";

        charsPosition = new String[word.length()];

        for(int i = 0; i<word.length(); i++)
            charsPosition[i] = chars;

    }

    private void initHiddenWord(){

        hiddenWord = new StringBuilder();

        for(int i = 0; i<word.length()-1; i++)
            hiddenWord.append("_ ");

        hiddenWord.append("_");

    }

    public void insertChar(char c, int pos){

        Log.d("PRUEBA POS: ", ""+pos);

        int initPos = pos;

        if(pos == -1){
            initPos = 0;
            pos = word.length();
        }

        boolean existChar = false;

        int j = initPos*2 >= hiddenWord.length() ? hiddenWord.length()-1 : initPos*2;

        for(int i = initPos; i<= pos; i++){

            charsPosition[i] = charsPosition[i].replace(String.valueOf(Character.toLowerCase(c)), "");

            if(Character.toLowerCase(word.charAt(i)) == Character.toLowerCase(c)) {

                final int posDelete = i+1;
                availablePositions.removeIf((s) -> s.equals(String.valueOf(posDelete)));

                hiddenWord.setCharAt(j, word.charAt(i));
                existChar = true;

            }

            j+=2;

        }

        if(!existChar)
            currentLives -= 1;
        else
            increasePoints(initPos == pos-1);

    }

    private void increasePoints(boolean extraPoints){
        points += extraPoints ? 5 : 1;
    }

    public void setHiddenWord(String word){
        this.word = new StringBuilder(word);

        initCharsPosition();
        initAvailablePositions();
        initHiddenWord();

    }

    public String getHiddenWord(){
        return hiddenWord.toString();
    }

    public void setLives(int lives) {
        this.lives = lives;
        this.currentLives = lives;
    }

    public int getLives() {
        return lives;
    }

    public int getCurrentLives(){
        return currentLives;
    }

    public int getPoints(){
        return points;
    }

    public void setComodin(boolean comodin){
        this.comodin = comodin;
    }

    public boolean hasComodin(){
        return comodin;
    }

    public boolean endGame(){

        word.trimToSize();
        hiddenWord.trimToSize();

        return  word.toString().toLowerCase().equals(hiddenWord.toString().toLowerCase());

    }

    public String[] getAvailablePositions(){

        String[] positions = new String[availablePositions.size()];

        return availablePositions.toArray(positions);
    }

    public String getCharsPosition(int position) {

        if(position != -1)
            return charsPosition[position].toUpperCase();
        else //TODO MERGE TODAS LAS CADENAS
            return null;
    }

}
