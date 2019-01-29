package de.articdive.lwckeys;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import de.articdive.enum_to_yaml.EnumConfiguration;
import de.articdive.enum_to_yaml.EnumConfigurationBuilder;
import de.articdive.enum_to_yaml.yaml.file.interfaces.ConfigurationSection;
import de.articdive.lwckeys.commands.LWCKeysCommand;
import de.articdive.lwckeys.configuration.KeysConfiguration;
import de.articdive.lwckeys.listeners.PlayerListener;
import de.articdive.lwckeys.objects.LWCKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.Period;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class LWCKeys extends JavaPlugin {
    private final List<LWCKey> lwcKeys = new ArrayList<>();
    private LWC lwc;
    private EnumConfiguration keysConfiguration;

    @Override
    public void onEnable() {
        if (!(this.getServer().getPluginManager().isPluginEnabled("LWC"))) {
            getLogger().info("LWCKeys couldn't find LWC, please get LWC before running LWCKeys.");
            this.getPluginLoader().disablePlugin(this);
        } else {
            Plugin lwcp = Bukkit.getPluginManager().getPlugin("LWC");
            lwc = ((LWCPlugin) lwcp).getLWC();
            getLogger().info("Hooked into LWC version " + lwcp.getDescription().getVersion() + " successfully");
        }
        // Copy defaults;
        saveDefaultConfig();
        // EnumConfiguration
        keysConfiguration = new EnumConfigurationBuilder(new File(this.getDataFolder(), "config.yml"), KeysConfiguration.class).build();
        keysConfiguration.set(KeysConfiguration.CONFIG_VERSION, this.getDescription().getVersion());
        loadLWCKeys();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        LWCKeysCommand lwcKeysCommand = new LWCKeysCommand();
        getCommand("lwckeys").setExecutor(lwcKeysCommand);
        getCommand("lwckeys").setTabCompleter(lwcKeysCommand);
    }

    @Override
    public void onDisable() {
        getLogger().info("LWCKeys has been disabled!");
    }

    @SuppressWarnings("unchecked")
    private void loadLWCKeys() {
        ConfigurationSection outersection = keysConfiguration.getConfigurationSection(KeysConfiguration.KEYS);
        for (String name : outersection.getValues(false).keySet()) {
            ConfigurationSection innersection = outersection.getConfigurationSection(name);
            String displayName = null;
            List<String> lore = new ArrayList<>();
            Material material = Material.TRIPWIRE_HOOK;
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            Period timeRequirement = Period.ZERO;
            boolean requiresPermission = false;
            boolean removeProtection = true;
            boolean sinceOwnerOnline = true;
            for (String keyAttribute : innersection.getValues(false).keySet()) {
                switch (keyAttribute.toLowerCase()) {
                    case "name":
                    case "displayname": {
                        displayName = ChatColor.translateAlternateColorCodes('&', (String) innersection.get(keyAttribute));
                        break;
                    }
                    case "description":
                    case "lore": {
                        List<String> uncolouredLore = (List<String>) innersection.get(keyAttribute);
                        for (String s : uncolouredLore) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                        break;
                    }
                    case "itemtype":
                    case "item":
                    case "material": {
                        material = Material.valueOf((String) innersection.get(keyAttribute));
                        break;
                    }
                    case "enchants":
                    case "enchantments": {
                        List<String> enchants_levels = (List<String>) innersection.get(keyAttribute);
                        for (String enchant_level : enchants_levels) {
                            String[] splitenchant_level = enchant_level.split(",");
                            if (splitenchant_level.length <= 2 && splitenchant_level.length != 0) {
                                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(splitenchant_level[0].toLowerCase()));
                                if (enchantment == null) {
                                    getLogger().log(Level.SEVERE, "Enchantment: " + splitenchant_level[0] + " couldn't be found!");
                                    continue;
                                }
                                try {
                                    int level = Integer.parseInt(splitenchant_level[1]);
                                    enchantments.put(enchantment, level);
                                } catch (NumberFormatException iae) {
                                    getLogger().log(Level.SEVERE, "Couldn't create enchantment level out of: " + splitenchant_level[1] + ".");
                                }
                            }
                        }
                        break;
                    }
                    case "unlocktime":
                    case "unlock_time":
                    case "time_required":
                    case "timerequired":
                    case "timerequirement":
                    case "time_requirement":
                    case "time": {
                        String timeStrings = (String) innersection.get(keyAttribute);
                        String[] splitTimeStrings = timeStrings.split(",");
                        for (String splitTimeString : splitTimeStrings) {
                            String periodAmountString = splitTimeString.trim().split(" ")[0];
                            String periodType = splitTimeString.trim().split(" ")[1];
                            int periodAmount = 1;
                            try {
                                periodAmount = Integer.parseInt(periodAmountString);
                            } catch (NumberFormatException iae) {
                                getLogger().log(Level.SEVERE, "Couldn't create period amount out of: " + periodAmountString + ".");
                            }
                            switch (periodType.toLowerCase().trim()) {
                                case "y":
                                case "year":
                                case "years": {
                                   timeRequirement = timeRequirement.plusYears(periodAmount);
                                    break;
                                }
                                case "month":
                                case "months": {
                                    timeRequirement = timeRequirement.plusMonths(periodAmount);
                                    break;
                                }
                                case "w":
                                case "week":
                                case "weeks": {
                                    timeRequirement = timeRequirement.plusWeeks(periodAmount);
                                    break;
                                }
                                case "d":
                                case "day":
                                case "days": {
                                    timeRequirement = timeRequirement.plusDays(periodAmount);
                                    break;
                                }
                                case "h":
                                case "hour":
                                case "hours": {
                                    timeRequirement = timeRequirement.plusHours(periodAmount);
                                    break;
                                }
                                case "m":
                                case "minute":
                                case "minutes": {
                                    timeRequirement = timeRequirement.plusMinutes(periodAmount);
                                    break;
                                }
                                case "s":
                                case "second":
                                case "seconds": {
                                    timeRequirement = timeRequirement.plusSeconds(periodAmount);
                                    break;
                                }
                            }
                        }
                    }
                    case "permission_required":
                    case "permissions_required":
                    case "requires_permissions":
                    case "requires_permission":
                    case "permissions":
                    case "permission": {
                        requiresPermission = Boolean.parseBoolean((String) innersection.get(keyAttribute));
                    }
                    case "sinceowneronline":
                    case "since_owner_online": {
                        sinceOwnerOnline = Boolean.parseBoolean((String) innersection.get(keyAttribute));
                    }
                    case "remove_protection":
                    case "removeprotection": {
                        removeProtection = Boolean.parseBoolean((String) innersection.get(keyAttribute));
                    }
                }
            }
            lwcKeys.add(new LWCKey(name.toLowerCase(), displayName, lore, material, enchantments, timeRequirement, requiresPermission, sinceOwnerOnline, removeProtection));
        }
    }

    public List<LWCKey> getLwcKeys() {
        return lwcKeys;
    }

    public LWC getLWC() {
        return lwc;
    }

    public EnumConfiguration getKeysConfiguration() {
        return keysConfiguration;
    }
}
