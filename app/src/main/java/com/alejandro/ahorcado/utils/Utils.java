package com.alejandro.ahorcado.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static int getRandomNumber(int min, int max){
        return (int)(Math.random() * ( max - min )) + min;
    }

    public static String dateToString(Date date){
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    }

}
