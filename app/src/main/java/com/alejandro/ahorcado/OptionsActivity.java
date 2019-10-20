package com.alejandro.ahorcado;

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

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup groupDificultad;
    private EditText txtLives;
    private CheckBox checkboxComodin;
    private Button btBack;

    private int receivedLives;
    private boolean textChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initializeComponents();

    }

    private void initializeComponents(){

        groupDificultad = findViewById(R.id.groupDificultad);
        txtLives = findViewById(R.id.txtLives);
        checkboxComodin = findViewById(R.id.checkboxComodin);
        btBack = findViewById(R.id.btBack);

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

        receivedLives = getIntent().getExtras().getInt("LEVEL");
        changeRadioGroup(receivedLives);
        txtLives.setText(String.valueOf(receivedLives));

    }

    private void changeRadioGroup(int lives){

        switch (lives){

            case 15:
                groupDificultad.check(R.id.radioEasy);
                break;

            case 10:
                groupDificultad.check(R.id.radioNormal);
                break;

            case 5:
                groupDificultad.check(R.id.radioHard);
                break;

            default:
                groupDificultad.clearCheck();

        }

    }

    @Override
    public void onClick(View view) {

        Intent resultIntent = new Intent();
        resultIntent.putExtra("LEVEL", getLevel());
        resultIntent.putExtra("COMODIN", checkboxComodin.isChecked());

        setResult(RESULT_OK, resultIntent);
        finish();

    }

    private int getLevel(){

        switch (groupDificultad.getCheckedRadioButtonId()){
            case R.id.radioEasy:
                return DifficultyEnum.EASY.getLives();
            case R.id.radioNormal:
                return DifficultyEnum.NORMAL.getLives();
            case R.id.radioHard:
                return DifficultyEnum.HARD.getLives();
            default:
                return getCustomNumLives();
        }

    }

    private int getCustomNumLives(){

        String strLives = txtLives.getText().toString();
        strLives = strLives.equals("") ? String.valueOf(receivedLives) : strLives;

        return Integer.parseInt(strLives);

    }

}
