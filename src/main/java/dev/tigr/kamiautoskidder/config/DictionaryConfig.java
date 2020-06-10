package dev.tigr.kamiautoskidder.config;

import org.json.JSONObject;

/**
 * @author Tigermouthbear
 * Created on April 3, 2020
 */

public class DictionaryConfig extends Config<JSONObject> {
    public DictionaryConfig(String name) {
        super(name, Type.DICTIONARY);
    }
}
