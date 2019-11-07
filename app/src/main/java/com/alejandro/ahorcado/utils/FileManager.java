package com.alejandro.ahorcado.utils;

import android.content.Context;

import com.alejandro.ahorcado.model.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author Alejandro Perez Alvarez
 * @version 2.0
 * @since 27/10/2019
 *
 * Clase encargada de la lectura y escritura de los ficheros de la aplicacion.
 */
public class FileManager {

    private static final String HANG_GAME_WORDS_FILE = "hang_game_words.txt";
    private static final String HANG_GAME_PLAYERS = "hang_game_players.txt";

    /**
     * Escribe en el fichero las palabras del juego
     * @param context Contexto del juego
     * @param words Palabras a escribir en el fichero
     * @throws IOException
     */
    public static void writeHangGameWords(Context context, String... words) throws IOException {

        DataOutputStream dataOutput = getDataOutputStream(context);

        for(String word : words)
            dataOutput.writeUTF(word);

        dataOutput.close();

    }

    /**
     * Lee del fichero las palabras que contendra el juego
     * @param context Contexto de la aplicacion
     * @return Array con las palabras del archivo
     * @throws IOException Archivo no encontrado
     */
    public static String[] readHangGameWords(Context context) throws IOException {

        StringBuilder words = new StringBuilder();
        DataInputStream dataInput = getDataInputStream(context);

        try{

            while(true)
                words.append(dataInput.readUTF()).append(";");

        }catch (EOFException ignored){}

        dataInput.close();

        return words.toString().split(";");

    }

    /**
     * Escribe en el fichero los datos de los jugadores
     * @param context Contexto de la aplicacion
     * @param players Jugadores a escribir
     * @throws IOException Archivo no encontrado
     */
    public static void writeHangGamePlayer(Context context, ArrayList<Player> players) throws IOException {

        ObjectOutputStream output = new ObjectOutputStream(context.openFileOutput(HANG_GAME_PLAYERS, Context.MODE_PRIVATE));

        for(Player player : players)
            output.writeObject(player);

        output.close();

    }

    /**
     * Lee del fichero los datos de los jugadores del juego
     * @param context Contexto de la aplicacion
     * @return Array con los jugadores del fichero
     * @throws Exception En caso de no encontrar el fichero
     */
    public static ArrayList<Player> readHangGamePlayers(Context context) throws Exception {

        ObjectInputStream input = new ObjectInputStream(context.openFileInput(HANG_GAME_PLAYERS));

        ArrayList<Player> players = new ArrayList<>();

        try{

            while (true)
                players.add((Player) input.readObject());

        }catch(EOFException ignored){}

        input.close();

        return players;

    }

    /**
     * Comprueba si el jugado existe, si existe y la puntuacion obtenida es mayor a la anterior
     * actualiza la puntuacion, si no existe, añade el jugador y escribe los jugadores en el fichero
     * @param context Contexto de la aplicacion
     * @param player Jugador a añadir
     * @throws Exception Archivo no encontrado
     */
    public static void appendHangGamePlayer(Context context, Player player) throws Exception {

        ArrayList<Player> players = readHangGamePlayers(context);

        Player foundPlayer = findPlayer(players, player);

        if(foundPlayer != null){

            if(foundPlayer.getPoints() < player.getPoints()){ //SI LA NUEVA PUNTUACION ES MAYOR A LA ANTERIOR
                foundPlayer.setPoints(player.getPoints());
                foundPlayer.setDate(player.getDate());
            }

        } else
            players.add(player);

        writeHangGamePlayer(context, players);

    }

    /**
     * Busca el jugador pasado por parametro en el array y lo devuelve
     * @param players Jugadores actuales
     * @param player Jugador a buscar
     * @return Jugador encontrado, null si no se encuentra
     */
    private static Player findPlayer(ArrayList<Player> players, Player player) {

        Player foundPlayer = null;

        for(Player p : players)
            if(p.getName().equalsIgnoreCase(player.getName()))
                foundPlayer = p;

        return foundPlayer;

    }

    private static DataInputStream getDataInputStream(Context context) throws IOException {
        return new DataInputStream(context.openFileInput(HANG_GAME_WORDS_FILE));
    }

    private static DataOutputStream getDataOutputStream(Context context) throws IOException {
        return new DataOutputStream(context.openFileOutput(HANG_GAME_WORDS_FILE, Context.MODE_PRIVATE));
    }

}
