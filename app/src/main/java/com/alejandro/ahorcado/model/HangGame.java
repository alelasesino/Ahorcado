package com.alejandro.ahorcado.model;

import android.content.Context;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * @author Alejandro Perez Alvarez
 * @version 2.0
 * @since 27/10/2019
 *
 * Clase encargada de la logica del Ahorcado.
 * En cada posicion de la palabra a acertar tiene una cadena con todos los
 * caracteres que estan diponibles para esa posicion de la palabra Ej.
 * Palabra: Ale
 * charsPosition: ["abcd...", "abcd...", "abcd..."]
 */
public class HangGame {

    public static final int EASY_MODE = 15;
    public static final int NORMAL_MODE = 10;
    public static final int HARD_MODE = 5;

    public static String[] words;

    private static final int EXTRA_POINTS = 5;
    private Player player;

    private Context context;
    private String[] charsPosition;
    private StringBuilder word, hiddenWord;
    private int lives, currentLives;
    private boolean comodin;

    public HangGame(Context context){
        this.context = context;
        lives = NORMAL_MODE;
    }

    /**
     * Inicializa para cada posicion de la palabra con todas las letras disponibles
     */
    private void initCharsPosition(){

        String chars = context.getString(R.string.chars);

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

    /**
     * Inserta un caracter en la posicion indicada en la palabra oculta
     * @param c Caracter a insertar
     * @param finalPos Posicion del caracter a insertar
     */
    public void insertChar(char c, int finalPos){

        c = Character.toLowerCase(c);

        int initPos = finalPos;

        if(finalPos == -1){ //SI SON TODAS LAS POSICIONES '*'
            initPos = 0;
            finalPos = word.length();
        }

        int i = initPos;
        boolean existChar = false;

        do{
            //ELIMINO EL CARACTER INTRODUCIDO DE TODOS LOS CARACTERES DIPONIBLES Ej. Introducido 'c' remplazo "abcdefg.." a "abdefg.."
            charsPosition[i] = charsPosition[i].replace(String.valueOf(c), "");

            if(Character.toLowerCase(word.charAt(i)) == c) {

                charsPosition[i] = ""; //ELIMINO TODOS LOS CARACTERES DIPONIBLES
                hiddenWord.setCharAt(i, word.charAt(i));
                existChar = true;

            }

            i++;

        } while(i<finalPos);

        checkSuccess(existChar, initPos == finalPos);

    }

    /**
     * Comprueba si el caracter introducido existe, si no existe disminuye las vidas,
     * si existe incrementa los puntos
     * @param existChar Existe el caracter introducido en la palabra oculta
     * @param extraPoints Añadir puntuacion extra al acierto
     */
    private void checkSuccess(boolean existChar, boolean extraPoints){

        if(!existChar)
            currentLives -= 1;
        else
            increasePoints(extraPoints);

    }

    /**
     * Incrementa los puntos de la partida
     * @param extraPoints Puntos extras
     */
    private void increasePoints(boolean extraPoints){
        player.setPoints(player.getPoints() + (extraPoints ? EXTRA_POINTS : 1));
    }

    private void setHiddenWord(String word){
        this.word = new StringBuilder(word);
    }

    /**
     * Empieza una partida estableciendo la palabra aleatoriamente
     */
    public void startGame(){

        setHiddenWord(getRandomWord());

        initCharsPosition();
        initHiddenWord();

        resetGame();

    }

    /**
     * Reinicia el juego, la puntuacion y las vidas
     */
    private void resetGame(){

        player.setPoints(0);
        currentLives = lives;

    }

    /**
     * Devuelve una palabra aleatoria del array que contiene todas las palabras del juego
     * @return Palabra aleatoria
     */
    private String getRandomWord(){

        if(words != null){
            int length = lives >= words.length ? words.length : lives;
            return words[Utils.getRandomNumber(0, length)];
        }

        return "";

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
    }

    public int getLives() {
        return lives;
    }

    public int getCurrentLives(){
        return currentLives;
    }

    public void setComodin(boolean comodin){
        this.comodin = comodin;
    }

    public boolean hasComodin(){
        return comodin;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getWord(){
        return word.toString();
    }

    /**
     * Comprueba si la partida ha terminado ya sea por puntos o por vidas
     * @return True si la partida ha terminado, false si no termino
     */
    public boolean endGame(){

        word.trimToSize();
        hiddenWord.trimToSize();

        return  word.toString().toLowerCase().equals(hiddenWord.toString().toLowerCase()) || currentLives <= 0;

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
