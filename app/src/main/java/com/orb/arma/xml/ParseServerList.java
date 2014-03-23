package com.orb.arma.xml;

import android.util.Log;

import com.orb.arma.beans.Search;
import com.orb.arma.beans.Server;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParseServerList {
    private List<Server> serverList;
    private String tempVal;

    public ParseServerList() {
        this.serverList = new ArrayList<Server>();
    }

    public List<Server> parse(InputStream is, Search currentSearch) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(is, null);

            Server server = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //(attribute values, most be collected at the START_TAG)
                        if (tagname.equalsIgnoreCase("server")) {
                            // create a new instance of server
                            server = new Server();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        tempVal = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(server != null){

                            if (tagname.equalsIgnoreCase("country")) {
                               server.setCountry(tempVal);
                            }
                            else if (tagname.equalsIgnoreCase("id")) {
                                server.setId(tempVal);
                            }
                            else if (tagname.equalsIgnoreCase("name")) {
                                server.setName(tempVal);
                            }
                            else if (tagname.equalsIgnoreCase("passworded")) {
                                server.setPassword(tempVal.equals("true"));
                            }
                            else if (tagname.equalsIgnoreCase("players")) {
                                server.setPlayers(tempVal);
                            }
                            else if (tagname.equalsIgnoreCase("state")) {
                                server.setState(tempVal);
                                // the xml has sometimes, shows a 1 player count on a server in waiting state
                                // you however cant retrieve the name, so I just set it to 0
                                if(tempVal.equals("waiting")){
                                    server.setPlayers(0);
                                }
                            }
                            // closing tag server, add the server to the list
                            else if (tagname.equalsIgnoreCase("server")) {
                                // minimum players in search filters is not set
                                if(currentSearch.getPlayerCount() == 0){
                                    serverList.add(server);
                                }
                                //check if the server has enough players
                                else{
                                    if(currentSearch.getPlayerCount() < server.getPlayers()){
                                        serverList.add(server);
                                    }
                                }
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

        return serverList;
    }
}