package com.orb.arma.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //pass a string and return a url encoded string
    public static String urlEncode(String value) {
        String encodedString = "";
        try {
            encodedString = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedString;
    }

    // convert a string date to a simpleDateFormat
    // format "yyyy-MM-dd HH:mm" for 2014-01-27 20:02
    public static Date strToData(String strDate, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // Hide the virtual keyboard
    // and clear the focus
    public static void hideKeyBoardAndFocus(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.clearFocus();
    }

    // Get a trimmed String from a view
    // validate on non existing view and minimum length
    public static String getStringFromView(EditText view, String defaultValue, int minLength){
        if(view.getText() != null){
            //get trimmed value
            String value = view.getText().toString().trim();

            if(value.length() >= minLength){
                return value;
            }else{
               return defaultValue;
            }
        }
        //no view, return null
        return null;
    }

    // Get a Integer from a view
    // parse the value to integer
    // validate on non existing view and minimum value
    public static Integer getIntegerFromView(EditText view, int defaultValue, int minValue){
        if(view.getText() != null){
            int value = defaultValue;

            //get trimmed value as int
            String str = view.getText().toString().trim();
            if( str.length() > 0){
                value = Integer.parseInt(str);
            }

            if(value >= minValue ){
                return value;
            }else{
                return defaultValue;
            }
        }
        //no view, return null
        return null;
    }


}
