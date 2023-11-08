package xyz.gothaj.webserverhandler.utils;

import java.util.ArrayList;

public class VariabileHandler {

    public static ArrayList<Variabile> handleArguments(String link){
        ArrayList<Variabile> variabiles = new ArrayList<>();
        String[] vars = link.split("&");

        for(String var : vars){
            String[] v = var.split("=");
            if(v.length != 2){
                return null;
            }

            variabiles.add(new Variabile(v[0], v[1]));
        }
        return variabiles;
    }

}
