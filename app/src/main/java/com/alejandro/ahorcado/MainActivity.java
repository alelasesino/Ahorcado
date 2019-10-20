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

    private TextView lblPlaying, lblHiddenWord, lblPoints, lblLives;
    private Spinner positionSpinner, charSpinner;
    private Button btPlay, btStart, btEnd, btPlayer, btOptions;

    private boolean waitStartGame = true;
    private HangGame hangGame = new HangGame();

    ArrayList<String> data = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hangGame.setLives(DifficultyEnum.NORMAL.getLives());

        initializeComponents();
        changeState();

    }

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

        lblPlaying.setText(getString(R.string.playing, getString(R.string.default_player_name)));
        lblPoints.setText(getString(R.string.puntos, 0));
        lblLives.setText(getString(R.string.vidas, 0));

        String[] l = new String[]{"A", "B", "C"};
        data.addAll(Arrays.asList(l));

        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, data);
        charSpinner.setAdapter(adapter);

    }

    private void onClickPlay(View v){

        String[] r = new String[]{"H", "R", "J"};
        data.clear();
        data.addAll(Arrays.asList(r));

        //adapter.clear();
        //charSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, new String[]{"R", "C", "W"}));
        adapter.notifyDataSetChanged();

    }

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
        intent.putExtra("COMODIN", true);
        startActivityForResult(intent, OPTIONS_ACTIVITY);

    }

    private void changeState(){

        positionSpinner.setEnabled(!waitStartGame);
        charSpinner.setEnabled(!waitStartGame);

        btPlay.setEnabled(!waitStartGame);
        btStart.setEnabled(waitStartGame);
        btEnd.setEnabled(!waitStartGame);
        btPlayer.setEnabled(waitStartGame);
        btOptions.setEnabled(waitStartGame);

    }

    private void updateDataGUI(){

        lblHiddenWord.setText(hangGame.getHiddenWord());
        lblPoints.setText(getString(R.string.puntos, hangGame.getPoints()));
        lblLives.setText(getString(R.string.vidas, hangGame.getCurrentLives()));

    }

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
     * @param data Datos recibidos
     */
    private void receivedDataOptions(Intent data){

        if(data !=null)
            if(data.getExtras() != null){

                Bundle bundle = data.getExtras();
                hangGame.setLives(bundle.getInt("LEVEL"));

            }

    }

}
