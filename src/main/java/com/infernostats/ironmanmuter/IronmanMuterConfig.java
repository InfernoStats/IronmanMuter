package com.infernostats.ironmanmuter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ironmenmuter")
public interface IronmanMuterConfig extends Config
{
    @ConfigItem(
            keyName = "allowlist",
            name = "Allowlist",
            description = "Comma separated list of ironmen that will not be muted",
            position = 1
    )
    default String allowlist()
    {
        return "";
    }

    @ConfigItem(
            keyName = "filterFriends",
            name = "Filter Friends",
            description = "Block messages from ironmen on your friends list",
            position = 2
    )
    default boolean filterFriends()
    {
        return false;
    }
}