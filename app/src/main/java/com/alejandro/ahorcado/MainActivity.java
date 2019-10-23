package com.alejandro.ahorcado;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

        initializeHangGame();
        initializeComponents();
        changeState();

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

       //lblPlaying.setText(getString(R.string.playing, hangGame.getPlayerName()));
        /*lblPoints.setText(getString(R.string.puntos, 0));
        lblLives.setText(getString(R.string.vidas, 0));*/
        updateHangGameDataGUI();

        positionsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, positionsArray);
        charsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, charsArray);

        positionSpinner.setAdapter(positionsAdapter);
        charSpinner.setAdapter(charsAdapter);

    }

    private void initializeHangGame(){

        try{

            readOptionsFile(); //LEE EL ARCHIVO CON LAS OPCIONES

        }catch (IOException e){
            System.err.println("FILE NOT FOUND");
            writeOptionsFile(); //CREA EL ARCHIVO

        }

    }

    private void readOptionsFile() throws  IOException{
        if(hangGame == null)
            hangGame = FileManager.readHangGameOptions(this);
    }

    private void writeOptionsFile(){
        try{

            if(hangGame == null)
                hangGame = new HangGame();

            FileManager.writeHangGameOptions(this, hangGame);

        }catch (IOException e){}
    }

    /**
     * Inserta el caracter en el juego
     * @param v
     */
    private void onClickPlay(View v){

        hangGame.insertChar(charSpinner.getSelectedItem().toString().charAt(0), getPositionSelected());

        updateDataGUI();
        updateCharSpinner();

    }

    /**
     * Inicia la palabra al juego y actualiza la interfaz
     * @param v
     */
    private void onClickStart(View v){

        initHangGameWords();
        hangGame.startGame();

        waitStartGame = false;
        changeState();
        updateDataGUI();

    }

    private void initHangGameWords(){

        try{

            if(HangGame.words == null) readWordsFile(); //LEE EL ARCHIVO DE PALABRAS

        }catch (IOException e){

            System.err.println("FILE NOT FOUND"); //NO EXISTE EL ARCHIVO

            writeWordsFile(); //CREA EL ARCHIVO

            try{ readWordsFile(); }catch (IOException e1){} //LEE EL ARCHIVO DE PALABRAS DE NUEVO

        }

    }

    private void readWordsFile() throws  IOException{
        if(HangGame.words == null)
            HangGame.words = FileManager.readHangGameWords(this);
    }

    private void writeWordsFile(){
        try{

            FileManager.writeHangGameWords(this, "Alejandro", "Ordenador", "Android");

        }catch (IOException e1){}
    }

    /**
     * Manda a cambiar el estado de aplicacion a esperar juego
     * @param v
     */
    private void onClickEnd(View v){

        waitStartGame = true;
        changeState();

    }

    private void onClickPlayer(View v){

        Intent intent = new Intent(this, UserActivity.class);
        startActivityForResult(intent, USER_ACTIVITY);

    }

    /**
     * Arranca la activity OptionsActivity y pasa los opciones actuales
     * @param v
     */
    private void onClickOptions(View v){

        Intent intent = new Intent(this, OptionsActivity.class);
        intent.putExtra("LEVEL", hangGame.getCurrentLives());
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
     * Actualiza los datos de la interfaz con los datos del juego
     */
    private void updateDataGUI(){

        updateHangGameDataGUI();
        updatePositionSpinner();

    }

    private void updateHangGameDataGUI(){

        lblHiddenWord.setText(hangGame.getHiddenWord());
        lblPoints.setText(getString(R.string.puntos, hangGame.getPoints()));
        lblLives.setText(getString(R.string.vidas, hangGame.getCurrentLives()));

    }

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

    private void updateCharSpinner(){

        String letras = hangGame.getCharsPosition(getPositionSelected());

        String[] chars = letras.split("(?!^)"); //DIVIDE LA CADENA POR CARACTERES

        charsArray.clear();
        charsArray.addAll(Arrays.asList(chars));
        charsAdapter.notifyDataSetChanged();

        charSpinner.setSelection(0);

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
                    Log.d("PRUEBA", "USER");
                    break;

            }

    }

    /**
     * Recibe los datos de la OptionsActivity y actualiza los datos de HangGame
     * @param intent Intent con los datos recibidos
     */
    private void receivedDataOptions(Intent intent){

        Bundle data = intent.getExtras();

        if(data !=null) {

            hangGame.setLives(data.getInt("LEVEL"));
            hangGame.setComodin(data.getBoolean("COMODIN"));

        }

    }

    private void receivedDataUser(Intent data){

        if(data !=null)
            if(data.getExtras() != null){

                Bundle bundle = data.getExtras();
                hangGame.setLives(bundle.getInt("LEVEL"));
                hangGame.setComodin(bundle.getBoolean("COMODIN"));

            }

    }

    /**
     * Posicion seleccionada en el spinner de posicion
     * @return Posicion seleccionada
     */
    private int getPositionSelected(){

        String position = positionSpinner.getSelectedItem().toString();

        return position.equals("*") ? -1 : (Integer.parseInt(position)-1);

    }

    /**
     * Guarda las opciones de HangGame en un fichero
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        writeOptionsFile();

        /*try{

            FileManager.writeHangGameOptions(this, hangGame);

        }catch (IOException e){
            System.err.println("FILE NOT FOUND");
        }*/

    }

}
