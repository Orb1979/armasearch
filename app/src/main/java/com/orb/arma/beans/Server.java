package com.orb.arma.beans;

import java.io.Serializable;

public class Server implements Serializable{
    private String id;
    private String name;
    private Boolean password; //Boolean?
    private Integer players;
    private String country;
    private String state;
    public Boolean hasFriends = false;

    //id
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    //name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //password
    public Boolean getPassword() {
        return password;
    }
    public void setPassword(Boolean password) {
        this.password = password;
    }

    //country
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    //players
    public int getPlayers() {
        return players;
    }
    public String getPlayersString() {
        return players.toString();
    }
    public void setPlayers(int value) {
        this.players = value;
    }

    public void setPlayers(String value) {
        this.players = Integer.parseInt(value);
    }

    //state
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}

