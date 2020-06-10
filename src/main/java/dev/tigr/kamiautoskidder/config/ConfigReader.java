package dev.tigr.kamiautoskidder.config;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Tigermouthbear
 * Created on April 3, 2020
 */

public class ConfigReader
{
	public static void read(File file)
	{
		try
		{
			JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));

			for(Config config: Config.getAll())
			{
				switch(config.getType())
				{
					case STRING:
						config.setValue(jsonObject.getString(config.getName()));
						break;

					case ARRAY:
						config.setValue(jsonObject.getJSONArray(config.getName()));
						break;

					case DICTIONARY:
						config.setValue(jsonObject.getJSONObject(config.getName()));
						break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
