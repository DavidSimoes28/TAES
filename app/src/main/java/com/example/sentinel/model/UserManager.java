package com.example.sentinel.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public enum UserManager {
    INSTANCE;

    private List<User> users;

    UserManager() {
        this.users = new LinkedList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void updateUser(String email, User user) {
        for (User user1 : users) {
            if(user.getEmail().equals(email)){
                user1 = user;
            }
        }
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    public User getUser(String email) {
        for (User user : users) {
            if(user.getEmail().equals(email)){
                return user;
            }
        }
        return null;
    }

    public List<String> getFavorites(String email) {
        for (User user : users) {
            if(user.getEmail().equals(email)){
                return user.getFavoritos();
            }
        }
        return null;
    }
    public boolean isFavorito(String email,String fav) {
        for (User user : users) {
            if(user.getEmail().equals(email)){
                for (String favorito : user.getFavoritos()) {
                    if(favorito.equals(fav)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void addFavorite(String email,String localizacao) {
        for (User user : users) {
            if(user.getEmail().equals(email)){
                user.addFavorito(localizacao);
            }
        }
    }

    public void removeFavorite(String email,String localizacao) {
        for (User user : users) {
            if(user.getEmail().equals(email)){
                user.removeFavorito(localizacao);
            }
        }
    }
}
