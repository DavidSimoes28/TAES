package com.example.sentinel.model;


import java.util.LinkedList;
import java.util.List;

public class Sensor {
    private String Localizacao;
    private String Humidade;
    private String Data;
    private String Temperatura;
    private String Edificio;
    private List<String> Favoritos;


    public Sensor(String localizacao, String humidade, String data, String temperatura,String email) {
        Localizacao = localizacao;
        Humidade = humidade;
        Data = data;
        Temperatura = temperatura;
        Edificio = "A";
        Favoritos = new LinkedList<>();
        Favoritos.add(email);
    }

    public String getLocalizacao() {
        return Localizacao;
    }

    public void setLocalizacao(String localizacao) {
        Localizacao = localizacao;
    }

    public String getHumidade() {
        return Humidade;
    }

    public void setHumidade(String humidade) {
        Humidade = humidade;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getTemperatura() {
        return Temperatura;
    }

    public void setTemperatura(String temperatura) {
        Temperatura = temperatura;
    }

    public String getEdificio() {
        return Edificio;
    }

    public void setEdificio(String edificio) {
        Edificio = edificio;
    }

    public List getFavoritos() {
        return Favoritos;
    }

    public void setFavoritos(List favoritos) {
        Favoritos = favoritos;
    }
}
