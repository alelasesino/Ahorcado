package com.alejandro.ahorcado.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.model.Player;
import com.alejandro.ahorcado.utils.FileManager;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Player> players;
    private EditText txtUser;

    private RecyclerView recyclerScore;
    private PlayerAdapter adapterScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initializePlayers();
        initializeToolbar();
        initializeComponents();

    }

    /**
     * Lee las datos de los jugadores del fichero y ordena los jugadores
     * por puntuacion de forma descendente
     */
    private void initializePlayers(){

        Thread thread = new Thread(() ->{

            try {

                players = FileManager.readHangGamePlayers(this);

            } catch (Exception e) {
                players = new ArrayList<>();
            }

        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException ignored) {}

        players.sort((player1, player2) -> player2.getPoints() - player1.getPoints());

    }

    /**
     * Establece el titulo y el icono a la toolbar de la activity
     */
    private void initializeToolbar(){

        ActionBar toolbar = getSupportActionBar();

        if(toolbar != null){
            toolbar.setDisplayShowHomeEnabled(true);
            toolbar.setIcon(R.drawable.ic_scoreboard);
            toolbar.setTitle("   " + getString(R.string.user_title));
        }

    }

    /**
     * Inicializa los componentes del XML
     */
    private void initializeComponents(){

        txtUser = findViewById(R.id.txtUser);
        recyclerScore = findViewById(R.id.listScore);
        Button btBack = findViewById(R.id.btBack);

        btBack.setOnClickListener(this);

        initializeRecyclerView();
        receivedDataUser();

    }

    /**
     * Inicializa el recyclerView añadiendole un asistente tactil que permite controlar
     * eventos tactiles de las cardviews
     */
    private void initializeRecyclerView(){

        recyclerScore.setHasFixedSize(true);
        recyclerScore.setLayoutManager(new LinearLayoutManager(this));

        adapterScore = new PlayerAdapter(players);
        recyclerScore.setAdapter(adapterScore);

        //ASISTENTE QUE PERMITE ARRASTRAR LOS CARDVIEWS
        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(1000, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapterScore.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerScore);

        adapterScore.setOnItemClickListener(this::onItemClick);

    }

    /**
     * Establece el nombre de la cardview cliqueada en el campo de texto del nombre del jugador
     * @param player Jugador seleccionado
     */
    private void onItemClick(Player player){
        txtUser.setText(player.getName());
    }

    /**
     * Evento click del boton de volver
     * @param view Vista
     */
    @Override
    public void onClick(View view) {
        closeActivity();
    }

    /**
     * Envia el nombre del jugador al MainActivity
     * y finaliza esta activity
     */
    private void closeActivity(){

        Intent resultIntent = new Intent();
        resultIntent.putExtra("PLAYER_NAME", getPlayerName());

        setResult(RESULT_OK, resultIntent);
        finish();

    }

    /**
     * Recibe el nombre del jugador de la MainActivity
     */
    private void receivedDataUser(){

        Bundle data = getIntent().getExtras();

        if(data != null)
            txtUser.setText(data.getString("PLAYER_NAME"));

    }

    /**
     * Devuelve el nombre del jugador del campo de texto, si no se introdujo ningun
     * nombre devuelve el nombre por defecto
     * @return Nombre del jugador
     */
    private String getPlayerName(){

        String strName = txtUser.getText().toString();

        if(!strName.equals(""))
            return strName;

        return getString(R.string.default_player_name);

    }

    /**
     * Escribe los nombres de los jugadores en el fichero de jugadores
     */
    @Override
    protected void onStop() {
        super.onStop();

        new Thread(() ->{

            try {

                FileManager.writeHangGamePlayer(this, players);

            } catch (Exception ignored) {}

        }).start();

    }

}
