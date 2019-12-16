package com.example.sentinel.model;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SensorUp {
    private int id;
    private String localizacao;
    private String humidade;
    private String data;
    private String temperatura;
    private String edificio;
    private List<Favoritos> favoritos;


    public SensorUp(int id, String localizacao, String humidade, String data, String temperatura, Favoritos email) {
        this.id = id;
        this.localizacao = localizacao;
        this.humidade = humidade;
        this.data = data;
        this.temperatura = temperatura;
        edificio = "A";
        favoritos = new LinkedList<>();
        favoritos.add(email);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Favoritos> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<Favoritos> favoritos) {
        this.favoritos = favoritos;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getHumidade() {
        return humidade;
    }

    public void setHumidade(String humidade) {
        this.humidade = humidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }


}
