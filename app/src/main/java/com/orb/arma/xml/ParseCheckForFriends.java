package com.orb.arma.xml;

import android.util.Log;
import com.orb.arma.adapters.FriendAdapter;
import com.orb.arma.adapters.ServerListAdapter;
import com.orb.arma.beans.Server;
import com.orb.arma.utils.WildCardStringFinder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;

public class ParseCheckForFriends {
    private String tempVal;

    public ServerListAdapter parse(InputStream is, Server aServer, ServerListAdapter adapter) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();

            boolean foundAfriend = false;
            boolean playerIsActive = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("player")) {
                            //start of player tag, reset booleans
                            foundAfriend = false;
                            playerIsActive = false;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        tempVal = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                            //check name
                            if (tagname.equalsIgnoreCase("name")) {

                                //check for a match in the friendlist
                                String playerName = tempVal.toLowerCase();
                                for(String friend : FriendAdapter.friends){

                                    String searchString = friend.toLowerCase();
                                    WildCardStringFinder finder = new WildCardStringFinder();

                                    if(finder.isStringMatching(playerName, searchString)){
                                        foundAfriend = true;
                                    }
                                }
                            }

                            //check player is online
                            else if(tagname.equalsIgnoreCase("disconnect_time")){
                                if(foundAfriend){
                                    if(tempVal.replaceAll("\\s","").length() == 0){
                                        playerIsActive = true;
                                    }
                                }
                            }

                            //end of the player tag
                            else if (tagname.equalsIgnoreCase("player")) {

                                //If we found a matching friend that is online, flag the server with hasfriends
                                if(foundAfriend && playerIsActive){
                                    aServer.hasFriends = true;

                                    // we exit the friend search for this server, with the first active friend we found
                                    // We only need to know if it has friends or not, (to highlight it in the serverlist)
                                    // this should speed up the search when we searching for friends in a lot of servers
                                    return adapter;
                                }
                            }

                     break;

                    default:
                        break;
                }

                //repeatedly call next() function to retrieve next event until the event is END_DOCUMENT
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return adapter;
    }
}