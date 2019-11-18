package com.alejandro.ahorcado.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.alejandro.ahorcado.R;
import com.alejandro.ahorcado.model.HangGame;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup groupDificultad;
    private EditText txtLives;
    private CheckBox checkboxComodin;

    private int receivedLives;
    private boolean textChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initializeToolbar();
        initializeComponents();

    }

    /**
     * Establece el titulo y el icono a la toolbar de la activity
     */
    private void initializeToolbar(){

        ActionBar toolbar = getSupportActionBar();

        if(toolbar != null){
            toolbar.setDisplayShowHomeEnabled(true);
            toolbar.setIcon(R.drawable.ic_settings);
            toolbar.setTitle("   " + getString(R.string.options_title));
        }

    }

    /**
     * Inicializa los componentes XML
     */
    private void initializeComponents(){

        groupDificultad = findViewById(R.id.groupDificultad);
        txtLives = findViewById(R.id.txtLives);
        checkboxComodin = findViewById(R.id.checkboxComodin);
        Button btBack = findViewById(R.id.btBack);

        groupDificultad.setOnCheckedChangeListener((radioGroup, i) -> {

            if(!textChanged)
                txtLives.setText(String.valueOf(getLevel()));

        });

        txtLives.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                textChanged = true;
                changeRadioGroup(getCustomNumLives());
                textChanged = false;

            }

            @Override
            public void afterTextChanged(Editable editable) {}

        });

        btBack.setOnClickListener(this);

        receivedDataOptions();

        changeRadioGroup(receivedLives);
        txtLives.setText(String.valueOf(receivedLives));

    }

    /**
     * Cambia la seleccion del los radios buttons de la dificultad si las vidas pasadas por
     * parametro coincide con las vidas dde alguna de las dificultades de los radio buttons
     * en caso de no coincidan con ninguna dificultad, deselecciona todos los radio buttons
     * @param lives Vidas de la dificultad
     */
    private void changeRadioGroup(int lives){

        switch (lives){

            case HangGame.EASY_MODE:
                groupDificultad.check(R.id.radioEasy);
                break;

            case HangGame.NORMAL_MODE:
                groupDificultad.check(R.id.radioNormal);
                break;

            case HangGame.HARD_MODE:
                groupDificultad.check(R.id.radioHard);
                break;

            default:
                groupDificultad.clearCheck();

        }

    }

    /**
     * Envia el numero de vidas y el estado del comodin al MainActivity
     * y finaliza esta activity
     * @param view Vista
     */
    @Override
    public void onClick(View view) {

        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.ARG_LEVEL, getLevel());
        resultIntent.putExtra(MainActivity.ARG_COMODIN, checkboxComodin.isChecked());

        setResult(RESULT_OK, resultIntent);
        finish();

    }

    /**
     * Devuelve el numero de vidas indicadas por el usuario ya sean a traves de los radio buttons
     * o escrita en el campo de texto
     * @return Numero de vidas
     */
    private int getLevel(){

        switch (groupDificultad.getCheckedRadioButtonId()){

            case R.id.radioEasy:
                return HangGame.EASY_MODE;

            case R.id.radioNormal:
                return HangGame.NORMAL_MODE;

            case R.id.radioHard:
                return HangGame.HARD_MODE;

            default:
                return getCustomNumLives();

        }

    }

    /**
     * Devuelve el numero de vidas personalizada escrita en el campo de texto
     * @return Numero de vidas personalizadas
     */
    private int getCustomNumLives(){

        String strLives = txtLives.getText().toString();
        strLives = strLives.equals("") ? String.valueOf(receivedLives) : strLives;

        return Integer.parseInt(strLives);

    }

    /**
     * Recibe el numero de vidas y el estado del comodin de la MainActivity
     */
    private void receivedDataOptions(){

        Bundle data = getIntent().getExtras();

        if(data != null){

            receivedLives = data.getInt("LEVEL");
            checkboxComodin.setChecked(data.getBoolean("COMODIN"));

        }

    }

}
