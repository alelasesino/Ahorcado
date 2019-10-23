package com.alejandro.ahorcado;

import java.util.ArrayList;

public class HangGame {
    //String = "abcdefghijklmnñopqrstuvwxyz"
    //String={"", "", "", "", ""}; CADA POSICION DEL ARRAY ES UN CARACTER DE LA PALABRA
    //PARA CADA CARACTER ALMACENO UN CADENA CON TODAS LOS CARACTERES QUE YA SE INSERTARON
    //NO MOSTRAR LAS PALABRAS QUE YA ESTEN INSERTARDAS
    public static String[] words;

    private String playerName; //TODO CAMBIAR A UN OBJECTO PLAYER

    private String[] charsPosition;
    private StringBuilder word, hiddenWord;
    private int lives, currentLives, points;
    private boolean comodin;

    public HangGame(){

        lives = DifficultyEnum.NORMAL.getLives();
        currentLives = lives;

    }

    /**
     * Inicializa para cada posicion de la palabra con todas las letras disponibles
     */
    private void initCharsPosition(){

        String chars = "abcdefghijklmnñopqrstuvwxyz";

        charsPosition = new String[word.length()];

        for(int i = 0; i<word.length(); i++)
            charsPosition[i] = chars;

    }

    /**
     * Inicializa la palabra oculta con guiones bajos
     */
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

    /**
     * Incrementa los puntos de la partida
     * @param extraPoints Puntos extras
     */
    private void increasePoints(boolean extraPoints){
        points += extraPoints ? 5 : 1;
    }

    private void setHiddenWord(String word){
        this.word = new StringBuilder(word);
    }

    public void startGame(){

        setHiddenWord(getRandomWord());

        initCharsPosition();
        initHiddenWord();

    }

    private String getRandomWord(){
        return words != null ? words[Utils.getRandomNumber(0, words.length)] : "";
    }

    /**
     * Devuelve la palabra oculta añadiendo espacios entre cada caracter
     * @return Palabra oculta
     */
    public String getHiddenWord(){

        if(hiddenWord != null){

            StringBuilder result = new StringBuilder();

            for(int i = 0; i<hiddenWord.length(); i++)
                result.append(hiddenWord.charAt(i)).append(" ");

            return result.toString().trim();

        }

        return "";

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

    /**
     * Comprueba si la partida ha terminado ya sea por puntos o por vidas
     * @return
     */
    public boolean endGame(){

        word.trimToSize();
        hiddenWord.trimToSize();

        return  word.toString().toLowerCase().equals(hiddenWord.toString().toLowerCase()) || lives <= 0;

    }

    /**
     * Obtiene los numeros de las posiciones disponibles de la palabra
     * @return Cadena con todas las posiciones disponibles
     */
    public String[] getAvailablePositions(){

        ArrayList<String> positions = new ArrayList<>();

        if(comodin)
            positions.add("*");

        for(int i = 0; i<charsPosition.length; i++)
            if(!charsPosition[i].equals(""))
                positions.add(String.valueOf(i+1));

        return positions.toArray(new String[0]);

    }

    /**
     * Obtiene todos los caracteres disponibles para la posicion indicada por parametro
     * @param position Posicion de la cadena
     * @return Cadena con todas las letras disponibles
     */
    public String getCharsPosition(int position) {

        if(position != -1)
            return charsPosition[position].toUpperCase();
        else
            return mergeString(charsPosition).toUpperCase();

    }

    /**
     * Fusiona todas las cadenas recibidas en un una. Ej. input["abc", "bcde"] output["abcde"]
     * @param strings Cadenas a fusionar
     * @return Cadena fusionada
     */
    private String mergeString(String... strings) {

        String result = "";

        for(String s : strings)
            result += s.replace(result, "");

        return result;


    }

}
