package com.example.fitzone.common_functions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class StaticFunctions {

    public static int oldData = 0;

    //store day data
    public static void storeDayData(Activity activity, String dayName, int dayNumber) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("day_data", activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("day_name", dayName);
        editor.putInt("day_number", dayNumber);
        editor.apply();
    }

    //get day data
    public static HashMap<String, String> getDayData(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("day_data", activity.MODE_PRIVATE);

        String dayName = sharedPreferences.getString("day_name", "");

        String dayNumber = String.valueOf(sharedPreferences.getInt("day_number", 1));

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("day_name", dayName);
        hashMap.put("day_number", dayNumber);
        return hashMap;
    }

    //return baser url
    public static String getBaseUrl(Activity activity) {
        if (activity == null)
            return null;

        //get host data
        SharedPreferences serverDataFile = activity.getSharedPreferences("ipAndPort", activity.MODE_PRIVATE);
        String apiIP = serverDataFile.getString("ip", "192.168.1.3");
        String apiPort = serverDataFile.getString("port", "8000");
        return "http://" + apiIP + ':' + apiPort + "/api/";
    }

    //return host url
    public static String getHostUrl(Activity activity) {
        if (activity == null)
            return null;

        //get host data
        SharedPreferences serverDataFile = activity.getSharedPreferences("ipAndPort", activity.MODE_PRIVATE);
        String apiIP = serverDataFile.getString("ip", "192.168.1.3");
        String apiPort = serverDataFile.getString("port", "8000");
        return "http://" + apiIP + ':' + apiPort + "/";
    }

    //store api_token
    public static boolean storeApiToken(Activity activity, String apiToken) {
        try {//store apiToken
            SharedPreferences storeApiToken = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = storeApiToken.edit();
            editor.putString("apiToken", apiToken);
            editor.apply();
            return true;
        } catch (Exception e) {
            Log.d("Store api_token", e.getMessage());
            return false;
        }
    }

    //get stored api_token
    public static String getApiToken(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("apiToken", "");
    }

    //move to another activity
    protected void moveToNextActivity(Context thisContext, Class nextContext) {
        Intent intent = new Intent(thisContext, nextContext);
        thisContext.startActivity(intent);
    }
}
