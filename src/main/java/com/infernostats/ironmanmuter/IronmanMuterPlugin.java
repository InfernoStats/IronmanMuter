package com.infernostats.ironmanmuter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
	name = "Ironman Muter"
)
public class IronmanMuterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private IronmanMuterConfig config;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event) {
		if (!"chatFilterCheck".equals(event.getEventName())) {
			return;
		}

		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();

		final int messageType = intStack[intStackSize - 2];
		final int messageId = intStack[intStackSize - 1];

		ChatMessageType chatMessageType = ChatMessageType.of(messageType);
		final MessageNode messageNode = client.getMessages().get(messageId);
		final String playerName = messageNode.getName();

		switch (chatMessageType) {
			case PUBLICCHAT:
			case MODCHAT:
			case AUTOTYPER:
			case PRIVATECHAT:
			case MODPRIVATECHAT:
			case FRIENDSCHAT:
				if (shouldBlock(playerName))
				{
					// Block the Message
					intStack[intStackSize - 3] = 0;
				}
		}
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		Actor actor = event.getActor();

		if (!(actor instanceof Player))
		{
			return;
		}

		if (!shouldBlock(actor.getName()))
		{
			return;
		}

		event.getActor().setOverheadText("");
	}

	private boolean shouldBlock(final String playerName)
	{
		boolean isMessageFromSelf = playerName.equals(client.getLocalPlayer().getName());

		if (isMessageFromSelf)
		{
			return false;
		}

		if (!isIronman(playerName))
		{
			return false;
		}

		ArrayList<String> allowList = getAllowlist();
		String sanitizedName = Text.standardize(playerName);
		log.debug("Sanitized contains: {}", sanitizedName);
		log.debug("Allowlist contains: {}", allowList);
		if (allowList.contains(sanitizedName))
		{
			return false;
		}

		if (!config.filterFriends() && client.isFriended(playerName, false))
		{
			return false;
		}

		return true;
	}

	private ArrayList<String> getAllowlist()
	{
		final String allowlist = config.allowlist();

		if (allowlist.isEmpty())
		{
			return new ArrayList<>();
		}

		return new ArrayList<>(Text.fromCSV(allowlist));
	}

	private static boolean isIronman(final String playerName)
	{
		ArrayList<IconID> IronmanIcons = new ArrayList<IconID>(){{
			add(IconID.IRONMAN);
			add(IconID.ULTIMATE_IRONMAN);
			add(IconID.HARDCORE_IRONMAN);
			add(IconID.LEAGUE);
		}};

		for (IconID icon : IronmanIcons)
		{
			if (playerName.contains(icon.toString()))
			{
				log.debug("UserName {} is an ironman!", playerName);
				return true;
			}
		}

		return false;
	}

	@Provides
	IronmanMuterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(IronmanMuterConfig.class);
	}
}
