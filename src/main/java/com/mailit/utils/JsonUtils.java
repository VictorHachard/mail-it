package com.mailit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class provides utility methods for working with JSON objects.
 */
public class JsonUtils {

    /**
     * Converts a JSONObject to a Map.
     *
     * @param object The JSONObject to convert.
     * @return A Map containing the key-value pairs in the JSONObject.
     */
    public static Map<String, String> toMap(JSONObject object) {
        Map<String, String> map = new HashMap<String, String>();
        if (object != null && object.size() > 0) {
            for (String key : (Iterable<String>) object.keySet()) {
                map.put(key, (String) object.get(key));
            }
        }
        return map;
    }

    /**
     * Converts a JSONObject to a Map.
     *
     * @param array The JSONArray to convert.
     * @return A List containing the value in the JSONArray.
     */
    public static List<String> toList(JSONArray array) {
        List<String> list = new ArrayList<>();
        if (array != null && array.size() > 0) {
            for (Object value: array) {
                list.add((String) value);
            }
        }
        return list;
    }

}