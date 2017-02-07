/*
 * Data class storing data fetch from a server containing a list
 * of properties displayed by the app
 *
 * Copyright (C) 2017 Christian Rivera
 *
 */

package com.christian.domaindemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PropertyData {

    static JSONArray PropertyList;
    static List<Integer> Favorites = new ArrayList<Integer>();

    private static JSONObject curObject = null;
    private static int curObjectIndex = -1;

    /**
     * Returns number of items held; if list container
     * is not allocated, -1
     *
     * @return number of items held;
     */
    public static int getItemCount() {
        if (PropertyList == null) return -1;
        return PropertyList.length();
    }

    /**
     * Store the item at {@link int} index into internal storage; useful for
     * subsequent value fetches at the same index. Returns true if
     * successful or already stored
     *
     * @param  index index of item in question
     * @return       result if storing item is successful
     */
    private static boolean storeCurrentItem(int index) {
        if (PropertyList == null || index > getItemCount() || index < 0) return false;
        if (index != curObjectIndex) {
            curObjectIndex = index;
            try {
                curObject = PropertyList.getJSONObject(index);
            } catch (JSONException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get string value item at {@link int} index using key {@link String} key.
     * Returns null if string value does not exist. For better performance,
     * all subsequent calls should use the same {@link int} index.
     *
     * @param  index   index of item from value to get from
     * @param  key     key of item to get from item at {@link int}
     * @return String  String value if successful; null if not
     */
    public static String getString(int index, String key) {
        if (storeCurrentItem(index)) {
            try {
                return curObject.getString(key);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Get non-negative integer item at {@link int} index using key
     * {@link String} key. Returns -1 if value does not exist. For
     * better performance, all subsequent calls should use the same
     * {@link int} index
     *
     * @param  index   index of item from value to get from
     * @param  key     key of item to get from item at {@link int} index
     * @return int     Non-negative value if successful; -1 if not
     */
    public static int getUint(int index, String key) {
        if (storeCurrentItem(index)) {
            try {
                return curObject.getInt(key);
            } catch (JSONException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Get  integer item at {@link int} index using key {@link String} key.
     * Throws NoSuchElementException if {@link String} key is not a valid key.
     *
     * @param  index   index of item from value to get from
     * @param  key     key of item to get from item at {@link int} index
     * @return int     Non-negative value if successful; -1 if not
     */
    public static int getInt(int index, String key) throws NoSuchElementException{
        if (storeCurrentItem(index)) {
            try {
                return curObject.getInt(key);
            } catch (JSONException e) {
                throw new NoSuchElementException();
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Add item at {@link int} index  onto the favorites list
     *
     * @param  index index of item to be added to favorites
     * @return       result if adding to list is successful or
     *               in favorites already; false if index is invalid
     */
    public static boolean addToFavorites(int index) {
        if (index < 0 || index > getItemCount()) return false;
        if (Favorites.lastIndexOf(index) > -1) return true;
        Favorites.add(index);
        return true;
    }

    /**
     * Remove item at {@link int} value from the favorites list
     *
     * @param  index index of item to be removed
     * @return       result if removing from list is successful;
     *               false if index is invalid or not in list
     */
    public static boolean removeFromFavorites(int index) {
        if (index < 0 || index > getItemCount()) return false;

        // 'index here' is a value; we need its index in new list favorites
        int x = Favorites.lastIndexOf(index);
        if (x == -1) return false;
        Favorites.remove(x);
        return true;
    }

    /**
     * Checks if {@link int} index is in favorites list or not
     *
     * @param  index index of item
     * @return       if in favorites list or not
     */
    public static boolean isInFavorites(int index) {
        return Favorites.contains(index);
    }
}
