package com.orb.arma.beans;
import java.util.ArrayList;
import java.util.List;

public class ServerDetail {
    public String state;
    public String host;
    public String port;

    public String playerCount;
    public String mission;
    public String island;
    public String passworded;
    public String dedicated;

    public String startTime;
    public String maxPlayers;

    public ArrayList<PlayerDetail> players;
    public Boolean hasFriends = false;

}
