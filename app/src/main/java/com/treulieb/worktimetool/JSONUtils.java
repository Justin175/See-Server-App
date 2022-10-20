package com.treulieb.worktimetool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
    public static JSONArray getArray(JSONObject obj, String key) {
        if (obj == null)
            return null;

        try {
            return obj.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONObject obj, String key) {
        if (obj == null)
            return null;

        try {
            return obj.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(JSONObject obj, String key) {
        if (obj == null)
            return null;

        try {
            return obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean getBoolean(JSONObject obj, String key) {
        if (obj == null)
            return null;

        try {
            return obj.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] toArray(JSONArray jsonArray){
        return toArray(jsonArray, null);
    }

    public static String[] toArray(JSONArray jsonArray, String[] defaultArray){
        if(jsonArray == null)
            return defaultArray;

        String[] arr = new String[jsonArray.length()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = jsonArray.optString(i);

        return arr;
    }
}
