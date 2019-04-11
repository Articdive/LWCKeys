package de.articdive.lwckeys.configuration;

import de.articdive.enum_to_yaml.interfaces.ConfigurationEnum;

public enum KeysConfiguration implements ConfigurationEnum {
    CONFIG_VERSION("config_version", "", "# Current version of LWCKeys.", "# Please don't change this value!"),
    NO_PERMISSION_MESSAGE("no_permission_message", "&cYou don't have permission to use this key!", "# Message for the player if it has no permission to use a key."),
    UNLOCK_OWN_CHEST_MESSAGE("unlock_own_chest_message", "&cWhy would you unlock something you own?", "# Message for the player if it tries to unlock it's own chest."),
    OWNER_ONLINE_MESSAGE("owner_online_message", "&c{owner} is online, container is not unlocked", "# Message for the player if it tries to unlock a chest, whose owner is online."),
    UNLOCK_MESSAGE("unlock_message", "&aYou have unlocked the chest belonging to {owner}.", "# Successful unlock message."),
    TIME_REQUIRED_MESSAGE("time_required_message", "&cYou must wait {time} before you can unlock this container!", "# Unsuccessful unlock message."),
    NO_PERMISSION_CMD_MESSAGE("no_permission_cmd_message", "&cYou don't have permission to use this command!", "# Message for the player if it has no permission to run a command."),
    SPECIFY_KEY_MESSAGE("specify_key_message", "&cYou need to specify a key!", "# Message when a player uses /lwckeys give , without a key."),
    SPECIFY_VALID_KEY_MESSAGE("specify_valid_key_message", "&c{input} is not a valid key name!", "# Message when a player uses /lwckeys give (keyname), and there's no key with that name."),
    SPECIFY_PLAYER_MESSAGE("specify_player_message", "&cYou need to specify a player to give the key to", "# Message when console tries to give a key without specifying a player."),
    PLAYER_NOT_FOUND_MESSAGE("player_not_found_message", "&cPlayer {input} couldn't be found or is not online!", "# When a player-name which can't be found is inputted."),
    NOT_A_VALID_INTEGER_MESSAGE("not_a_valid_integer_message", "&c{input} is not a valid integer.", "# When a number is not an integer or not readable."),
    TOWNY_NO_PERMISSION("towny_no_permission_message", "&cYou don't have permission to use this key in this towny region!", "# Message for the player if it has no permission to use a key in the towny region"),
    GIVE_KEY_MESSAGE("give_key_message", "&aYou have given {player}, {amount} of the key: {keyname}."),
    KEYS("keys", "",
            "# You can add keys here.",
            "# The name of the key is the header of the config section.",
            "# displayname - Name of the item in minecraft.",
            "# lore - (List) Description of the item",
            "# material - item type (please use https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)",
            "# enchantments - (List) use the input Enchantment,level e.g 'SHARPNESS,5'",
            "# timeRequirement - Amount of time required per key, (for no time don't add it at all) use the syntax: 1 year, 2 months, 1 week, 5 days, 1 hour, 8 minutes",
            "# If you have trouble adding enchantments by name use the name ID of https://minecraft.gamepedia.com/Enchanting#IDs",
            "# permissionsRequired - Does the key require a permission (either lwckeys.use or lwckeys.use.(name)",
            "# removeProtection - Should protections be removed, or should the protection be transfered to the user?",
            "# sinceOwnerOnline - Should the unlock-time on keys be based on the player's login or the chest's last open time?",
            "# townyWilderness - Should the key unlock protections in the towny wilderness.",
            "# townyClaimed - Should the key unlock in ANY towny claimed protection.",
            "# townyClaimedOwn - Should the key unlock towny claimed protection that the player is a part of (resident of a town)"
    );


    private String path;
    private Object defaultValue;
    private String[] comments;

    KeysConfiguration(String path, Object defaultValue, String... comments) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.comments = comments;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String[] getComments() {
        return comments;
    }
}
