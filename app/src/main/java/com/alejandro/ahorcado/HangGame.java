package com.alejandro.ahorcado;

import java.util.ArrayList;

public class HangGame {
    //String = "abcdefghijklmnñopqrstuvwxyz"
    //String={"", "", "", "", ""}; CADA POSICION DEL ARRAY ES UN CARACTER DE LA PALABRA
    //PARA CADA CARACTER ALMACENO UN CADENA CON TODAS LOS CARACTERES QUE YA SE INSERTARON
    //NO MOSTRAR LAS PALABRAS QUE YA ESTEN INSERTARDAS
    private String playerName; //TODO CAMBIAR A UN OBJECTO PLAYER

    private String[] charsPosition;
    private StringBuilder word, hiddenWord;
    private int lives, currentLives, points;
    private boolean comodin;

    public HangGame(){

        lives = DifficultyEnum.NORMAL.getLives();
        currentLives = lives;

    }

    private void initCharsPosition(){

        String chars = "abcdefghijklmnñopqrstuvwxyz";

        charsPosition = new String[word.length()];

        for(int i = 0; i<word.length(); i++)
            charsPosition[i] = chars;

    }

    private void initHiddenWord(){

        hiddenWord = new StringBuilder();

        for(int i = 0; i<word.length(); i++)
            hiddenWord.append("_");

    }

    public void insertChar(char c, int finalPos){

        c = Character.toLowerCase(c);

        int initPos = finalPos;

        if(finalPos == -1){
            initPos = 0;
            finalPos = word.length();
        }

        int i = initPos;
        boolean existChar = false;

        do{

            charsPosition[i] = charsPosition[i].replace(String.valueOf(c), "");

            if(Character.toLowerCase(word.charAt(i)) == c) {

                charsPosition[i] = "";
                hiddenWord.setCharAt(i, word.charAt(i));
                existChar = true;

            }

            i++;

        } while(i<finalPos);

        if(!existChar)
            currentLives -= 1;
        else
            increasePoints(initPos == finalPos);

    }

    private void increasePoints(boolean extraPoints){
        points += extraPoints ? 5 : 1;
    }

    public void setHiddenWord(String word){
        this.word = new StringBuilder(word);

        initCharsPosition();
        initHiddenWord();

    }

    public String getHiddenWord(){

        StringBuilder result = new StringBuilder();

        for(int i = 0; i<hiddenWord.length(); i++)
            result.append(hiddenWord.charAt(i)).append(" ");

        return result.toString().trim();

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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean endGame(){

        word.trimToSize();
        hiddenWord.trimToSize();

        return  word.toString().toLowerCase().equals(hiddenWord.toString().toLowerCase()) || lives <= 0;

    }

    public String[] getAvailablePositions(){

        ArrayList<String> positions = new ArrayList<>();

        if(comodin)
            positions.add("*");

        for(int i = 0; i<charsPosition.length; i++)
            if(!charsPosition[i].equals(""))
                positions.add(String.valueOf(i+1));

        return positions.toArray(new String[0]);

    }

    public String getCharsPosition(int position) {

        if(position != -1)
            return charsPosition[position].toUpperCase();
        else
            return mergeString(charsPosition).toUpperCase();

    }

    private String mergeString(String... strings) {

        String result = "";

        for(String s : strings)
            result += s.replace(result, "");

        return result;


    }

}
