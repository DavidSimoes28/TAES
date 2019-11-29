package com.example.sentinel.model;

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

    public User getUser(String email) {
        for (User user : users) {
            if(user.getEmail().equals(email)){
                return user;
            }
        }
        return null;
    }
}
