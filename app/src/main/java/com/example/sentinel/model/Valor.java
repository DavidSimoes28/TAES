package com.example.sentinel.model;

public class Valor {
    private String data;
    private int temperatura;
    private int humidade;


    public Valor(String data, int temperatura, int humidade) {
        this.data = data;
        this.temperatura = temperatura;
        this.humidade = humidade;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public void setHumidade(int humidade) {
        this.humidade = humidade;
    }

    public String getData() {
        return data;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public int getHumidade() {
        return humidade;
    }
}
