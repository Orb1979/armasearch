package com.orb.arma.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import com.orb.arma.beans.Search;
import java.util.ArrayList;

/**
 * load and saveHistory an ArrayList with Objects to Shared preferences
 * (avoiding putStringSet() its sdk11+)
 * (Not fit for Utils class because ArrayList holds objects and therefore it has specific implementation and typecasting)
 */
public class SharedPrefAdapter {
    private final static String SEARCHNAME = "_name";
    private final static String PLAYERS = "_playerCount";
    private final static String COUNTRY = "_country";
    private final static String DEDICATED = "_dedicatedOnly";
    private final static String FRIENDNAME = "_name";
    private final static String SIZE = "_size";

    public static boolean saveHistory(Context context, ArrayList<Search> arrayList) {
        SharedPreferences sp = context.getSharedPreferences("history", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        //saveHistory sharedPref
        for(int i=0;i<arrayList.size();i++){
            //remove old
            edit.remove(SEARCHNAME + i);
            edit.remove(PLAYERS + i);
            edit.remove(COUNTRY + i);
            edit.remove(DEDICATED + i);
            //insert new
            Search search = arrayList.get(i);
            edit.putString(SEARCHNAME + i, search.getName());
            edit.putInt(PLAYERS + i   , search.getPlayerCount());
            edit.putString(COUNTRY + i   , search.getCountry());
            edit.putBoolean(DEDICATED + i , search.getDedicatedOnly());
        }
        //saveHistory size
        edit.putInt(SIZE, arrayList.size());

        //return true if succeeded
        return edit.commit();
    }

    public static void loadHistory(Context context, ArrayList<Search> arrayList) {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sp = context.getSharedPreferences("history", Context.MODE_PRIVATE);

        //clear the old
        arrayList.clear();

        //get size
        int size = sp.getInt(SIZE, 0);

        //load sharedPref
        for(int i=0;i<size;i++){
            Search search = new Search(
                    sp.getString(SEARCHNAME + i, null),
                    sp.getInt(PLAYERS + i, 0),
                    sp.getString(COUNTRY + i, null),
                    sp.getBoolean(DEDICATED + i, false)
            );

            //add it to the data source
            arrayList.add(search);
        }
    }

    public static boolean saveFriendList(Context context, ArrayList<String> arrayList) {
        SharedPreferences sp = context.getSharedPreferences("friends", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();

        //remove old / insert new
        for(int i=0;i<arrayList.size();i++){
            edit.remove(FRIENDNAME + i);
            edit.putString(FRIENDNAME + i, arrayList.get(i));

        }
        //save Friendlist size
        edit.putInt(SIZE, arrayList.size());

        //return true if succeeded
        return edit.commit();
    }

    public static void loadFriendList(Context context, ArrayList<String> arrayList) {
        SharedPreferences sp = context.getSharedPreferences("friends", Context.MODE_PRIVATE);

        //clear the old
        arrayList.clear();

        //get size
        int size = sp.getInt(SIZE, 0);

        //load sharedPref
        for(int i=0;i<size;i++){
            String name =  sp.getString(SEARCHNAME + i, null);
            arrayList.add(name);
        }
    }
}
