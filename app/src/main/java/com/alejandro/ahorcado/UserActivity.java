package com.alejandro.ahorcado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtUser;
    private ListView listScore;
    private Button btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initializeComponents();

    }

    private void initializeComponents(){

        txtUser = findViewById(R.id.txtUser);
        listScore = findViewById(R.id.listScore);
        btBack = findViewById(R.id.btBack);

        btBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        Intent resultIntent = new Intent();
        resultIntent.putExtra("PLAYER_NAME", getPlayerName());

        setResult(RESULT_OK, resultIntent);
        finish();

    }

    private String getPlayerName(){

        String strName = txtUser.getText().toString();

        if(!strName.equals(""))
            return strName;

        return getString(R.string.default_player_name);

    }

}
