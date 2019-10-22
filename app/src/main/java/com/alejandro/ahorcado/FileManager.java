package com.alejandro.ahorcado;

import android.content.Context;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {

    private static final String HANG_GAME_FILE = "hang_game_options.txt";

    public static void writeHangGameOptions(Context context, HangGame hangGame) throws IOException {

        FileOutputStream outputStream = context.openFileOutput(HANG_GAME_FILE, Context.MODE_PRIVATE);
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        dataOutput.writeBoolean(hangGame.hasComodin());
        dataOutput.writeInt(hangGame.getLives());

        dataOutput.close();
        outputStream.close();

    }

    public static HangGame readHangGameOptions(Context context) throws IOException {

        HangGame hangGame = new HangGame();

        FileInputStream inputStream = context.openFileInput(HANG_GAME_FILE);
        DataInputStream dataInput = new DataInputStream(inputStream);

        hangGame.setComodin(dataInput.readBoolean());
        hangGame.setLives(dataInput.readInt());

        dataInput.close();
        inputStream.close();

        return hangGame;
    }



}
