package com.infernostats.ironmanmuter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class IronmanMuterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(IronmanMuterPlugin.class);
		RuneLite.main(args);
	}
}