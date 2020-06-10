package dev.tigr.kamiautoskidder.config;

import org.json.JSONArray;

/**
 * @author Tigermouthbear
 * Created on April 3, 2020
 */

public class ArrayConfig extends Config<JSONArray>
{
	public ArrayConfig(String name)
	{
		super(name, Type.ARRAY);
	}
}
