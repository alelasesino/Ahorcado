package com.alejandro.ahorcado.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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

/**
 * @author Alejandro Perez Alvarez
 * @version 2.0
 * @since 07/11/2019
 *
 * Clase encarga de controlar la interfaz y de interactuar
 * con la clase HangGame encargada de la logica del juego
 */
public class MainActivity extends AppCompatActivity {

    public static final String ARG_PLAYER_NAME = "PLAYER_NAME";
    public static final String ARG_LEVEL = "LEVEL";
    public static final String ARG_COMODIN = "COMODIN";

    /**
     * Codigo que identifica la activity result
     */
    private static final int OPTIONS_ACTIVITY = 1, USER_ACTIVITY = 2;
    private static final String HANG_GAME_PREFERENCE = "hang_game_options";

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

    /**
     * Establece el titulo y el icono a la toolbar de la activity
     */
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

        btPlay.setOnClickListener(v -> onClickPlay());
        btStart.setOnClickListener(v -> onClickStart());
        btEnd.setOnClickListener(v -> onClickEnd());
        btPlayer.setOnClickListener(this::onClickPlayer);
        btOptions.setOnClickListener(this::onClickOptions);

        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                updateCharSpinner();
                updateHiddenTextView();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}

        });

        lblPlaying.setText(getString(R.string.playing, ""));
        lblPoints.setText(getString(R.string.puntos, 0));
        lblLives.setText(getString(R.string.vidas, 0));

        positionsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, positionsArray);
        charsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, charsArray);

        positionSpinner.setAdapter(positionsAdapter);
        charSpinner.setAdapter(charsAdapter);

    }

    /**
     * Initializa las opciones del juego leyendo el fichero de opciones
     */
    private void initializeHangGame(){

        if(hangGame == null)
            hangGame = new HangGame(this);

        recoverPreferencesOptions();
        initHangGameWords();

    }

    /**
     * Recupera las opciones del juego de las preferencias compartidas de la aplicacion
     */
    private void recoverPreferencesOptions(){

        SharedPreferences pref = getSharedPreferences(HANG_GAME_PREFERENCE, Context.MODE_PRIVATE);
        hangGame.setComodin(pref.getBoolean(ARG_COMODIN, false));
        hangGame.setLives(pref.getInt(ARG_LEVEL, 10));
        hangGame.setPlayer(new Player(this));

    }

    /**
     * Inserta el caracter en el juego y comprueba si acabo la partida
     */
    private void onClickPlay(){

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

            if(hangGame.getCurrentLives() > 0) //SI NO TERMINO POR LIMITE DE VIDAS
                winnerGame(player);
            else
                loserGame(player);

        } else {

            updateCharSpinner();

        }

    }

    /**
     * Muestra el mensaje de victoria al jugador y guardas sus estadisticas en un archivo de juegadores
     * cuando el jugador acerto la palabra
     * @param player Jugador ganador
     */
    private void winnerGame(Player player) {

        Toast.makeText(this, getString(R.string.winner_player, player.getName(), player.getPoints()), Toast.LENGTH_LONG).show();

        player.setDate(new Date());

        if(!getString(R.string.default_player_name).equalsIgnoreCase(player.getName())) //SI EL NOMBRE DEL JUGADOR NO ES EL DE POR DEFECTO
            writePlayerScore(); //GUARDO LAS ESTADISTICAS DEL JUGADOR EN EL FICHERO

    }

    /**
     * Muestra el mensaje de derrota al jugador cuando el jugador perdio el total de vidas
     * @param player Jugador que perdedor
     */
    private void loserGame(Player player) {

        Toast.makeText(this, getString(R.string.loser_player, player.getName(), hangGame.getWord(), player.getPoints()), Toast.LENGTH_LONG).show();

    }

    /**
     * Escribe las estadisticas del jugador ganador en el fichero de jugadores
     */
    private void writePlayerScore(){

        Thread thread = new Thread(() ->{

            try {

                FileManager.appendHangGamePlayer(this, hangGame.getPlayer());

            } catch (Exception ignored) {}

        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException ignored) {}

    }

    /**
     * Inicia la palabra al juego y actualiza la interfaz
     */
    private void onClickStart(){

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

            writeWordsFile();

            readWordsFile(); //LEE EL ARCHIVO DE PALABRAS

        }catch (IOException e){

            System.err.println("FILE NOT FOUND"); //NO EXISTE EL ARCHIVO

            writeWordsFile(); //CREA EL ARCHIVO

            try{ readWordsFile(); }catch (IOException ignored){} //LEE EL ARCHIVO DE PALABRAS DE NUEVO

        }

    }

    /**
     * Carga el array de palabras con todas las palabras del archivo
     * @throws IOException Archivo no existe
     */
    private void readWordsFile() throws  IOException{

        HangGame.words = FileManager.readHangGameWords(this);

        //Arrays.sort(HangGame.words, (s1, s2) -> s2.length()-s1.length());

    }

    /**
     * Crea el archivo con todas las palabras disponibles, con palabras predeterminadas
     * en los recursos de cadenas
     */
    private void writeWordsFile(){

        Thread thread = new Thread(() ->{

            try{

                FileManager.writeHangGameWords(this, getResources().getStringArray(R.array.wods_game));

            }catch (IOException ignored){}

        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException ignored) {}

    }

    /**
     * Manda a cambiar el estado de aplicacion a esperar juego
     */
    private void onClickEnd(){

        finishGame();

    }

    /**
     * Arranca la activity UserActivity y pasa el nombre del jugador actual
     * @param v Vista
     */
    private void onClickPlayer(View v){

        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(ARG_PLAYER_NAME, hangGame.getPlayer().getName());
        startActivityForResult(intent, USER_ACTIVITY);

    }

    /**
     * Arranca la activity OptionsActivity y pasa los opciones actuales
     * @param v Vista
     */
    private void onClickOptions(View v){

        Intent intent = new Intent(this, OptionsActivity.class);
        intent.putExtra(ARG_LEVEL, hangGame.getLives());
        intent.putExtra(ARG_COMODIN, hangGame.hasComodin());
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

        updatePositionSpinner();
        updateHangGameDataGUI();

    }

    /**
     * Actualiza los componentes de la interfaz con los nuevos datos del objecto HangGame
     */
    private void updateHangGameDataGUI(){

        lblPlaying.setText(getString(R.string.playing, hangGame.getPlayer().getName()));
        lblPoints.setText(getString(R.string.puntos, hangGame.getPlayer().getPoints()));
        lblLives.setText(getString(R.string.vidas, hangGame.getCurrentLives()));

        updateHiddenTextView();

    }

    /**
     * Actualiza el textView con la palabra oculta permitiendo hacer click en el textView para seleccionar
     * la posicion deseada
     */
    private void updateHiddenTextView(){

        SpannableString ss = new SpannableString(hangGame.getHiddenWord());

        setClickableHiddenWord(ss);

        if(positionSpinner.getSelectedItem() != null)
            setHighlightCharacter(ss, getPositionSelected()*2); //MULTIPLICO POR 2 POR LOS ESPACIOS ENTRE '_'

        lblHiddenWord.setHighlightColor(Color.TRANSPARENT);
        lblHiddenWord.setMovementMethod(LinkMovementMethod.getInstance());
        lblHiddenWord.setText(ss);

    }

    /**
     * Hace clickable los caracteres ocultos ('_')
     * @param ss Cadena para hacer clickable
     */
    private void setClickableHiddenWord(SpannableString ss){

        for(int i = 0; i<ss.length(); i+=2)
            if(ss.charAt(i) == '_'){
                WordClickableSpan clickableSpan = new WordClickableSpan(i);
                ss.setSpan(clickableSpan, i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

    }

    /**
     * Subraya de color rojo el caracter oculto ('_') en la posicion deseada
     * @param ss Cadena clickable
     * @param position Posicion del caracter
     */
    private void setHighlightCharacter(SpannableString ss, int position){

        if(position > -1)//COMPRUEBO QUE LA POSICION NO ES EL ASTERISCO
            ss.setSpan(new ForegroundColorSpan(Color.RED), position, position+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    /**
     * Clase encargada de seleccionar del position spinner la posicion del caracter
     * que fue clickado
     */
    private class WordClickableSpan extends ClickableSpan{

        private int position;

        private WordClickableSpan(int position){
            this.position = position;
        }

        @Override
        public void onClick(@NonNull View view) {
            positionSpinner.setSelection(getPositionItem((position/2)+1));
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setUnderlineText(false);
        }

    }

    /**
     * Obtiene el item del positionSpinner en la posicion pasada por parametro
     * @param position Posicion del item del positionSpinner
     * @return Valor entero del item del positionSpinner
     */
    private int getPositionItem(int position){

        for(int i = 0; i<positionSpinner.getAdapter().getCount(); i++)
            if(positionSpinner.getAdapter().getItem(i).toString().equalsIgnoreCase(String.valueOf(position)))
                return i;

        return 0;

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
     * @param requestCode Codigo de solicitud de la activity
     * @param resultCode Codigo del resultado del activity
     * @param data Datos recibidos de la activity
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

            hangGame.setLives(data.getInt(ARG_LEVEL));
            hangGame.setComodin(data.getBoolean(ARG_COMODIN));
            savePreferencesOptions();

        }

    }

    /**
     * Guarda las opciones del juego en las preferencias compartidas de la aplicacion
     */
    private void savePreferencesOptions(){

        SharedPreferences prefs = getSharedPreferences(HANG_GAME_PREFERENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ARG_COMODIN, hangGame.hasComodin());
        editor.putInt(ARG_LEVEL, hangGame.getLives());
        editor.apply();

    }

    /**
     * Recibe el nombre del jugador escrito en la UserActivity y actualiza los datos del objeto HangGame
     * @param intent Intent que contiene el bundle con los datos recibidos de la activity
     */
    private void receivedDataUser(Intent intent){

        Bundle data = intent.getExtras();

        if(data !=null)
            hangGame.getPlayer().setName(data.getString(ARG_PLAYER_NAME));

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
