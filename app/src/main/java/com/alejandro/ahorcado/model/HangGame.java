package com.alejandro.ahorcado.model;

import android.content.Context;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.utils.Utils;

import java.util.ArrayList;


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

    public static String[] words; //PALABRAS PARA ADIVINAR

    private static final int EXTRA_POINTS = 5;
    private Player player;

    private Context context;
    private String[] charsPosition;
    private StringBuilder word, hiddenWord;
    private int lives, currentLives; //VIDAS DE LA DIFICULTDAD, VIDAS ACTUALES DE LA PARTIDA
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
     * y actualiza la puntuacion en caso de acierto
     * @param c Caracter a insertar
     * @param position Posicion del caracter a insertar
     */
    public void insertChar(char c, int position){

        c = Character.toLowerCase(c);

        int[] initFinalPositions = getInitFinalPosition(position);

        int initPosition = initFinalPositions[0];
        int finalPosition = initFinalPositions[1];

        Integer[] positionsCharExist = getPositionsCharExist(c, initPosition, finalPosition);

        for(Integer integer : positionsCharExist)
            insertChar(integer);

        checkSuccess(positionsCharExist.length > 0, !isComodin(position));

    }

    /**
     * @param position Posicion a comprobar
     * @return true si la posicion seleccionada es el asterisco, false si no es el asterisco
     */
    private boolean isComodin(int position){
        return position == -1;
    }

    /**
     * Metodo que recibe la posicion seleccionada por el usuario, comprueba si selecciono
     * el asterisco o no y devuelve la posicion inicial y final que se tiene que buscar el caracter
     * en la palabra oculta. Si es el asterisco devuelve la longitud de la palabra de lo contrario
     * devuelve la posicion seleccionada por el usuario
     * @param position Posicion seleccionada por el usuario
     * @return Array de enteros, siendo la posicion 0, la posicion inicial
     * y la posicion 1, la posicion final
     */
    private int[] getInitFinalPosition(int position){

        if(isComodin(position))
            return new int[]{0, word.length()};
        else
            return new int[]{position, position};

    }

    /**
     * Metodo que busca el caracter recibido en la palabra oculta y devuelve la posicion de los caracteres
     * que coincidan con el caracter recibido por parametro
     * @param c Caracter a buscar
     * @param initPos Posicion inicial a buscar
     * @param finalPos Posicion final a buscar
     * @return Lista de enteros con las posiciones de los caracteres que existen en al palabra oculta
     */
    private Integer[] getPositionsCharExist(char c,  int initPos, int finalPos) {

        int i = initPos;

        ArrayList<Integer> charsPositionFound = new ArrayList<>();

        do{
            //ELIMINO EL CARACTER INTRODUCIDO DE TODOS LOS CARACTERES DIPONIBLES Ej. Introducido 'c' remplazo "abcdefg.." a "abdefg.."
            charsPosition[i] = charsPosition[i].replace(String.valueOf(c), "");

            if(Character.toLowerCase(word.charAt(i)) == c)
                charsPositionFound.add(i);

            i++;

        } while(i<finalPos);

        return charsPositionFound.toArray(new Integer[0]);

    }

    /**
     * Inserta el caracter en la palabra oculta en posicion deseada y
     * elimina todos los caracteres disponibles de esa posicion
     * @param position Posicion del caracter a insertar
     */
    private void insertChar(int position) {

        charsPosition[position] = ""; //ELIMINO TODOS LOS CARACTERES DISPONIBLES
        hiddenWord.setCharAt(position, word.charAt(position));

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
