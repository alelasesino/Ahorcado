package com.alejandro.ahorcado;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int OPTIONS_ACTIVITY = 1, USER_ACTIVITY = 2;

    private HangGame hangGame = new HangGame();

    private TextView lblPlaying, lblHiddenWord, lblPoints, lblLives;
    private Spinner positionSpinner, charSpinner;
    private Button btPlay, btStart, btEnd, btPlayer, btOptions;

    private String playerName;
    private boolean waitStartGame = true;

    ArrayList<String> data = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerName = getString(R.string.default_player_name);

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

        lblPlaying.setText(getString(R.string.playing, playerName));
        lblPoints.setText(getString(R.string.puntos, 0));
        lblLives.setText(getString(R.string.vidas, 0));

        String[] l = new String[]{"A", "B", "C"};
        data.addAll(Arrays.asList(l));

        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, data);
        charSpinner.setAdapter(adapter);

    }

    /**
     * Inserta el caracter en el juego
     * @param v
     */
    private void onClickPlay(View v){

        String[] r = new String[]{"H", "R", "J"};
        data.clear();
        data.addAll(Arrays.asList(r));

        //adapter.clear();
        //charSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, new String[]{"R", "C", "W"}));
        adapter.notifyDataSetChanged();

    }

    /**
     * Inicia la palabra al juego y actualiza la interfaz
     * @param v
     */
    private void onClickStart(View v){

        hangGame.setHiddenWord("Alejandro");

        waitStartGame = false;
        changeState();
        updateDataGUI();

    }

    private void onClickEnd(View v){

        waitStartGame = true;
        changeState();

    }

    private void onClickPlayer(View v){

        Intent intent = new Intent(this, UserActivity.class);
        startActivityForResult(intent, USER_ACTIVITY);

    }

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

        lblHiddenWord.setText(hangGame.getHiddenWord());
        lblPoints.setText(getString(R.string.puntos, hangGame.getPoints()));
        lblLives.setText(getString(R.string.vidas, hangGame.getCurrentLives()));

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

}
