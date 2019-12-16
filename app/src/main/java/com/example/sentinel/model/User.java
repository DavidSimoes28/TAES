package com.example.sentinel.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class User {
    private String name;
    private String email;
    private String password;
    private List<String> favoritos;

    public User() {
        this.favoritos = new ArrayList<>();
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.favoritos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<String> favoritos) {
        this.favoritos = favoritos;
    }

    public void addFavorito(String favorite) {
        this.favoritos.add(favorite);
    }

    public void removeFavorito(String favorito) {
        this.favoritos.remove(favorito);
    }
}
