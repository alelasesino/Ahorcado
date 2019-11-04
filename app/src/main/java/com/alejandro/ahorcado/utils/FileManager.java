package com.alejandro.ahorcado.utils;

import android.content.Context;

import com.alejandro.ahorcado.model.HangGame;
import com.alejandro.ahorcado.model.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileManager {

    private static final String HANG_GAME_WORDS_FILE = "hang_game_words.txt";
    private static final String HANG_GAME_PLAYERS = "hang_game_players.txt";

    public static void writeHangGameWords(Context context, String... words) throws IOException {

        DataOutputStream dataOutput = getDataOutputStream(context);

        for(String word : words)
            dataOutput.writeUTF(word);

        dataOutput.close();

    }

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

    public static void writeHangGamePlayer(Context context, ArrayList<Player> players) throws IOException {

        ObjectOutputStream output = new ObjectOutputStream(context.openFileOutput(HANG_GAME_PLAYERS, Context.MODE_PRIVATE));

        for(Player player : players)
            output.writeObject(player);

        output.close();

    }

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

    public static void appendHangGamePlayer(Context context, Player player) throws Exception {

        ArrayList<Player> players = readHangGamePlayers(context);

        boolean exist = false;

        for(Player p : players)
            if(p.getName().equalsIgnoreCase(player.getName())) { //SI EL JUGADOR EXISTE
                exist = true;

                if(p.getPoints() < player.getPoints()){ //SI LA NUEVA PUNTUACION ES MAYOR A LA ANTERIOR
                    p.setPoints(player.getPoints());
                    p.setDate(player.getDate());
                }

            }

        if(!exist)
            players.add(player);

        writeHangGamePlayer(context, players);

    }

    private static DataInputStream getDataInputStream(Context context) throws IOException {
        return new DataInputStream(context.openFileInput(HANG_GAME_WORDS_FILE));
    }

    private static DataOutputStream getDataOutputStream(Context context) throws IOException {
        return new DataOutputStream(context.openFileOutput(HANG_GAME_WORDS_FILE, Context.MODE_PRIVATE));
    }

}
