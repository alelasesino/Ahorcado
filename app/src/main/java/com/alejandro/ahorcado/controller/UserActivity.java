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

        /*players = new ArrayList<>();
        players.add(new Player("Alejandro", 2));
        players.add(new Player("Angel", 5));
        players.add(new Player("Pedro", 2));
        players.add(new Player("Julian", 7));
        players.add(new Player("Manuel", 10));*/

        players.sort((player1, player2) -> player2.getPoints() - player1.getPoints());

    }

    private void initializeToolbar(){

        ActionBar toolbar = getSupportActionBar();

        if(toolbar != null){
            toolbar.setDisplayShowHomeEnabled(true);
            toolbar.setIcon(R.drawable.ic_scoreboard);
            toolbar.setTitle("   " + getString(R.string.user_title));
        }

    }

    private void initializeComponents(){

        txtUser = findViewById(R.id.txtUser);
        recyclerScore = findViewById(R.id.listScore);
        Button btBack = findViewById(R.id.btBack);

        btBack.setOnClickListener(this);

        initializeRecyclerView();

        receivedDataUser();

    }

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

    private void onItemClick(Player player){
        txtUser.setText(player.getName());
    }

    @Override
    public void onClick(View view) {
        closeActivity();
    }

    private void closeActivity(){

        Intent resultIntent = new Intent();
        resultIntent.putExtra("PLAYER_NAME", getPlayerName());

        setResult(RESULT_OK, resultIntent);
        finish();

    }

    private void receivedDataUser(){

        Bundle data = getIntent().getExtras();

        if(data != null)
            txtUser.setText(data.getString("PLAYER_NAME"));

    }

    private String getPlayerName(){

        String strName = txtUser.getText().toString();

        if(!strName.equals(""))
            return strName;

        return getString(R.string.default_player_name);

    }

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
