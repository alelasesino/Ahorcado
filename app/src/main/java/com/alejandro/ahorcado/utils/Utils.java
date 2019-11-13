package com.alejandro.ahorcado.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clase que contiene funcionalidades utiles para toda la aplicacion
 */
public class Utils {

    /**
     * Genera un numero aleatorio entre el numero maximo y minimo
     * @param min Numero minimo
     * @param max Numero maximo
     * @return Numero aleatorio
     */
    public static int getRandomNumber(int min, int max){
        return (int)(Math.random() * (max - min)) + min;
    }

    /**
     * Convierte una fecha en una cadena con formato dd/MM/yyyy
     * @param date Fecha a convertir
     * @return Fecha como cadena con formato dd/MM/yyyy
     */
    public static String dateToString(Date date){
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    }

}
