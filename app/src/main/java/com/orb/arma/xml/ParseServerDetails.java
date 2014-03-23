package com.orb.arma.xml;

import android.util.Log;

import com.orb.arma.adapters.FriendAdapter;
import com.orb.arma.beans.PlayerDetail;
import com.orb.arma.beans.Server;
import com.orb.arma.beans.ServerDetail;
import com.orb.arma.utils.WildCardStringFinder;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ParseServerDetails {
    private String tempVal = "";

    public ServerDetail parse(InputStream is, Server server) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        ServerDetail serverDetail = new ServerDetail();
        serverDetail.players =  new ArrayList<PlayerDetail>();
        PlayerDetail player = null;

        // The xml has 2 different <players> tags
        // a player container tag with all the player info
        // a tag with the player count
        // we use this boolean to distinguish the two and skip unneeded checks
        boolean inPlayerContainerTag = false;

        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("player")) {
                            inPlayerContainerTag = true;
                            player = new PlayerDetail();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        tempVal = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(!inPlayerContainerTag){
                            if (tagname.equalsIgnoreCase("host")) {
                                serverDetail.host = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("port")) {
                                serverDetail.port = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("state")) {
                                serverDetail.state = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("mission")) {
                                serverDetail.mission = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("island")) {
                                serverDetail.island = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("players")) {
                                serverDetail.playerCount = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("passworded")) {
                                serverDetail.passworded = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("dedicated")) {
                                serverDetail.dedicated = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("start")) {
                                serverDetail.startTime = tempVal;
                            }
                            else if (tagname.equalsIgnoreCase("max_players")) {
                                //This is not the maximum slots of the players
                                //this is the maximum active players that where / are on the server
                                serverDetail.maxPlayers = tempVal;
                            }
                        }
                        else {
                           if (tagname.equalsIgnoreCase("player")) {

                               //add only the active the player to he playerList
                               if(player.active){

                                   // we need to do a wildcard comparison again, to highlight the friends
                                   // this is because we dont want to keep track off all players on all servers
                                   // and this check is only for 1 server, so should go pretty fast

                                   // we could limit this check if the user does a friend check in the serverlist
                                   //if(server.hasFriends) {

                                   //check for a match in the friendlist
                                   String playerName = player.name.toLowerCase();
                                   server.hasFriends = false;

                                   for (String friend : FriendAdapter.friends) {
                                       String searchString = friend.toLowerCase();

                                       WildCardStringFinder finder = new WildCardStringFinder();
                                       if (finder.isStringMatching(playerName, searchString)) {
                                           serverDetail.hasFriends = true;
                                           player.isFriend = true;
                                       }

                                   }

                                   serverDetail.players.add(player);
                               }
                           }

                           else if (tagname.equalsIgnoreCase("disconnect_time")) {
                               //!important getText() adds empty buffer or whitespace
                               String str = tempVal.replaceAll("\\s","");
                               player.setDisconnectTime(str);
                           }
                           else if (tagname.equalsIgnoreCase("name")) {
                               player.name = tempVal;
                           }
                           else if (tagname.equalsIgnoreCase("deaths")) {
                               player.deaths = tempVal;
                           }
                           else if (tagname.equalsIgnoreCase("score")) {
                               player.score = tempVal;
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

        return serverDetail;
    }

}