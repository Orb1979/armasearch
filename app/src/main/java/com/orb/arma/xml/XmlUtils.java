package com.orb.arma.xml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;

public class XmlUtils {
    /*
        uses HttpURLConnection to make Http request from Android to download the XML file
    */
    public static String getXmlFromUrl(String urlString) {

        StringBuffer output = new StringBuffer("");
        try {
            InputStream stream = null;
            java.net.URL url = new java.net.URL(urlString);
            URLConnection connection = url.openConnection();

            java.net.HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == java.net.HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return output.toString();
    }

}
