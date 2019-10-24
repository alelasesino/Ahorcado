package com.alejandro.ahorcado.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.model.Player;

import java.util.Arrays;
import java.util.Comparator;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private Player[] players;
    private EditText txtUser;
    private RecyclerView listScore;
    private Button btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        players = new Player[5];
        players[0] = new Player("Alejandro", 2);
        players[1] = new Player("Angel", 5);
        players[2] = new Player("Pedro", 2);
        players[3] = new Player("Julian", 7);
        players[4] = new Player("Manuel", 10);

        Arrays.sort(players, (player1, player2) -> player2.getPoints() - player1.getPoints());

        initializeComponents();

    }

    private void initializeComponents(){

        txtUser = findViewById(R.id.txtUser);
        listScore = findViewById(R.id.listScore);
        btBack = findViewById(R.id.btBack);

        //AÑADE UNA LINEA HORIZONTAL ENTRE CADA ITEM
        listScore.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listScore.setLayoutManager(new LinearLayoutManager(this));
        listScore.setAdapter(new PlayerAdapter(players));

        btBack.setOnClickListener(this);

        receivedDataUser();

    }

    @Override
    public void onClick(View view) {

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

}
