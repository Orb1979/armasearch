package com.orb.arma.beans;

import android.util.Log;

import java.io.Serializable;

public class PlayerDetail implements Serializable {
    public String name;
    public boolean isFriend = false;
    public String deaths;
    public String score;
    public boolean active = false;

    public void setDisconnectTime(String strDate){
        // if a player does not have a disconnect time, that player is still on the server (active)
        active = (strDate.length() == 0);
    }
}
