package com.alejandro.ahorcado.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.model.HangGame;
import com.alejandro.ahorcado.model.Player;
import com.alejandro.ahorcado.utils.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int OPTIONS_ACTIVITY = 1, USER_ACTIVITY = 2;

    private HangGame hangGame;

    private TextView lblPlaying, lblHiddenWord, lblPoints, lblLives;
    private Spinner positionSpinner, charSpinner;
    private Button btPlay, btStart, btEnd, btPlayer, btOptions;

    private boolean waitStartGame = true;

    private ArrayList<String> charsArray = new ArrayList<>(), positionsArray = new ArrayList<>();
    private ArrayAdapter<String> charsAdapter, positionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeToolbar();
        initializeHangGame();
        initializeComponents();
        changeState();

    }

    private void initializeToolbar(){

        ActionBar toolbar = getSupportActionBar();

        if(toolbar != null){
            toolbar.setDisplayShowHomeEnabled(true);
            toolbar.setIcon(R.drawable.ic_toolbar_icon);
            toolbar.setTitle("   " + getString(R.string.title_app_name));
        }

    }

    /**
     * Inicializa los componentes XML
     */
    private void initializeComponents(){

        lblPlaying = findViewById(R.id.lblPlaying);
        lblHiddenWord = findViewById(R.id.lblHiddenWord);
        lblPoints = findViewById(R.id.lblPoints);
        lblLives = findViewById(R.id.lblLives);

        positionSpinner = findViewById(R.id.positionSpinner);
        charSpinner = findViewById(R.id.charSpinner);

        btPlay = findViewById(R.id.btPlay);
        btStart = findViewById(R.id.btStart);
        btEnd = findViewById(R.id.btEnd);
        btPlayer = findViewById(R.id.btPlayer);
        btOptions = findViewById(R.id.btOptions);

        btPlay.setOnClickListener(this::onClickPlay);
        btStart.setOnClickListener(this::onClickStart);
        btEnd.setOnClickListener(this::onClickEnd);
        btPlayer.setOnClickListener(this::onClickPlayer);
        btOptions.setOnClickListener(this::onClickOptions);

        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                updateCharSpinner();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}

        });

        lblPlaying.setText(getString(R.string.playing, ""));
        lblPoints.setText(getString(R.string.puntos, 0));
        lblLives.setText(getString(R.string.vidas, 0));
        //updateHangGameDataGUI();

        positionsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, positionsArray);
        charsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, charsArray);

        positionSpinner.setAdapter(positionsAdapter);
        charSpinner.setAdapter(charsAdapter);

    }

    private void initializeHangGame(){

        Thread thread = new Thread(()-> {

            try{

                readOptionsFile(); //LEE EL ARCHIVO CON LAS OPCIONES

            }catch (IOException e){
                System.err.println("FILE NOT FOUND");
                writeOptionsFile(); //CREA EL ARCHIVO
            }

        });

        try {
            thread.start();
            thread.join();
        }catch (InterruptedException e){}

        hangGame.setPlayer(new Player(this));

    }

    private void readOptionsFile() throws  IOException{
        if(hangGame == null)
            hangGame = FileManager.readHangGameOptions(this);
    }

    private void writeOptionsFile(){

        if(hangGame == null)
            hangGame = new HangGame();

        new Thread(() -> {

            try{

                FileManager.writeHangGameOptions(this, hangGame);

            }catch (IOException e){}

        }).start();

    }

    /**
     * Inserta el caracter en el juego y comprueba si acabo la partida
     * @param v
     */
    private void onClickPlay(View v){

        hangGame.insertChar(charSpinner.getSelectedItem().toString().charAt(0), getPositionSelected());

        updateDataGUI();

        checkEndGame();

    }

    /**
     * Comprueba si termino la partida por limite de vidas o por acertar la palabra
     */
    private void checkEndGame(){

        if(hangGame.endGame()){ //SI TERMINO LA PARTIDA POR VIDAS O PALABRA ACERTADA

            finishGame();

            Player player = hangGame.getPlayer();

            if(hangGame.getCurrentLives() > 0){ //SI NO TERMINO POR LIMITE DE VIDAS

                Toast.makeText(this, getString(R.string.winner_player, player.getName(), player.getPoints()), Toast.LENGTH_LONG).show();

                player.setDate(new Date());

            } else { //TERMINO POR LIMITE DE VIDAS

                Toast.makeText(this, getString(R.string.loser_player, player.getName(), hangGame.getWord(), player.getPoints()), Toast.LENGTH_LONG).show();

            }

        } else {

            updateCharSpinner();

        }

    }

    /**
     * Inicia la palabra al juego y actualiza la interfaz
     * @param v
     */
    private void onClickStart(View v){

        initHangGameWords(); //ESTABLECE LAS PALABRAS DISPONIBLES EN EL JUEGO

        hangGame.startGame();

        waitStartGame = false;

        changeState();

        updateDataGUI();
        updateCharSpinner();

    }

    /**
     * Inicializa el array con todas las palabras del archivo palabras, en caso de
     * que el archivo no existiese lo crea
     */
    private void initHangGameWords(){

        try{

            if(HangGame.words == null) readWordsFile(); //LEE EL ARCHIVO DE PALABRAS

        }catch (IOException e){

            System.err.println("FILE NOT FOUND"); //NO EXISTE EL ARCHIVO

            writeWordsFile(); //CREA EL ARCHIVO

            try{ readWordsFile(); }catch (IOException e1){} //LEE EL ARCHIVO DE PALABRAS DE NUEVO

        }

    }

    /**
     * Carga el array de palabras con todas las palabras del archivo
     * @throws IOException Archivo no existe
     */
    private void readWordsFile() throws  IOException{
        if(HangGame.words == null)
            HangGame.words = FileManager.readHangGameWords(this);
    }

    /**
     * Crea el archivo con todas las palabras disponibles, con palabras predeterminadas
     * en los recursos de cadenas
     */
    private void writeWordsFile(){
        try{

            FileManager.writeHangGameWords(this, getResources().getStringArray(R.array.wods_game));
            //FileManager.writeHangGameWords(this, "Alejandro", "Ordenador", "Android");

        }catch (IOException e1){}
    }

    /**
     * Manda a cambiar el estado de aplicacion a esperar juego
     * @param v
     */
    private void onClickEnd(View v){

        finishGame();

    }

    /**
     * Arranca la activity UserActivity y pasa el nombre del jugador actual
     * @param v
     */
    private void onClickPlayer(View v){

        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("PLAYER_NAME", hangGame.getPlayer().getName());
        startActivityForResult(intent, USER_ACTIVITY);

    }

    /**
     * Arranca la activity OptionsActivity y pasa los opciones actuales
     * @param v
     */
    private void onClickOptions(View v){

        Intent intent = new Intent(this, OptionsActivity.class);
        intent.putExtra("LEVEL", hangGame.getLives());
        intent.putExtra("COMODIN", hangGame.hasComodin());
        startActivityForResult(intent, OPTIONS_ACTIVITY);

    }

    /**
     * Cambia el estado de la aplicacion
     */
    private void changeState(){

        positionSpinner.setEnabled(!waitStartGame);
        charSpinner.setEnabled(!waitStartGame);

        btPlay.setEnabled(!waitStartGame);
        btStart.setEnabled(waitStartGame);
        btEnd.setEnabled(!waitStartGame);
        btPlayer.setEnabled(waitStartGame);
        btOptions.setEnabled(waitStartGame);

    }

    /**
     * Actualiza los datos de la interfaz con los datos del juego y las posiciones disponibles del spinner con las posiciones
     */
    private void updateDataGUI(){

        updateHangGameDataGUI();
        updatePositionSpinner();

    }

    /**
     * Actualiza los componentes de la interfaz con los nuevos datos del objecto HangGame
     */
    private void updateHangGameDataGUI(){

        lblPlaying.setText(getString(R.string.playing, hangGame.getPlayer().getName()));
        lblHiddenWord.setText(hangGame.getHiddenWord());
        lblPoints.setText(getString(R.string.puntos, hangGame.getPlayer().getPoints()));
        lblLives.setText(getString(R.string.vidas, hangGame.getCurrentLives()));

    }

    /**
     * Actualiza las posiciones disponibles en la palabra del spinner que indica la posicion de la palabra
     */
    private void updatePositionSpinner(){

        positionsArray.clear();
        positionsArray.addAll(Arrays.asList(hangGame.getAvailablePositions()));
        positionsAdapter.notifyDataSetChanged();

        int itemSelected = positionSpinner.getSelectedItemPosition();

        if(itemSelected == -1) //NINGUN ITEM SELECCIONADO
            itemSelected = 0;
        else if(positionsAdapter.getCount() == itemSelected) //SELECCIONADO ULTIMO ITEM
            itemSelected -= 1;

        positionSpinner.setSelection(itemSelected);

    }

    /**
     * Actualiza los caracteres disponibles del spinner con la nueva posicion seleccionada
     */
    private void updateCharSpinner(){

        String letras = hangGame.getCharsPosition(getPositionSelected());

        String[] chars = letras.split("(?!^)"); //DIVIDE LA CADENA POR CARACTERES

        charsArray.clear();
        charsArray.addAll(Arrays.asList(chars));
        charsAdapter.notifyDataSetChanged();

        charSpinner.setSelection(0);

    }

    /**
     * LLamado cuando la partida termina por acertar la palabra, fin de vidas o por el boton finalizar.
     * Cambia el estado de la aplicacion a esperar para jugar
     */
    private void finishGame(){

        waitStartGame = true;
        changeState();

    }

    /**
     * Recibe el resultado de la activity que se cerro
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
            switch (requestCode){

                case OPTIONS_ACTIVITY:
                    receivedDataOptions(data);
                    break;

                case USER_ACTIVITY:
                    receivedDataUser(data);
                    break;

            }

    }

    /**
     * Recibe el numero de vidar y si selecciono el comodin de la OptionsActivity y actualiza los datos del objeto HangGame
     * @param intent Intent que contiene el bundle con los datos recibidos de la activity
     */
    private void receivedDataOptions(Intent intent){

        Bundle data = intent.getExtras();

        if(data !=null) {

            hangGame.setLives(data.getInt("LEVEL"));
            hangGame.setComodin(data.getBoolean("COMODIN"));

            writeOptionsFile(); //GUARDA LAS OPCIONES EN LE FICHERO

        }

    }

    /**
     * Recibe el nombre del jugador escrito en la UserActivity y actualiza los datos del objeto HangGame
     * @param intent Intent que contiene el bundle con los datos recibidos de la activity
     */
    private void receivedDataUser(Intent intent){

        Bundle data = intent.getExtras();

        if(data !=null)
            hangGame.getPlayer().setName(data.getString("PLAYER_NAME"));

    }

    /**
     * Devuelve la posicion de la letra seleccionada, -1 en caso de ser todas
     * @return Posicion seleccionada, -1 si son todas las posiciones
     */
    private int getPositionSelected(){

        String position = positionSpinner.getSelectedItem().toString();

        return position.equals("*") ? -1 : (Integer.parseInt(position)-1);

    }

}
