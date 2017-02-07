/*
 * Copyright (C) 2017 Christian Rivera
 *
 */

package com.christian.domaindemo;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.Scanner;

/**
 * JSONArray, JSONObject are part of the Android SDK and these two can't be mocked
 * or included in Unit tests and running instrumented tests would have too much
 * overhead to be worth it; also roboelectric seems to be the best option atm.
 * */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class PropertyDataTest {

    public String[] stringKeys = {
            "AgencyLogoUrl",
            "AgencyColour",
            "DisplayPrice",
            "DisplayableAddress",
            "TruncatedDescription",
            "RetinaDisplayThumbUrl",
            "SecondRetinaDisplayThumbUrl",
            "Description"
    };

    public String[] intKeys = {
            "AdId",
            "Bathrooms",
            "Bedrooms",
            "Carspaces",
            "IsElite"
    };

    public JSONArray getTestArray(String fileName) throws Exception {
        StringBuilder jsonData = new StringBuilder("");
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            jsonData.append(line).append("\n");
        }
        scanner.close();
        JSONArray result = new JSONArray(jsonData.toString());
        return result;
    }

    @Before
    public void setup() throws Exception {
        PropertyData.PropertyList = getTestArray("testData.json");
    }

    @Test
    public void LookupTest() throws Exception {

        for (int i = 0; i<PropertyData.getItemCount(); i++) {
            int j;
            for (j = 0; j < stringKeys.length; j++) {
                assert(PropertyData.getString(i, stringKeys[j]) != null);
            }
            for (j = 0; j < intKeys.length; j++) {
                assert(PropertyData.getUint(i, intKeys[j]) > -1);
                PropertyData.getInt(i, intKeys[j]); // already throws an exception
            }
        }
    }

    @Test
    public void FavoritesAddTest() throws Exception {
        for (int i = 0; i<PropertyData.getItemCount(); i++) {
            assert(PropertyData.addToFavorites(i));
            assert(PropertyData.isInFavorites(i));
        }
    }

    @Test
    public void RemoveFavoritesTest() throws Exception {
        for (int i = 0; i<PropertyData.getItemCount(); i++) {
            assert(PropertyData.removeFromFavorites(i));
            assert(!PropertyData.isInFavorites(i));
        }
    }

}