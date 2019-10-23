package com.alejandro.ahorcado;

import android.content.Context;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

    private static final String HANG_GAME_FILE = "hang_game_options.txt";
    private static final String HANG_GAME_WORDS_FILE = "hang_game_words.txt";

    public static void writeHangGameOptions(Context context, HangGame hangGame) throws IOException {

        DataOutputStream dataOutput = getDataOutputStream(context, HANG_GAME_FILE);

        dataOutput.writeBoolean(hangGame.hasComodin());
        dataOutput.writeInt(hangGame.getLives());

        dataOutput.close();

    }

    public static HangGame readHangGameOptions(Context context) throws IOException {

        HangGame hangGame = new HangGame();

        DataInputStream dataInput = getDataInputStream(context, HANG_GAME_FILE);

        hangGame.setComodin(dataInput.readBoolean());
        hangGame.setLives(dataInput.readInt());

        dataInput.close();

        return hangGame;

    }

    public static void writeHangGameWords(Context context, String... words) throws IOException {

        DataOutputStream dataOutput = getDataOutputStream(context, HANG_GAME_WORDS_FILE);

        for(String word : words)
            dataOutput.writeUTF(word);

        dataOutput.close();

    }

    public static String[] readHangGameWords(Context context) throws IOException {

        StringBuilder words = new StringBuilder();
        DataInputStream dataInput = getDataInputStream(context, HANG_GAME_WORDS_FILE);

        try{

            while(true)
                words.append(dataInput.readUTF()).append(";");

        }catch (EOFException e){}

        dataInput.close();

        return words.toString().split(";");

    }

    private static DataInputStream getDataInputStream(Context context, String path) throws IOException {
        return new DataInputStream(context.openFileInput(path));
    }

    private static DataOutputStream getDataOutputStream(Context context, String path) throws IOException {
        return new DataOutputStream(context.openFileOutput(path, Context.MODE_PRIVATE));
    }

}
