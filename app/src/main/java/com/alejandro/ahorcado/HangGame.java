package com.alejandro.ahorcado;

public class HangGame {
    //String = "abcdefghijklmnñopqrstuvwxyz"
    //String={"", "", "", "", ""}; CADA POSICION DEL ARRAY ES UN CARACTER DE LA PALABRA
    //PARA CADA CARACTER ALMACENO UN CADENA CON TODAS LOS CARACTERES QUE YA SE INSERTARON
    //NO MOSTRAR LAS PALABRAS QUE YA ESTEN INSERTARDAS
    private String[] charsPosition;

    private StringBuilder word, hiddenWord;
    private int lives, currentLives, points;

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

        int initPos = pos-1;

        if(pos == -1){
            initPos = 0;
            pos = word.length();
        }

        boolean existChar = false;

        int j = 0;

        for(int i = initPos; i<pos; i++){

            if(Character.toLowerCase(word.charAt(i)) == Character.toLowerCase(c)) {

                charsPosition[i] = charsPosition[i].replace(word.charAt(i), Character.MIN_VALUE);
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

    public boolean endGame(){

        word.trimToSize();
        hiddenWord.trimToSize();

        return  word.toString().toLowerCase().equals(hiddenWord.toString().toLowerCase());

    }

    public String getCharsPosition(int position) {
        return charsPosition[position];
    }

}
