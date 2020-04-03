package me.tigermouthbear.kamiautoskidder.config;

import java.util.ArrayList;

/**
 * @author Tigermouthbear
 * Created on April 3, 2020
 */

public class Config<T>
{
	private static ArrayList<Config> configs = new ArrayList<>();

	private String name;
	private Type type;
	private T value;

	public Config(String name, Type type)
	{
		this.name = name;
		this.type = type;

		configs.add(this);
	}

	enum Type
	{
		ARRAY,
		STRING,
		DICTIONARY
	}

	public String getName()
	{
		return name;
	}

	public Type getType()
	{
		return type;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	public static ArrayList<Config> getAll()
	{
		return configs;
	}
}
