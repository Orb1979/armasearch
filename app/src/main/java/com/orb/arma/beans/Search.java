package com.orb.arma.beans;
import android.content.Context;
import android.util.Log;
import com.orb.arma.R;
import com.orb.arma.utils.Utils;

public class Search extends Exception {
    private String name;
    private int playerCount;
    private String country;
    private Boolean dedicatedOnly;
    private static String searchUrlPrefix;
    private static String searchUrlPostfix;

    public Search(String name, int playerCount, String country, Boolean dedicatedOnly){
        this.name = name;
        this.playerCount = playerCount;
        this.country = country;
        this.dedicatedOnly = dedicatedOnly;
   }

    public static void setUrlInfo(String searchUrlPrefixString, String searchUrlPostfixString){
        searchUrlPrefix = searchUrlPrefixString;
        searchUrlPostfix = searchUrlPostfixString;
        // not using &state_down=1
     }

    public String getSearchURL(){
       String str = searchUrlPrefix + "?nquery=" + Utils.urlEncode(name);
       if(dedicatedOnly){
           str += "&filter_dedicated=1";
       }if(!country.equals("")){
           str += "&country=" + country;
       }
       str += searchUrlPostfix;

       return str;
    }

    //-- Getters

    public String getName() {
        return name;
    }

    public Boolean getDedicatedOnly() {
        return dedicatedOnly;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getCountry() {
        return country;
    }
}
