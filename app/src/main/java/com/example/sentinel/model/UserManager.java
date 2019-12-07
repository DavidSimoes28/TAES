package com.example.sentinel.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;

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

    public void addUser(User user) {
        this.users.add(user);
    }
    public void removeUser(User user) {
        this.users.remove(user);
    }

    public void setFirebaseUser(String email, FirebaseUser firebaseUser){
        for (User user : users) {
            if(user.getEmail().equals(email)){
                user.setUser(firebaseUser);
            }
        }
    }

    public FirebaseUser getFirebaseUser(String email){
        for (User user : users) {
            if(user.getEmail().equals(email)){
                return user.getUser();
            }
        }
        return null;
    }

    public User getUser(String email) {
        for (User user : users) {
            if(user.getEmail().equals(email)){
                return user;
            }
        }
        return null;
    }
}
